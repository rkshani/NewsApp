package com.andromob.andronews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.R;
import com.andromob.andronews.models.GetComment;
import com.andromob.andronews.utils.Methods;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
    private List<GetComment> dataList;
    Context context;
    private int type;

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userName, comment, time;
        ShapeableImageView userImg;

        MyViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.userName);
            comment = view.findViewById(R.id.comment);
            time = view.findViewById(R.id.time);
            userImg = view.findViewById(R.id.userImg);
        }
    }

    public CommentsAdapter(Context context, List<GetComment> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        GetComment row = dataList.get(position);
        holder.userName.setText(row.getUser_name());
        holder.time.setText(row.getCreated());
        holder.comment.setText(row.getComment());
        new Methods(context).loadImage(holder.userImg, row.getUser_img());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
