package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VP {
    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("p_count")
    @Expose
    private int p_count;

    public VP(boolean error, int p_count) {
        this.error = error;
        this.p_count = p_count;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public int getP_count() {
        return p_count;
    }

    public String getmessage() {
        return message;
    }
}
