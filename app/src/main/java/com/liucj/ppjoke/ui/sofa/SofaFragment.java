package com.liucj.ppjoke.ui.sofa;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liucj.libnavannotation.FragmentDestination;
import com.liucj.ppjoke.ui.view.BaseListFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.jetbrains.annotations.NotNull;

@FragmentDestination(pageUrl = "main/tabs/sofa", asStarter = false)
public class SofaFragment extends BaseListFragment {
    @Override
    protected void afterCreateView() {

    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return null;
    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {

    }
}