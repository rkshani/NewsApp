package com.andromob.andronews.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Videos implements Parcelable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("cid")
    @Expose
    private int cid;
    @SerializedName("comments")
    @Expose
    private int comments;
    @SerializedName("video_type")
    @Expose
    private String video_type;
    @SerializedName("category_name")
    @Expose
    private String category_name;
    @SerializedName("video_title")
    @Expose
    private String video_title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("video_url")
    @Expose
    private String video_url;
    @SerializedName("posted_at")
    @Expose
    private String posted_at;
    @SerializedName("views")
    @Expose
    private String views;
    @SerializedName("video_status")
    @Expose
    private String video_status;

    public Videos() {

    }

    public Videos(int id, int cid, int comments, String video_type, String category_name, String video_title, String description, String thumbnail, String video_url, String posted_at, String views, String video_status) {
        this.id = id;
        this.cid = cid;
        this.comments = comments;
        this.video_type = video_type;
        this.category_name = category_name;
        this.video_title = video_title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.video_url = video_url;
        this.posted_at = posted_at;
        this.views = views;
        this.video_status = video_status;
    }

    public int getId() {
        return id;
    }

    public int getCid() {
        return cid;
    }

    public int getComments() {
        return comments;
    }

    public String getVideo_type() {
        return video_type;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getVideo_title() {
        return video_title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getPosted_at() {
        return posted_at;
    }

    public String getViews() {
        return views;
    }

    public String getVideo_status() {
        return video_status;
    }

    protected Videos(Parcel in) {
        id = in.readInt();
        cid = in.readInt();
        comments = in.readInt();
        video_type = in.readString();
        category_name = in.readString();
        video_title = in.readString();
        description = in.readString();
        thumbnail = in.readString();
        video_url = in.readString();
        posted_at = in.readString();
        views = in.readString();
        video_status = in.readString();
    }

    public static final Creator<Videos> CREATOR = new Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel in) {
            return new Videos(in);
        }

        @Override
        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(cid);
        dest.writeInt(comments);
        dest.writeString(video_type);
        dest.writeString(category_name);
        dest.writeString(video_title);
        dest.writeString(description);
        dest.writeString(thumbnail);
        dest.writeString(video_url);
        dest.writeString(posted_at);
        dest.writeString(views);
        dest.writeString(video_status);
    }
}
