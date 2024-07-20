package com.andromob.andronews.adapter;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.CommentsActivity;
import com.andromob.andronews.activity.LoginActivity;
import com.andromob.andronews.activity.NewsDetailActivity;
import com.andromob.andronews.models.News;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_ONE = 0;
    private static final int ITEM_TYPE_TWO = 1;
    Activity activity;
    private ArrayList<Object> data = new ArrayList<>();
    private boolean isRelated = false;
    User user;

    public NewsAdapter(Activity activity, boolean isRelated, User user) {
        this.activity = activity;
        this.user = user;
        this.isRelated = isRelated;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lyt_news;
        TextView title, username, time, views, comments, share;
        ImageView thumb, btn_popup;
        ShapeableImageView user_img;

        public MyViewHolder(View view) {
            super(view);
            lyt_news = view.findViewById(R.id.lyt_news);
            title = view.findViewById(R.id.title);
            username = view.findViewById(R.id.username);
            time = view.findViewById(R.id.time);
            views = view.findViewById(R.id.views);
            comments = view.findViewById(R.id.comments);
            share = view.findViewById(R.id.share);
            thumb = view.findViewById(R.id.thumb);
            user_img = view.findViewById(R.id.user_img);
            btn_popup = view.findViewById(R.id.btn_popup);
        }
    }

    public void setData(List<News> rowData) {
        this.data.addAll(rowData);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_ONE) {
            View view = LayoutInflater.from(this.activity).inflate(R.layout.item_news_big, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == ITEM_TYPE_TWO){
            View view = LayoutInflater.from(this.activity).inflate(R.layout.item_news, parent, false);
            return new MyViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final News row = (News) data.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        Glide.with(activity).asBitmap().load(row.getThumbnail()).placeholder(R.drawable.placeholder_img).centerCrop().into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                myViewHolder.thumb.setImageBitmap(resource);
                myViewHolder.thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
        Glide.with(activity).asBitmap().load(row.getUser_img()).placeholder(R.drawable.user_img).centerCrop().into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                myViewHolder.user_img.setImageBitmap(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });

        myViewHolder.title.setText(row.getNews_title());
        myViewHolder.time.setText(row.getPosted_at());
        myViewHolder.views.setText(row.getViews());
        myViewHolder.comments.setText(String.valueOf(row.getComments()));
        myViewHolder.share.setText(row.getShare());
        if (row.getPosted_by() != null && !row.getPosted_by().equals("")) {
            myViewHolder.username.setText(row.getPosted_by());
        } else {
            myViewHolder.username.setText(activity.getString(R.string.default_posted_by));
        }
        myViewHolder.lyt_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdsManager.getInstance(activity).showInterAdOnClick(activity, new AdsManager.InterAdListener() {
                    @Override
                    public void onClick(String type) {
                        ArrayList<News> news = new ArrayList<>(Collections.singleton(row));
                        Intent intent = new Intent(activity, NewsDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("data", news);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    }
                }, "");
            }
        });

        myViewHolder.btn_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionPopUp(myViewHolder.btn_popup, myViewHolder.thumb, row, myViewHolder.getBindingAdapterPosition());
            }
        });
    }

    private void openOptionPopUp(ImageView imageView, ImageView news_thumb, News newsList,  final int position) {
        ContextThemeWrapper ctw;
        ctw = new ContextThemeWrapper(activity, R.style.PopupMenuLight);
        PopupMenu popup = new PopupMenu(ctw, imageView);
        popup.getMenuInflater().inflate(R.menu.menu_news, popup.getMenu());
        checkFav(newsList.getId(), popup);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_fav:
                        if (LoginPrefManager.getInstance(activity).isLoggedIn()) {
                            addRemoveFav(newsList.getId());
                            new Methods(activity).addRemoveFav(newsList.getId(), user.getUser_id());
                        } else {
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                        }
                        break;
                    case R.id.popup_share:
                        new Methods(activity).shareNews(news_thumb, newsList.getNews_title(), newsList.getId());
                        break;
                    case R.id.popup_comment:
                        if (LoginPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
                            Intent intent = new Intent(activity, CommentsActivity.class);
                            intent.putExtra("type", "news");
                            intent.putExtra("post_id", newsList.getId());
                            activity.startActivity(intent);
                        } else {
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void checkFav(int news_id, PopupMenu popupMenu) {
        String favIDList = new Prefs(activity).getFavorite();
        if (favIDList != null && !favIDList.isEmpty()) {
            if (favIDList.contains("," + news_id + ",")) {
                popupMenu.getMenu().findItem(R.id.popup_fav).setTitle(activity.getString(R.string.remove_from_fav));
            } else {
                popupMenu.getMenu().findItem(R.id.popup_fav).setTitle(activity.getString(R.string.add_to_fav));
            }
        } else {
            popupMenu.getMenu().findItem(R.id.popup_fav).setTitle(activity.getString(R.string.add_to_fav));
        }
    }

    private void addRemoveFav(int favID) {
        String message;
        String favIDList = new Prefs(activity).getFavorite();

        if (!favIDList.isEmpty()) {
            if (favIDList.contains("," + favID + ",")) {
                favIDList = favIDList.replace("," + favID + ",", ",");
                new Prefs(activity).addFavorite(favIDList);
                message = activity.getString(R.string.removed_from_fav);
            } else {
                favIDList = favIDList + favID + ",";
                new Prefs(activity).addFavorite(favIDList);
                message = activity.getString(R.string.added_to_fav);
            }
        } else {
            favIDList = "," + favID + ",";
            new Prefs(activity).addFavorite(favIDList);
            message = activity.getString(R.string.added_to_fav);
        }
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isRelated){
            return ITEM_TYPE_TWO;
        }else {
            if (position == 0) {
                return ITEM_TYPE_ONE;
            } else {
                return ITEM_TYPE_TWO;
            }
        }
    }
}
