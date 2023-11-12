package com.teamproject2.models;

import java.util.Date;

public class Post {
    private String userId;
    private String title;

    private String contents;

    private String timestamp;
    private String postId;

    public Post() {
    }

    public Post(String userId, String title, String contents, String timestamp) {
        this.userId = userId;
        this.title = title;
        this.contents = contents;
        this.timestamp = timestamp;
    }

    public Post(String userId, String title, String contents, String timestamp, String postId) {
        this.userId = userId;
        this.title = title;
        this.contents = contents;
        this.timestamp = timestamp;
        this.postId=postId;
    }

    public Post(String userId, String title, String contents) {
        this.userId = userId;
        this.title = title;
        this.contents = contents;
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String content) {
        this.contents = content;
    }

    @Override
    public String toString() {
        return "Post{" +
                "time='" + String.valueOf(timestamp) + '\'' +
                "userIdId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + contents + '\'' +
                '}';
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
