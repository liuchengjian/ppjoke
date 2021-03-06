package com.liucj.ppjoke.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.Objects;

/**
 * 帖子
 */
public class Feed implements Serializable, MultiItemEntity {
    public static final int TYPE_IMAGE_TEXT = 1;//图文
    public static final int TYPE_VIDEO = 2;//视频
    /**
     * id : 428
     * itemId : 1578976510452
     * itemType : 2
     * createTime : 1578977844500
     * duration : 8
     * feeds_text : 2020他来了，就在眼前了
     * authorId : 1578919786
     * activityIcon : null
     * activityText : 2020新年快乐
     * width : 960
     * height : 540
     * url : https://pipijoke.oss-cn-hangzhou.aliyuncs.com/New%20Year%20-%2029212-video.mp4
     * cover : https://pipijoke.oss-cn-hangzhou.aliyuncs.com/2020%E5%B0%81%E9%9D%A2%E5%9B%BE.png
     * author : {}
     * topComment : {"id":1126,"itemId":1578976510452,"commentId":1579007787804000,"userId":1578919786,"commentType":1,"createTime":1579007787804,"commentCount":0,"likeCount":1001,"commentText":"2020他来了，就在眼前了~Happy New Year","imageUrl":"","videoUrl":"","width":0,"height":0,"hasLiked":false,"author":{"id":1250,"userId":1578919786,"name":"、蓅哖╰伊人为谁笑","avatar":"http://qzapp.qlogo.cn/qzapp/101794421/FE41683AD4ECF91B7736CA9DB8104A5C/100","description":"这是一只神秘的jetpack","likeCount":3,"topCommentCount":0,"followCount":0,"followerCount":2,"qqOpenId":"FE41683AD4ECF91B7736CA9DB8104A5C","expires_time":1586695789903,"score":0,"historyCount":222,"commentCount":9,"favoriteCount":0,"feedCount":0,"hasFollow":false},"ugc":{"likeCount":103,"shareCount":10,"commentCount":10,"hasFavorite":false,"hasLiked":false,"hasdiss":false,"hasDissed":false}}
     * ugc : {"likeCount":1001,"shareCount":12,"commentCount":504,"hasFavorite":false,"hasLiked":false,"hasdiss":false,"hasDissed":false}
     */

    public int id;
    public long itemId;
    public int itemType;
    public long createTime;
    public int duration;
    public String feeds_text;
    public int authorId;
    public String activityIcon;
    public String activityText;
    public int width;
    public int height;
    public String url;
    public String cover;
    public User author;
    public Comment topComment;
    public Ugc ugc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feed feed = (Feed) o;
        return id == feed.id &&
                itemId == feed.itemId &&
                itemType == feed.itemType &&
                createTime == feed.createTime &&
                duration == feed.duration &&
                authorId == feed.authorId &&
                width == feed.width &&
                height == feed.height &&
                Objects.equals(feeds_text, feed.feeds_text) &&
                Objects.equals(activityIcon, feed.activityIcon) &&
                Objects.equals(activityText, feed.activityText) &&
                Objects.equals(url, feed.url) &&
                Objects.equals(cover, feed.cover) &&
                Objects.equals(author, feed.author) &&
                Objects.equals(topComment, feed.topComment) &&
                Objects.equals(ugc, feed.ugc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, itemType, createTime, duration, feeds_text, authorId, activityIcon, activityText, width, height, url, cover, author, topComment, ugc);
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
