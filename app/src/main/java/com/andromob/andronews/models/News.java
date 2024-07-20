package com.andromob.andronews.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class News implements Parcelable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("cid")
    @Expose
    private int cid;
    @SerializedName("uid")
    @Expose
    private int uid;
    @SerializedName("comments")
    @Expose
    private int comments;
    @SerializedName("news_status")
    @Expose
    private int news_status;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("category_name")
    @Expose
    private String category_name;
    @SerializedName("news_title")
    @Expose
    private String news_title;
    @SerializedName("news")
    @Expose
    private String news;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("video_url")
    @Expose
    private String video_url;
    @SerializedName("posted_by")
    @Expose
    private String posted_by;
    @SerializedName("user_img")
    @Expose
    private String user_img;
    @SerializedName("posted_at")
    @Expose
    private String posted_at;
    @SerializedName("views")
    @Expose
    private String views;
    @SerializedName("share")
    @Expose
    private String share;

    public News() {

    }

    public News(int id, int cid, int uid, int comments, int news_status, String type, String category_name, String news_title, String news, String thumbnail, String video_url, String posted_by, String user_img, String posted_at, String views, String share) {
        this.id = id;
        this.cid = cid;
        this.uid = uid;
        this.comments = comments;
        this.news_status = news_status;
        this.type = type;
        this.category_name = category_name;
        this.news_title = news_title;
        this.news = news;
        this.thumbnail = thumbnail;
        this.video_url = video_url;
        this.posted_by = posted_by;
        this.user_img = user_img;
        this.posted_at = posted_at;
        this.views = views;
        this.share = share;
    }

    protected News(Parcel in) {
        id = in.readInt();
        cid = in.readInt();
        uid = in.readInt();
        comments = in.readInt();
        news_status = in.readInt();
        type = in.readString();
        category_name = in.readString();
        news_title = in.readString();
        news = in.readString();
        thumbnail = in.readString();
        video_url = in.readString();
        posted_by = in.readString();
        user_img = in.readString();
        posted_at = in.readString();
        views = in.readString();
        share = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getCid() {
        return cid;
    }

    public int getUid() {
        return uid;
    }

    public int getComments() {
        return comments;
    }

    public int getNews_status() {
        return news_status;
    }

    public String getType() {
        return type;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getNews_title() {
        return news_title;
    }

    public String getNews() {
        return news;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public String getUser_img() {
        return user_img;
    }

    public String getPosted_at() {
        return posted_at;
    }

    public String getViews() {
        return views;
    }

    public String getShare() {
        return share;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(cid);
        dest.writeInt(uid);
        dest.writeInt(comments);
        dest.writeInt(news_status);
        dest.writeString(type);
        dest.writeString(category_name);
        dest.writeString(news_title);
        dest.writeString(news);
        dest.writeString(thumbnail);
        dest.writeString(video_url);
        dest.writeString(posted_by);
        dest.writeString(user_img);
        dest.writeString(posted_at);
        dest.writeString(views);
        dest.writeString(share);
    }
}
