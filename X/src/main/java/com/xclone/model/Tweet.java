package com.xclone.model;

import java.sql.Timestamp;

public class Tweet {
    private int id;
    private int userId;
    private String content;
    private String[] mediaUrls;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isDeleted;
    private Integer replyToTweetId;
    private Integer retweetOfTweetId;

    private String username;
    private String displayName;
    private int likesCount;
    private int repliesCount;
    private boolean isLikedByCurrentUser;

    public Tweet() {}

    public Tweet(int userId, String content){
        this.userId = userId;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String[] getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(String[] mediaUrls) { this.mediaUrls = mediaUrls; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Integer getReplyToTweetId() { return replyToTweetId; }
    public void setReplyToTweetId(Integer replyToTweetId) { this.replyToTweetId = replyToTweetId; }

    public Integer getRetweetOfTweetId() { return retweetOfTweetId; }
    public void setRetweetOfTweetId(Integer retweetOfTweetId) { this.retweetOfTweetId = retweetOfTweetId; }



    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getRepliesCount() { return repliesCount; }
    public void setRepliesCount(int repliesCount) { this.repliesCount = repliesCount; }

    public boolean isLikedByCurrentUser() { return isLikedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { isLikedByCurrentUser = likedByCurrentUser; }

    @Override
    public String toString(){
        return "Tweet{ " +
                "id = " + id +
                ", userId = " + userId +
                ", content = " + content + '\'' +
                ", createdAt = " + createdAt +
                ", likesCount = " + likesCount +
                " }";
    }
}
