package com.liucj.ppjoke.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.liucj.libnavannotation.FragmentDestination;
import com.liucj.libnetwork.ApiResponse;
import com.liucj.libnetwork.ApiService;
import com.liucj.libnetwork.JsonCallBack;
import com.liucj.libnetwork.Request;
import com.liucj.ppjoke.R;
import com.liucj.ppjoke.exoplayer.PageListPlayDetector;
import com.liucj.ppjoke.model.Feed;
import com.liucj.ppjoke.ui.adapter.HomeAdapter;
import com.liucj.ppjoke.ui.view.BaseListFragment;
import com.liucj.ppjoke.ui.view.ListPlayerView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends BaseListFragment<Feed, BaseViewHolder> {
    public static final String KEY_FEED_TYPE = "tag_feed_list";
    private PageListPlayDetector playDetector;
    private List<Feed> feedList = new ArrayList<>();

    @Override
    protected void afterCreateView() {
        loadData(true);
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playDetector = new PageListPlayDetector(this, mRecyclerView);
    }

    @Override
    public BaseQuickAdapter<Feed, BaseViewHolder> getAdapter() {
        HomeAdapter adapter = new HomeAdapter(feedList, KEY_FEED_TYPE) {
            @Override
            public void onViewAttachedToWindow(@NotNull BaseViewHolder holder) {
                if (holder.getItemViewType() == Feed.TYPE_VIDEO) {
                    ListPlayerView listPlayerView = holder.getView(R.id.list_player_view);
                    playDetector.addTarget(listPlayerView);
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
                if (holder.getItemViewType() == Feed.TYPE_VIDEO) {
                    ListPlayerView listPlayerView = holder.getView(R.id.list_player_view);
                    playDetector.removeTarget(listPlayerView);
                }
            }
        };
        return adapter;
    }

    private void loadData(boolean isRefresh) {
        Request request = null;
        if (isRefresh) {
            request = ApiService.get("/feeds/queryHotFeedsList")
                    .addParam("feedType", "all")
                    .addParam("userId", "1630657969")
                    .addParam("feedId", "")
                    .addParam("pageCount", 10)
                    .responseType(new TypeReference<ArrayList<Feed>>() {
                    }.getType());
        } else {
            request = ApiService.get("/feeds/queryHotFeedsList")
                    .addParam("feedType", "text")
                    .addParam("userId", "1630657969")
                    .addParam("feedId", "")
                    .addParam("pageCount", 10)
                    .responseType(new TypeReference<ArrayList<Feed>>() {
                    }.getType());
        }

        request.execute(new JsonCallBack() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onSuccess(ApiResponse response) {
                List<Feed> list = (List<Feed>) response.body;
                if (isRefresh) {
                    feedList.clear();
                }
                feedList.addAll(list);
                ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        submitList(list);
                    }
                });

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onError(ApiResponse response) {
                super.onError(response);
                ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        submitList(feedList);
                    }
                });
            }

            @Override
            public void onCacheSuccess(ApiResponse response) {
                List<Feed> list = (List<Feed>) response.body;
                //下面的不可取，否则会报
                // java.lang.IllegalStateException: callback.onResult already called, cannot call again.
                //if (response.body != null) {
                //  callback.onResult(response.body);
                // }
            }
        });

    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        loadData(false);
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        //feeds/queryHotFeedsList
        loadData(true);
    }
}