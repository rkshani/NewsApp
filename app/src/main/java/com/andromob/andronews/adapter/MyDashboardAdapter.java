package com.andromob.andronews.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.AddNewsActivity;
import com.andromob.andronews.activity.CommentsActivity;
import com.andromob.andronews.models.News;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.Methods;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyDashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    private ArrayList<Object> data = new ArrayList<>();
    User user;

    public MyDashboardAdapter(Activity activity, User user) {
        this.activity = activity;
        this.user = user;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, time;
        ImageView thumb;
        LinearLayout btnEdit, btnComment, btnReport;
        TextView btnStatus;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            time = view.findViewById(R.id.time);
            thumb = view.findViewById(R.id.thumb);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnComment = view.findViewById(R.id.btnComment);
            btnStatus = view.findViewById(R.id.btnStatus);
            btnReport = view.findViewById(R.id.btnReport);
        }
    }

    public void setData(List<News> rowData) {
        this.data.addAll(rowData);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.activity).inflate(R.layout.item_user_post, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final News row = (News) data.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        Glide.with(activity).asBitmap().load(row.getThumbnail()).centerCrop().into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                myViewHolder.thumb.setImageBitmap(resource);
                myViewHolder.thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });

        myViewHolder.title.setText(row.getNews_title());
        myViewHolder.time.setText(row.getPosted_at());

        myViewHolder.btnEdit.setOnClickListener(v -> {
            AdsManager.getInstance(activity).showInterAdOnClick(activity, new AdsManager.InterAdListener() {
                @Override
                public void onClick(String type) {
                    ArrayList<News> news = new ArrayList<>(Collections.singleton(row));
                    Intent intent = new Intent(activity, AddNewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("data", news);
                    intent.putExtras(bundle);
                    intent.putExtra("type", "edit");
                    activity.startActivity(intent);
                }
            }, "");

        });

        myViewHolder.btnComment.setOnClickListener(v -> {
            AdsManager.getInstance(activity).showInterAdOnClick(activity, new AdsManager.InterAdListener() {
                @Override
                public void onClick(String type) {
                    Intent intent = new Intent(activity, CommentsActivity.class);
                    intent.putExtra("type", "news");
                    intent.putExtra("post_id", row.getId());
                    activity.startActivity(intent);
                }
            }, "");

        });

        myViewHolder.btnReport.setOnClickListener(v -> {
            AdsManager.getInstance(activity).showInterAdOnClick(activity, new AdsManager.InterAdListener() {
                @Override
                public void onClick(String type) {
                    new Methods(activity).doReport("news", user.getUser_id(), row.getId(), 0);
                }
            }, "");
        });

        setStatus(myViewHolder.btnStatus, row.getNews_status());
    }

    void setStatus(TextView textView, int status){
        if (status==1){
            textView.setText(activity.getString(R.string.approved));
            textView.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen));
            textView.setBackground(ContextCompat.getDrawable(activity,R.drawable.post_status_bg_green));
        } else if (status==2){
            textView.setText(activity.getString(R.string.waiting));
            textView.setTextColor(ContextCompat.getColor(activity, R.color.colorOrange));
            textView.setBackground(ContextCompat.getDrawable(activity,R.drawable.post_status_bg_orange));
        } else if (status==3){
            textView.setText(activity.getString(R.string.rejected));
            textView.setTextColor(ContextCompat.getColor(activity, R.color.colorRed));
            textView.setBackground(ContextCompat.getDrawable(activity,R.drawable.post_status_bg_red));
        } else if (status==0){
            textView.setText(activity.getString(R.string.disabled));
            textView.setTextColor(ContextCompat.getColor(activity, R.color.colorRed));
            textView.setBackground(ContextCompat.getDrawable(activity,R.drawable.post_status_bg_red));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
