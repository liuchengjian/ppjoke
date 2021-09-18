package com.liucj.ppjoke.ui.adapter;

import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.button.MaterialButton;
import com.liucj.libcommon.utils.PixUtils;
import com.liucj.libcommon.utils.TimeUtils;
import com.liucj.ppjoke.R;
import com.liucj.ppjoke.model.Comment;
import com.liucj.ppjoke.model.Feed;
import com.liucj.ppjoke.ui.activity.CommentDialog;
import com.liucj.ppjoke.ui.view.PPImageView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FeedDetailAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {
    public FeedDetailAdapter(int layoutResId, List<Comment> list) {
        super(layoutResId, list);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, Comment comment) {
        PPImageView avatar = helper.getView(R.id.author_avatar);
        if(comment.author!=null){
            avatar.setImageUrl(comment.author.avatar, true);
            //昵称
            helper.setText(R.id.author_name, comment.author.name);
        }
        helper.setText(R.id.create_time, TimeUtils.calculate(comment.createTime));
        TextView commentLike = helper.getView(R.id.comment_like);
        if (comment.ugc != null) {
            commentLike.setText(String.valueOf(comment.ugc.likeCount));
            commentLike.setTextColor(comment.ugc.hasLiked
                    ? getContext().getResources().getColor(R.color.color_theme)
                    : getContext().getResources().getColor(R.color.color_3d3));
            Drawable drawable;
            if (comment.ugc.hasLiked) {
                drawable = getContext().getResources().getDrawable(R.drawable.icon_cell_liked);
            } else {
                drawable = getContext().getResources().getDrawable(R.drawable.icon_cell_like);
            }
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            commentLike.setCompoundDrawables(null, null, drawable, null);

        }
        ImageView commentDelete = helper.getView(R.id.comment_delete);
        helper.setText(R.id.comment_text, comment.commentText);

        FrameLayout commentExt = helper.getView(R.id.comment_ext);
        PPImageView commentCover = helper.getView(R.id.comment_cover);
        MaterialButton labelAuthor = helper.getView(R.id.label_author);
        ImageView videoIcon = helper.getView(R.id.video_icon);
//        boolean self = comment.author == null ? false : UserManager.get().getUserId() == comment.author.userId;
        boolean self = true;
        labelAuthor.setVisibility(self ? View.VISIBLE : View.GONE);
        commentDelete.setVisibility(self ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(comment.imageUrl)) {
            commentExt.setVisibility(View.VISIBLE);
            commentCover.setVisibility(View.VISIBLE);
            commentCover.bindData(comment.width, comment.height, 0, PixUtils.dp2px(200), PixUtils.dp2px(200), comment.imageUrl);
            if (!TextUtils.isEmpty(comment.videoUrl)) {
                videoIcon.setVisibility(View.VISIBLE);
            } else {
                videoIcon.setVisibility(View.GONE);
            }
        } else {
            commentCover.setVisibility(View.GONE);
            videoIcon.setVisibility(View.GONE);
            commentExt.setVisibility(View.GONE);
        }

    }
}
