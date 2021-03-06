package com.liucj.ppjoke.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.Objects;

/**
 * 评论
 */
public class Comment implements Serializable {

    /**
     * id : 1126
     * itemId : 1578976510452
     * commentId : 1579007787804000
     * userId : 1578919786
     * commentType : 1
     * createTime : 1579007787804
     * commentCount : 0
     * likeCount : 1001
     * commentText : 2020他来了，就在眼前了~Happy New Year
     * imageUrl :
     * videoUrl :
     * width : 0
     * height : 0
     * hasLiked : false
     * author : {"id":1250,"userId":1578919786,"name":"、蓅哖╰伊人为谁笑","avatar":"http://qzapp.qlogo.cn/qzapp/101794421/FE41683AD4ECF91B7736CA9DB8104A5C/100","description":"这是一只神秘的jetpack","likeCount":3,"topCommentCount":0,"followCount":0,"followerCount":2,"qqOpenId":"FE41683AD4ECF91B7736CA9DB8104A5C","expires_time":1586695789903,"score":0,"historyCount":222,"commentCount":9,"favoriteCount":0,"feedCount":0,"hasFollow":false}
     * ugc : {"likeCount":103,"shareCount":10,"commentCount":10,"hasFavorite":false,"hasLiked":false,"hasdiss":false,"hasDissed":false}
     */

    public int id;
    public long itemId;
    public long commentId;
    public int userId;
    public int commentType;
    public long createTime;
    public int commentCount;
    public int likeCount;
    public String commentText;
    public String imageUrl;
    public String videoUrl;
    public int width;
    public int height;
    public boolean hasLiked;
    public User author;
    public Ugc ugc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return likeCount == comment.likeCount &&
                hasLiked == comment.hasLiked &&
                Objects.equals(author, comment.author) &&
                Objects.equals(ugc, comment.ugc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(likeCount, hasLiked, author, ugc);
    }


}
