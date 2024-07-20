package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user_id")
    @Expose
    private int user_id;
    @SerializedName("user_type")
    @Expose
    private String user_type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("img_url")
    @Expose
    private String img_url;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("auth_id")
    @Expose
    private String auth_id;
    @SerializedName("registered_on")
    @Expose
    private String registered_on;
    @SerializedName("fav_ids")
    @Expose
    private String fav_ids;

    public User(int user_id, String user_type, String name, String email, String img_url, String phone, String auth_id, String registered_on, String fav_ids) {
        this.user_id = user_id;
        this.user_type = user_type;
        this.name = name;
        this.email = email;
        this.img_url = img_url;
        this.phone = phone;
        this.auth_id = auth_id;
        this.registered_on = registered_on;
        this.fav_ids = fav_ids;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getPhone() {
        return phone;
    }

    public String getAuth_id() {
        return auth_id;
    }

    public String getRegistered_on() {
        return registered_on;
    }

    public String getFav_ids() {
        return fav_ids;
    }
}
