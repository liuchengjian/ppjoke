package com.liucj.ppjoke.ui.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.button.MaterialButton;
import com.liucj.libcommon.view.CornerFrameLayout;
import com.liucj.ppjoke.R;
import com.liucj.ppjoke.model.Feed;
import com.liucj.ppjoke.ui.activity.CaptureActivity;
import com.liucj.ppjoke.ui.activity.FeedDetailActivity;
import com.liucj.ppjoke.ui.view.ListPlayerView;
import com.liucj.ppjoke.ui.view.PPImageView;
import com.liucj.ppjoke.utils.StringConvert;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeAdapter extends BaseMultiItemQuickAdapter<Feed, BaseViewHolder> {
    private String mCategory;
    public HomeAdapter(List<Feed> data,String category) {
        super(data);
        this.mCategory = category;
        // 绑定 layout 对应的 type
        addItemType(Feed.TYPE_IMAGE_TEXT, R.layout.layout_feed_type_image);
        addItemType(Feed.TYPE_VIDEO, R.layout.layout_feed_type_video);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, Feed feed) {
        //头像
        PPImageView avatar = helper.getView(R.id.avatar);
        avatar.setImageUrl(feed.author.avatar, true);
        //昵称
        helper.setText(R.id.author_name, feed.author.name);
        TextView feeds_text = helper.getView(R.id.feeds_text);
        //帖子文本
        feeds_text.setText(feed.feeds_text);
        feeds_text.setVisibility(TextUtils.isEmpty(feed.feeds_text) ? View.GONE : View.VISIBLE);
        CornerFrameLayout layout = helper.getView(R.id.layout);
        layout.setVisibility(feed.topComment == null ? View.GONE : View.VISIBLE);
        if (feed.topComment != null) {
            //图片文本特有
            TextView commentText = helper.getView(R.id.commentText);
            commentText.setText(TextUtils.isEmpty(feed.topComment.commentText)
                    ? "" : feed.topComment.commentText);
            commentText.setVisibility(TextUtils.isEmpty(feed.topComment.commentText)
                    ? View.GONE : View.VISIBLE);
            PPImageView commentImg = helper.getView(R.id.commentImg);
            commentImg.setImageUrl(feed.topComment.imageUrl, false);
            commentImg.setVisibility(TextUtils.isEmpty(feed.topComment.imageUrl) ? View.GONE : View.VISIBLE);

            ImageView icon_video_play = helper.getView(R.id.icon_video_play);
            icon_video_play.setVisibility(TextUtils.isEmpty(feed.topComment.videoUrl)?View.GONE:View.VISIBLE);
        }

        TextView likeCount = helper.getView(R.id.likeCount);
        likeCount.setText(feed.ugc.likeCount > 0 ? StringConvert.convertFeedUgc(feed.ugc.likeCount) :
                getContext().getString(R.string.like));
        likeCount.setTextColor(feed.ugc.hasLiked
                ? getContext().getResources().getColor(R.color.color_theme)
                : getContext().getResources().getColor(R.color.color_3d3));
        ImageView likeCountImg = helper.getView(R.id.likeCountImg);
        likeCountImg.setImageDrawable(feed.ugc.hasLiked ?
                getContext().getResources().getDrawable(R.drawable.icon_cell_like)
                : getContext().getResources().getDrawable(R.drawable.icon_cell_like)
        );

        MaterialButton activityText = helper.getView(R.id.activityText);
        activityText.setText(String.valueOf(feed.activityText));
        activityText.setVisibility(TextUtils.isEmpty(feed.activityText) ? View.GONE : View.VISIBLE);

        MaterialButton like = helper.getView(R.id.like);

        like.setText(feed.ugc.likeCount > 0 ? StringConvert.convertFeedUgc(feed.ugc.likeCount) : getContext().getString(R.string.like));
        like.setTextColor(feed.ugc.hasLiked
                ? getContext().getResources().getColor(R.color.color_theme)
                : getContext().getResources().getColor(R.color.color_3d3)
        );
        like.setIcon(feed.ugc.hasLiked ?
                getContext().getResources().getDrawable(R.drawable.icon_cell_like)
                : getContext().getResources().getDrawable(R.drawable.icon_cell_like)
        );
        like.setIconTint(ColorStateList.valueOf(feed.ugc.hasLiked ?
                getContext().getResources().getColor(R.color.color_theme)
                : getContext().getResources().getColor(R.color.color_3d3)));

        MaterialButton diss = helper.getView(R.id.diss);

        diss.setTextColor(feed.ugc.hasdiss
                ? getContext().getResources().getColor(R.color.color_theme)
                : getContext().getResources().getColor(R.color.color_3d3)
        );
        diss.setIcon(feed.ugc.hasLiked ?
                getContext().getResources().getDrawable(R.drawable.icon_cell_dissed)
                : getContext().getResources().getDrawable(R.drawable.icon_cell_diss)
        );
        diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.startActivityForResult((Activity) getContext());
            }
        });
        diss.setIconTint(ColorStateList.valueOf(feed.ugc.hasLiked ?
                getContext().getResources().getColor(R.color.color_theme)
                : getContext().getResources().getColor(R.color.color_3d3)));


        MaterialButton comment = helper.getView(R.id.comment);

        comment.setText(feed.ugc.commentCount > 0 ? StringConvert.convertFeedUgc(feed.ugc.commentCount) : getContext().getString(R.string.feed_comment));

        MaterialButton share = helper.getView(R.id.share);
        share.setText(feed.ugc.shareCount > 0 ? StringConvert.convertFeedUgc(feed.ugc.shareCount) : getContext().getString(R.string.share));

        // 根据返回的 type 分别设置数据
        switch (helper.getItemViewType()) {
            case Feed.TYPE_IMAGE_TEXT:

                PPImageView cover = helper.getView(R.id.feed_image);
                cover.bindData(feed.width, feed.height, 16, feed.cover);
                break;
            case Feed.TYPE_VIDEO:

                ListPlayerView listPlayerView = helper.getView(R.id.list_player_view);
                listPlayerView.bindData(mCategory, feed.width, feed.height, feed.cover, feed.url);

                break;
            default:
                break;
        }
    }

}
