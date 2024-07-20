package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetResponseDetailed {
    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private List<User> userList = null;

    public GetResponseDetailed(boolean error, String message, List<User> userList) {
        this.error = error;
        this.message = message;
        this.userList = userList;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<User> getUserList() {
        return userList;
    }
}
