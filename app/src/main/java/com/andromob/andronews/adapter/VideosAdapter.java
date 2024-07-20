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
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.VideoDetailActivity;
import com.andromob.andronews.models.Videos;
import com.andromob.andronews.utils.AdsManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    private ArrayList<Object> data = new ArrayList<>();

    public VideosAdapter(Activity activity) {
        this.activity = activity;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lyt_video;
        TextView title, category, time, comments;
        ImageView thumb;

        public MyViewHolder(View view) {
            super(view);
            lyt_video = view.findViewById(R.id.lyt_video);
            title = view.findViewById(R.id.title);
            category = view.findViewById(R.id.category);
            time = view.findViewById(R.id.time);
            comments = view.findViewById(R.id.comments);
            thumb = view.findViewById(R.id.thumb);
        }
    }

    public void setData(List<Videos> rowData) {
        this.data.addAll(rowData);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.activity).inflate(R.layout.item_video, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Videos row = (Videos) data.get(position);
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

        myViewHolder.title.setText(row.getVideo_title());
        myViewHolder.time.setText(row.getPosted_at());
        myViewHolder.comments.setText(String.valueOf(row.getComments()));
        myViewHolder.lyt_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdsManager.getInstance(activity).showInterAdOnClick(activity, new AdsManager.InterAdListener() {
                    @Override
                    public void onClick(String type) {
                        ArrayList<Videos> videolist = new ArrayList<>(Collections.singleton(row));
                        Intent intent = new Intent(activity, VideoDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("data", videolist);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    }
                }, "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
