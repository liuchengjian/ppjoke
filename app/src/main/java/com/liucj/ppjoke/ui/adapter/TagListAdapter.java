package com.liucj.ppjoke.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.button.MaterialButton;
import com.liucj.ppjoke.R;
import com.liucj.ppjoke.model.Comment;
import com.liucj.ppjoke.model.TagList;
import com.liucj.ppjoke.ui.view.PPImageView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TagListAdapter extends BaseQuickAdapter<TagList, BaseViewHolder> {
    public TagListAdapter(int layoutResId) {
        super(layoutResId);
    }

    public TagListAdapter(int layoutResId, @Nullable List<TagList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, TagList tagList) {
        PPImageView tagAvtar = holder.getView(R.id.tag_avtar);
        tagAvtar.setImageUrl(tagList.icon, true);
        holder.setText(R.id.tag_title, tagList.title);
        holder.setText(R.id.tag_title, tagList.title);
        holder.setText(R.id.tag_desc, tagList.feedNum + getContext().getString(R.string.tag_list_item_hot_feed));
        MaterialButton actionFollow = holder.getView(R.id.action_follow);
        actionFollow.setText(tagList.hasFollow ? getContext().getString(R.string.tag_follow) : getContext().getString(R.string.tag_unfollow)
        );
    }
}
