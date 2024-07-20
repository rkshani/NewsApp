package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EarnHistory {
    @SerializedName("e_id")
    @Expose
    int e_id;
    @SerializedName("post_id")
    @Expose
    int post_id;
    @SerializedName("user_id")
    @Expose
    int user_id;
    @SerializedName("points")
    @Expose
    int points;
    @SerializedName("status")
    @Expose
    int status;
    @SerializedName("news_title")
    @Expose
    String news_title;
    @SerializedName("activity")
    @Expose
    String activity;
    @SerializedName("created_at")
    @Expose
    String created_at;

    public EarnHistory(int e_id, int post_id, int user_id, int points, int status, String news_title, String activity, String created_at) {
        this.e_id = e_id;
        this.post_id = post_id;
        this.user_id = user_id;
        this.points = points;
        this.status = status;
        this.news_title = news_title;
        this.activity = activity;
        this.created_at = created_at;
    }

    public int getE_id() {
        return e_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getPoints() {
        return points;
    }

    public int getStatus() {
        return status;
    }

    public String getNews_title() {
        return news_title;
    }

    public String getActivity() {
        return activity;
    }

    public String getCreated_at() {
        return created_at;
    }
}
