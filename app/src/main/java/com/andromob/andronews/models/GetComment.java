package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetComment {
    @SerializedName("cmt_id")
    @Expose
    int cmt_id;
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("user_name")
    @Expose
    String user_name;
    @SerializedName("comment")
    @Expose
    String comment;
    @SerializedName("user_img")
    @Expose
    String user_img;
    @SerializedName("nid")
    @Expose
    String nid;
    @SerializedName("vid")
    @Expose
    String vid;
    @SerializedName("created")
    @Expose
    String created;

    public GetComment(int cmt_id, String type, String user_name, String comment, String user_img, String nid, String vid, String created) {
        this.cmt_id = cmt_id;
        this.type = type;
        this.user_name = user_name;
        this.comment = comment;
        this.user_img = user_img;
        this.nid = nid;
        this.vid = vid;
        this.created = created;
    }

    public int getCmt_id() {
        return cmt_id;
    }

    public String getType() {
        return type;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getComment() {
        return comment;
    }

    public String getUser_img() {
        return user_img;
    }

    public String getNid() {
        return nid;
    }

    public String getVid() {
        return vid;
    }

    public String getCreated() {
        return created;
    }
}
