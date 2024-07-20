package com.andromob.andronews.utils;

import android.view.View;

import androidx.annotation.NonNull;

import java.util.List;
import com.andromob.andronews.BuildConfig;

import com.andromob.andronews.interfaces.VideosListener;
import com.andromob.andronews.models.Category;
import com.andromob.andronews.models.News;
import com.andromob.andronews.interfaces.CategoryListener;
import com.andromob.andronews.interfaces.NewsListener;
import com.andromob.andronews.models.Videos;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Presenter {
    private View views;

    NewsListener newsListener;
    VideosListener videosListener;
    CategoryListener categoryListener;
    public Presenter(View views){
        this.views = views;
    }

    public Presenter(CategoryListener categoryListener){
        this.categoryListener = categoryListener;
    }
    public Presenter(NewsListener newsListener){
        this.newsListener = newsListener;
    }
    public Presenter(VideosListener videosListener){
        this.videosListener = videosListener;
    }

    public void getCategories() {
        categoryListener.showLoading();
        Call<List<Category>> call = Methods.getApi().getCategories("category", BuildConfig.API_KEY);

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call,
                                   @NonNull Response<List<Category>> response) {
                categoryListener.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    categoryListener.setCategory(response.body());
                } else {
                    categoryListener.onErrorLoading(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                categoryListener.hideLoading();
                categoryListener.onErrorLoading("Something went Wrong!");
                call.cancel();
            }
        });
    }

    public void getMethodNews(String type) {
        newsListener.showLoading();
        Call<List<News>> call = Methods.getApi().getMethodNews("news", type, BuildConfig.API_KEY);
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call,
                                   @NonNull Response<List<News>> response) {
                newsListener.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    newsListener.setNews(response.body());
                } else {
                    newsListener.onErrorLoading(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<News>> call, @NonNull Throwable t) {
                newsListener.hideLoading();
                newsListener.onErrorLoading("Something went Wrong!");
                call.cancel();
            }
        });
    }

    public void getMethodNewsFavorite(int user_id) {
        newsListener.showLoading();
        Call<List<News>> call = Methods.getApi().getMethodNewsFavorite("news", "favorites", user_id, BuildConfig.API_KEY);
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call,
                                   @NonNull Response<List<News>> response) {
                newsListener.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    newsListener.setNews(response.body());
                } else {
                    newsListener.onErrorLoading(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<News>> call, @NonNull Throwable t) {
                newsListener.hideLoading();
                newsListener.onErrorLoading("Something went Wrong!");
                call.cancel();
            }
        });
    }

    public void getMethodNewsByUser(String type, int user_id) {
        newsListener.showLoading();
        Call<List<News>> call = Methods.getApi().getMethodNewsByUser("news", type, BuildConfig.API_KEY, user_id);
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call,
                                   @NonNull Response<List<News>> response) {
                newsListener.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    newsListener.setNews(response.body());
                } else {
                    newsListener.onErrorLoading(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<News>> call, @NonNull Throwable t) {
                newsListener.hideLoading();
                newsListener.onErrorLoading("Something went Wrong!");
                call.cancel();
            }
        });
    }

    public void getNewsBySearch(String search) {
        newsListener.showLoading();
        Call<List<News>> call = Methods.getApi().getNewsBySearch("news", search, BuildConfig.API_KEY);
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call,
                                   @NonNull Response<List<News>> response) {
                newsListener.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    newsListener.setNews(response.body());
                } else {
                    newsListener.onErrorLoading(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<News>> call, @NonNull Throwable t) {
                newsListener.hideLoading();
                newsListener.onErrorLoading("Something went Wrong!");
                call.cancel();
            }
        });
    }

    public void getMethodVideos(int page) {
        videosListener.showLoading();
        Call<List<Videos>> call = Methods.getApi().getMethodVideos("videos", BuildConfig.API_KEY, page);
        call.enqueue(new Callback<List<Videos>>() {
            @Override
            public void onResponse(@NonNull Call<List<Videos>> call,
                                   @NonNull Response<List<Videos>> response) {
                videosListener.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    videosListener.setVideos(response.body());
                } else {
                    videosListener.onErrorLoading(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Videos>> call, @NonNull Throwable t) {
                videosListener.hideLoading();
                videosListener.onErrorLoading("Something went Wrong!");
                call.cancel();
            }
        });
    }

    public void getNewsByCategory(int cat_id, int page) {
        newsListener.showLoading();
        Call<List<News>> call = Methods.getApi().getNewsByCategory("news", cat_id, page, BuildConfig.API_KEY);
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call,
                                   @NonNull Response<List<News>> response) {
                newsListener.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    newsListener.setNews(response.body());
                } else {
                    newsListener.onErrorLoading(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<News>> call, @NonNull Throwable t) {
                newsListener.hideLoading();
                newsListener.onErrorLoading("Something went Wrong!");
                call.cancel();
            }
        });
    }
}
