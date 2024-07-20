package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("cat_id")
    @Expose
    int cat_id;
    @SerializedName("category_name")
    @Expose
    String category_name;
    @SerializedName("category_image")
    @Expose
    String category_image;

    public Category(int cat_id, String category_name, String category_image) {
        this.cat_id = cat_id;
        this.category_name = category_name;
        this.category_image = category_image;
    }

    public int getCat_id() {
        return cat_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_image() {
        return category_image;
    }
}
