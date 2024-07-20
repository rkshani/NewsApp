package com.andromob.andronews.interfaces;

import com.andromob.andronews.models.News;

import java.util.List;

public interface NewsListener {
    void showLoading();
    void hideLoading();
    void onErrorLoading(String message);
    void setNews(List<News> dataList);
}
