package com.liucj.ppjoke.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.android.material.button.MaterialButton;
import com.liucj.libcommon.AppGlobals;
import com.liucj.libcommon.utils.PixUtils;
import com.liucj.libcommon.utils.StatusBar;
import com.liucj.libcommon.utils.TimeUtils;
import com.liucj.libnetwork.ApiResponse;
import com.liucj.libnetwork.ApiService;
import com.liucj.libnetwork.JsonCallBack;
import com.liucj.libnetwork.Request;
import com.liucj.ppjoke.R;
import com.liucj.ppjoke.model.Comment;
import com.liucj.ppjoke.model.Feed;
import com.liucj.ppjoke.ui.adapter.FeedDetailAdapter;
import com.liucj.ppjoke.ui.view.FullScreenPlayerView;
import com.liucj.ppjoke.ui.view.PPImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String KEY_FEED = "key_feed";
    public static final String KEY_CATEGORY = "key_category";
    private Feed feed;
    private FeedDetailAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();
    private FullScreenPlayerView playerView;
    private CommentDialog commentDialog;

    public static void startFeedDetailActivity(Context context, Feed item, String category) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        intent.putExtra(KEY_FEED, item);
        intent.putExtra(KEY_CATEGORY, category);
        context.startActivity(intent);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.fitSystemBar(this);
        feed = (Feed) getIntent().getSerializableExtra(KEY_FEED);
        String category = getIntent().getStringExtra(KEY_CATEGORY);
        if (feed == null) {
            return;
        }
        TextView inputView;
        if (feed.itemType == Feed.TYPE_IMAGE_TEXT) {
            setContentView(R.layout.activity_feed_detail_type_image);
            FrameLayout titleLayout = findViewById(R.id.title_layout);
            View authorInfoLayout = findViewById(R.id.author_info_layout);
            MaterialButton mBtnFollow = findViewById(R.id.mBtnFollow);
            TextView title = findViewById(R.id.title);
            TextView createTime = findViewById(R.id.create_time);
            inputView = findViewById(R.id.input_view);

            TextView authorName = findViewById(R.id.author_name);
            PPImageView authorAvatar = findViewById(R.id.author_avatar);
            setAuthorInfoView(authorName, createTime, authorAvatar, mBtnFollow);
            RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setItemAnimator(null);
            adapter = new FeedDetailAdapter(R.layout.layout_feed_comment_list_item, commentList);
            adapter.addChildClickViewIds(R.id.comment_delete);
            adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                    switch (view.getId()){
                        case R.id.comment_delete:
                            deleteFeedComment(FeedDetailActivity.this,commentList.get(position));
                            break;
                    }
                }
            });
            mRecyclerView.setAdapter(adapter);
            loadData();


            View mHeaderView = getLayoutInflater().inflate(R.layout.layout_feed_detail_type_image_header, mRecyclerView, false);
            TextView feedsText = mHeaderView.findViewById(R.id.feeds_text);
            feedsText.setMaxLines(1000);
            feedsText.setText(feed.feeds_text);
            TextView headerAuthorName = mHeaderView.findViewById(R.id.author_name);
            PPImageView headerAvatar = mHeaderView.findViewById(R.id.author_avatar);
            MaterialButton headerFollow = mHeaderView.findViewById(R.id.mBtnFollow);
            TextView headerCreateTime = mHeaderView.findViewById(R.id.create_time);
            setAuthorInfoView(headerAuthorName, headerCreateTime, headerAvatar, headerFollow);

            PPImageView headerImage = mHeaderView.findViewById(R.id.header_image);
            headerImage.bindData(feed.width, feed.height, feed.width > feed.height ? 0 : 16, feed.cover);


            adapter.addHeaderView(mHeaderView.getRootView());
            mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int offset = mRecyclerView.computeVerticalScrollOffset();
                    // 如果是垂直滑动，获取垂直滑动距离
