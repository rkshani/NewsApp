package com.andromob.andronews.interfaces;

import com.andromob.andronews.models.Videos;

import java.util.List;

public interface VideosListener {
    void showLoading();
    void hideLoading();
    void onErrorLoading(String message);
    void setVideos(List<Videos> dataList);
}
