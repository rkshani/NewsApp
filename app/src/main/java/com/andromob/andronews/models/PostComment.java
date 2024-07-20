package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostComment {
    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("comments_count")
    @Expose
    private int comments_count;

    public PostComment(boolean error, String message, int comments_count) {
        this.error = error;
        this.message = message;
        this.comments_count = comments_count;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public int getComments_count() {
        return comments_count;
    }
}