//                    Log.e("onScrollChange", "offset " + offset+" distance"+titleLayout.getMeasuredHeight());
                    boolean visible = offset >= titleLayout.getMeasuredHeight();
                    authorInfoLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
                    title.setVisibility(visible ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            //视频
            setContentView(R.layout.layout_feed_detail_type_video);
            View authorInfoView = findViewById(R.id.author_info);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) authorInfoView.getLayoutParams();
            params.setBehavior(new ViewAnchorBehavior(R.id.player_view));
            playerView = findViewById(R.id.player_view);
            CoordinatorLayout coordinator = findViewById(R.id.coordinator);
            playerView.bindData(category, feed.width, feed.height, feed.cover, feed.url);
            View bottomInteraction = findViewById(R.id.bottom_interaction);
            View fullscreenAuthorInfo = findViewById(R.id.fullscreen_author_info);
            inputView = findViewById(R.id.input_view);

            RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setItemAnimator(null);
            adapter = new FeedDetailAdapter(R.layout.layout_feed_comment_list_item, commentList);
            mRecyclerView.setAdapter(adapter);
            adapter.addChildClickViewIds(R.id.comment_delete);
            adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                    switch (view.getId()){
                        case R.id.comment_delete:
                            deleteFeedComment(FeedDetailActivity.this,commentList.get(position));
                            break;
                    }
                }
            });
            loadData();
            View mHeaderView = getLayoutInflater().inflate(R.layout.layout_feed_detail_type_video_header, mRecyclerView, false);
            TextView feedsText = mHeaderView.findViewById(R.id.feeds_text);
            feedsText.setMaxLines(1000);
            feedsText.setText(feed.feeds_text);

            adapter.addHeaderView(mHeaderView.getRootView());
            playerView.post(new Runnable() {
                @Override
                public void run() {
                    boolean fullscreen = playerView.getBottom() >= coordinator.getBottom();
                    fullscreenAuthorInfo.setVisibility(fullscreen ? View.VISIBLE : View.GONE);
                    //底部互动区域的高度
                    int inputHeight = bottomInteraction.getRootView().getMeasuredHeight();
                    //播放控制器的高度
                    int ctrlViewHeight = playerView.getPlayController().getMeasuredHeight();
                    //播放控制器的bottom值
                    int bottom = playerView.getPlayController().getBottom();
                    //全屏播放时，播放控制器需要处在底部互动区域的上面
                    playerView.getPlayController().setY(fullscreen ? bottom - inputHeight - ctrlViewHeight
                            : bottom - ctrlViewHeight);
                    inputView.setBackgroundResource(fullscreen ? R.drawable.bg_edit_view2 : R.drawable.bg_edit_view);

                }
            });
        }
        ImageView actionClose = findViewById(R.id.action_close);
        actionClose.setOnClickListener(this);
        inputView.setOnClickListener(this);
    }

    private  void toggleCommentLikeInternal(Comment comment) {

        ApiService.get("/ugc/toggleCommentLike")
                .addParam("commentId", comment.commentId)
                .addParam("userId","1631678065")
                .execute(new JsonCallBack<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBooleanValue("hasLiked");
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }


    //删除某个帖子的一个评论
    public  void deleteFeedComment(Context context, Comment comment) {
        new AlertDialog.Builder(context)
                .setNegativeButton("删除", (dialog, which) -> {
                    dialog.dismiss();
                    deleteFeedCommentInternal(comment);
                }).setPositiveButton("取消", (dialog, which) -> dialog.dismiss()).setMessage("确定要删除这条评论吗？").create().show();

    }

    private  void deleteFeedCommentInternal(Comment comment) {
        ApiService.get("/comment/deleteComment")
                .addParam("userId", "1631678065")
                .addParam("commentId",comment.commentId)
                .addParam("itemId", comment.itemId)
                .execute(new JsonCallBack<JSONObject>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean result = response.body.getBooleanValue("result");
                            if(result){
                                commentList.remove(comment);
                                ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AppGlobals.getApplication(), "评论删除成功", Toast.LENGTH_SHORT).show();
                                        if (adapter != null) {
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }else {
                                showToast("删除失败");
                            }
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private static void showToast(String message) {
        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppGlobals.getApplication(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setViewAppearance(boolean fullscreen) {


    }

    private void setAuthorInfoView(TextView authorName, TextView createTime, PPImageView authorAvatar, MaterialButton mBtnFollow) {
        authorName.setText(feed.author.name);
        authorAvatar.setImageUrl(feed.author.avatar, true);
        mBtnFollow.setText(feed.author.hasFollow ? getText(R.string.has_follow) : getText(R.string.unfollow));
//        int[][] states = new int[2][];
//        states[0] = new int[]{android.R.attr.state_selected};
//        states[1] = new int[]{};
//        int[] colors = new int[]{R.color.color_theme, R.color.color_theme};
//        ColorStateList stateList = new ColorStateList(states, colors);
//        mBtnFollow.setBackgroundTintList(stateList);
        createTime.setText(TimeUtils.calculate(feed.createTime));
    }

    private void loadData() {
        Request response = ApiService.get("/comment/queryFeedComments")
                .addParam("id", 0)
                .addParam("itemId", feed.itemId)
                .addParam("userId", "1631678065")
                .addParam("pageCount", 100)
                .responseType(new TypeReference<List<Comment>>() {
                }.getType());
        response.execute(new JsonCallBack() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onSuccess(ApiResponse response) {
                List<Comment> list = (List<Comment>) response.body;
                commentList.clear();
                commentList.addAll(list);
                ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (commentDialog != null) {
            commentDialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_close:
                finish();
                break;
            case R.id.input_view:
                if (commentDialog == null) {
                    commentDialog = CommentDialog.newInstance(feed.itemId);
                }
                commentDialog.setCommentAddListener(new CommentDialog.commentAddListener() {
                    @Override
                    public void onAddComment(Comment comment) {
                        commentList.add(0, comment);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                commentDialog.show(getSupportFragmentManager(), "comment_dialog");
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerView != null) {
            playerView.inActive();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerView != null) {
            playerView.onActive();
        }
    }

    @Override
    public void onBackPressed() {
        if (playerView != null) {
            playerView.inActive();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}