package com.andromob.andronews.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.R;
import com.andromob.andronews.models.EarnHistory;

import java.util.ArrayList;
import java.util.List;

public class EarningAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    private ArrayList<Object> data = new ArrayList<>();

    public EarningAdapter(Activity activity) {
        this.activity = activity;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, activity, time, points;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            time = view.findViewById(R.id.time);
            points = view.findViewById(R.id.points);
            activity = view.findViewById(R.id.activity);
        }
    }

    public void setData(List<EarnHistory> rowData) {
        this.data.addAll(rowData);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.activity).inflate(R.layout.item_earning_points, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final EarnHistory row = (EarnHistory) data.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.title.setText(row.getNews_title());
        myViewHolder.activity.setText(row.getActivity());
        myViewHolder.time.setText(row.getCreated_at());
        myViewHolder.points.setText(String.valueOf(row.getPoints()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
