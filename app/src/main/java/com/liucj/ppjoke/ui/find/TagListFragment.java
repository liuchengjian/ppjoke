package com.liucj.ppjoke.ui.find;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.liucj.libnetwork.ApiResponse;
import com.liucj.libnetwork.ApiService;
import com.liucj.libnetwork.JsonCallBack;
import com.liucj.libnetwork.Request;
import com.liucj.ppjoke.R;
import com.liucj.ppjoke.model.TagList;
import com.liucj.ppjoke.ui.adapter.TagListAdapter;
import com.liucj.ppjoke.ui.event.ChangePageEvent;
import com.liucj.ppjoke.ui.view.BaseListFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TagListFragment extends BaseListFragment<TagList, BaseViewHolder> {
    public static final String KEY_TAG_TYPE = "tag_type";
    private String tagType;
    private TagListAdapter tagListAdapter;
    private List<TagList> data= new ArrayList<>();

    public static TagListFragment newInstance(String tagType) {

        Bundle args = new Bundle();
        args.putString(KEY_TAG_TYPE, tagType);
        TagListFragment fragment = new TagListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void afterCreateView() {
        if (TextUtils.equals(tagType, "onlyFollow")) {
            mEmptyView.setTitle(getString(R.string.tag_list_no_follow));
            mEmptyView.setButton(getString(R.string.tag_list_no_follow_button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new ChangePageEvent());
                }
            });
        }
        mRecyclerView.removeItemDecorationAt(0);
        loadData(true);
    }

    @Override
    public BaseQuickAdapter<TagList, BaseViewHolder> getAdapter() {
        tagType = getArguments().getString(KEY_TAG_TYPE);
        tagListAdapter  = new TagListAdapter(R.layout.layout_tag_list_item,data);
        return tagListAdapter;
    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        loadData(false);
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        loadData(true);
    }

    private void loadData(boolean isRefresh){
        List<TagList> currentList = tagListAdapter.getData();
        long tagId = currentList.isEmpty()?0 : currentList.get(currentList.size()-1).tagId;
        int offset = 0;
        if(isRefresh){
            offset = 0;
        }else {
            offset +=data.size();
        }
       Request request = ApiService.get("/tag/queryTagList")
                .addParam("userId", "1631678065")
                .addParam("tagId", tagId)
                .addParam("tagType", tagType)
                .addParam("pageCount", 10)
                .addParam("offset", offset)
                .responseType(new TypeReference<ArrayList<TagList>>() {
                }.getType());
        request.execute(new JsonCallBack() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onSuccess(ApiResponse response) {
                List<TagList> list = (List<TagList>) response.body;
                if (isRefresh) {
                    data.clear();
                }
                data.addAll(list);
                ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
//                        tagListAdapter.setDiffNewData(data);
                        submitList(data);
                    }
                });
            }
        });
   }
}
