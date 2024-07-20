package com.andromob.andronews.interfaces;

import com.andromob.andronews.models.Category;

import java.util.List;

public interface CategoryListener {
    void showLoading();
    void hideLoading();
    void onErrorLoading(String message);
    void setCategory(List<Category> dataList);
}
