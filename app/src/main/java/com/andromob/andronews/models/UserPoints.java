package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPoints {
    @SerializedName("points")
    @Expose
    int points;

    public UserPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
