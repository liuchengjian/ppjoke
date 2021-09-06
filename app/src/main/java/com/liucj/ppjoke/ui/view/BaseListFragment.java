package com.liucj.ppjoke.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.liucj.libcommon.view.EmptyView;
import com.liucj.ppjoke.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

public abstract class BaseListFragment<T, M extends BaseViewHolder> extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    protected RecyclerView mRecyclerView;
    protected SmartRefreshLayout mSmartRefreshLayout;
    protected EmptyView mEmptyView;
    private BaseQuickAdapter<T, M> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_refresh_view, container, false);
        mSmartRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mEmptyView = view.findViewById(R.id.empty_view);
        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setEnableLoadMore(true);

        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
        adapter = getAdapter();
        if (adapter != null) {
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setItemAnimator(null);//取消动画
            DividerItemDecoration decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
            decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
            mRecyclerView.addItemDecoration(decoration);
        }
        afterCreateView();
        return view;

    }

    public void submitList(List<T> list) {
//        if (list.size() > 0) {
//            adapter.setList(list);
//        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
        finishRefresh(list.size() > 0);
    }

    public void finishRefresh(boolean hasData) {
        RefreshState state = mSmartRefreshLayout.getState();
        if (state.isFooter && state.isOpening) {
            mSmartRefreshLayout.finishLoadMore();
        } else if (state.isHeader && state.isOpening) {
            mSmartRefreshLayout.finishRefresh();
        }
        if (hasData) {
            mEmptyView.setVisibility(View.GONE);
        } else {
//            adapter.setEmptyView(mEmptyView);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }


    protected abstract void afterCreateView();

    public abstract BaseQuickAdapter<T, M> getAdapter();

}
