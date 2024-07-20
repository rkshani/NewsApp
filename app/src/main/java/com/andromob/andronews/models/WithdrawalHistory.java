package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WithdrawalHistory {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("uid")
    @Expose
    int uid;
    @SerializedName("points")
    @Expose
    int points;
    @SerializedName("payment_status")
    @Expose
    int payment_status;
    @SerializedName("payment_mode")
    @Expose
    String payment_mode;
    @SerializedName("payment_detail")
    @Expose
    String payment_detail;
    @SerializedName("amount")
    @Expose
    String amount;
    @SerializedName("created_at")
    @Expose
    String created_at;
    @SerializedName("remarks")
    @Expose
    String remarks;

    public WithdrawalHistory(int id, int uid, int points, int payment_status, String payment_mode, String payment_detail, String amount, String created_at, String remarks) {
        this.id = id;
        this.uid = uid;
        this.points = points;
        this.payment_status = payment_status;
        this.payment_mode = payment_mode;
        this.payment_detail = payment_detail;
        this.amount = amount;
        this.created_at = created_at;
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public int getUid() {
        return uid;
    }

    public int getPoints() {
        return points;
    }

    public int getPayment_status() {
        return payment_status;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public String getPayment_detail() {
        return payment_detail;
    }

    public String getAmount() {
        return amount;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getRemarks() {
        return remarks;
    }
}
