package com.andromob.andronews.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.R;
import com.andromob.andronews.models.WithdrawalHistory;
import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.Methods;

import java.util.ArrayList;
import java.util.List;

public class WithdrawalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    private ArrayList<Object> data = new ArrayList<>();

    public WithdrawalAdapter(Activity activity) {
        this.activity = activity;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView points, amount, time;
        Button btnStatus;
        CardView cardWithdrawal;

        public MyViewHolder(View view) {
            super(view);
            points = view.findViewById(R.id.points);
            amount = view.findViewById(R.id.amount);
            time = view.findViewById(R.id.time);
            btnStatus = view.findViewById(R.id.btnStatus);
            cardWithdrawal = view.findViewById(R.id.cardWithdrawal);
        }
    }

    public void setData(List<WithdrawalHistory> rowData) {
        this.data.addAll(rowData);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.activity).inflate(R.layout.item_withdrawal, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final WithdrawalHistory row = (WithdrawalHistory) data.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.points.setText(activity.getString(R.string.user_points) + " " + String.valueOf(row.getPoints()));
        myViewHolder.time.setText(row.getCreated_at());
        myViewHolder.amount.setText(row.getAmount());
        setStatus(myViewHolder.btnStatus, row.getPayment_status());

        myViewHolder.cardWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdsManager.getInstance(activity).showInterAdOnClick(activity, new AdsManager.InterAdListener() {
                    @Override
                    public void onClick(String type) {
                        if(row.getRemarks().isEmpty()){
                            new Methods(activity).showDialog(activity, activity.getString(R.string.payment_details), row.getPayment_detail() + "\nRemarks : "+activity.getString(R.string.in_progress),"payment", "dismiss");
                        } else {
                            new Methods(activity).showDialog(activity, activity.getString(R.string.payment_details), row.getPayment_detail() + "\nRemarks : "+row.getRemarks(),"payment", "dismiss");
                        }
                    }
                }, "");
            }
        });
    }

    void setStatus(Button button, int status) {
        if (status == 0) {
            button.setText(activity.getString(R.string.pending));
            button.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            button.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorYellow));
        } else if (status == 1) {
            button.setText(activity.getString(R.string.paid));
            button.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite));
            button.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorGreen));
        } else if (status == 2) {
            button.setText(activity.getString(R.string.rejected));
            button.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite));
            button.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorAlert));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
