package com.andromob.andronews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings {
    @SerializedName("error")
    @Expose
    boolean isError;
    @SerializedName("message")
    @Expose
    String message;
    @SerializedName("app_update_status")
    @Expose
    boolean app_update_status;
    @SerializedName("inter_clicks")
    @Expose
    int inter_clicks;
    @SerializedName("ad_status")
    @Expose
    String ad_status;
    @SerializedName("ad_network")
    @Expose
    String ad_network;
    @SerializedName("admob_small_banner")
    @Expose
    String admob_small_banner;
    @SerializedName("admob_medium_banner")
    @Expose
    String admob_medium_banner;
    @SerializedName("admob_inter")
    @Expose
    String admob_inter;
    @SerializedName("admob_native")
    @Expose
    String admob_native;
    @SerializedName("facebook_small_banner")
    @Expose
    String facebook_small_banner;
    @SerializedName("facebook_medium_banner")
    @Expose
    String facebook_medium_banner;
    @SerializedName("facebook_inter")
    @Expose
    String facebook_inter;
    @SerializedName("facebook_native")
    @Expose
    String facebook_native;
    @SerializedName("applovin_small_banner")
    @Expose
    String applovin_small_banner;
    @SerializedName("applovin_medium_banner")
    @Expose
    String applovin_medium_banner;
    @SerializedName("applovin_inter")
    @Expose
    String applovin_inter;
    @SerializedName("applovin_native")
    @Expose
    String applovin_native;
    @SerializedName("startapp_app_id")
    @Expose
    String startapp_app_id;
    @SerializedName("app_new_version")
    @Expose
    String app_new_version;
    @SerializedName("app_update_desc")
    @Expose
    String app_update_desc;
    @SerializedName("app_redirect_url")
    @Expose
    String app_redirect_url;
    @SerializedName("app_privacy_policy")
    @Expose
    String app_privacy_policy;
    @SerializedName("redeem_currency")
    @Expose
    String redeem_currency;
    @SerializedName("news_reward_status")
    @Expose
    boolean news_reward_status;
    @SerializedName("redeem_points")
    @Expose
    int redeem_points;
    @SerializedName("minimum_redeem_points")
    @Expose
    int minimum_redeem_points;
    @SerializedName("news_seconds")
    @Expose
    int news_seconds;
    @SerializedName("news_reward")
    @Expose
    int news_reward;
    @SerializedName("redeem_amount")
    @Expose
    double redeem_amount;
    @SerializedName("payment_methods")
    @Expose
    String[] payment_methods;
    @SerializedName("facebook_username")
    @Expose
    String facebook_username;
    @SerializedName("facebook_id")
    @Expose
    String facebook_id;
    @SerializedName("instagram_username")
    @Expose
    String instagram_username;
    @SerializedName("twitter_username")
    @Expose
    String twitter_username;
    @SerializedName("telegram_username")
    @Expose
    String telegram_username;
    @SerializedName("youtube_username")
    @Expose
    String youtube_username;
    @SerializedName("about_us_contact")
    String about_us_contact;
    @SerializedName("about_us_email")
    String about_us_email;
    @SerializedName("about_us_developer")
    String about_us_developer;
    @SerializedName("about_us_description")
    String about_us_description;

    public Settings() {
    }

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public boolean isApp_update_status() {
        return app_update_status;
    }

    public int getInter_clicks() {
        return inter_clicks;
    }

    public String getAd_status() {
        return ad_status;
    }

    public String getAd_network() {
        return ad_network;
    }

    public String getAdmob_small_banner() {
        return admob_small_banner;
    }

    public String getAdmob_medium_banner() {
        return admob_medium_banner;
    }

    public String getAdmob_inter() {
        return admob_inter;
    }

    public String getAdmob_native() {
        return admob_native;
    }

    public String getFacebook_small_banner() {
        return facebook_small_banner;
    }

    public String getFacebook_medium_banner() {
        return facebook_medium_banner;
    }

    public String getFacebook_inter() {
        return facebook_inter;
    }

    public String getFacebook_native() {
        return facebook_native;
    }

    public String getApplovin_small_banner() {
        return applovin_small_banner;
    }

    public String getApplovin_medium_banner() {
        return applovin_medium_banner;
    }

    public String getApplovin_inter() {
        return applovin_inter;
    }

    public String getApplovin_native() {
        return applovin_native;
    }

    public String getStartapp_app_id() {
        return startapp_app_id;
    }

    public String getApp_new_version() {
        return app_new_version;
    }

    public String getApp_update_desc() {
        return app_update_desc;
    }

    public String getApp_redirect_url() {
        return app_redirect_url;
    }

    public String getApp_privacy_policy() {
        return app_privacy_policy;
    }

    public String getRedeem_currency() {
        return redeem_currency;
    }

    public boolean isNews_reward_status() {
        return news_reward_status;
    }

    public int getRedeem_points() {
        return redeem_points;
    }

    public int getMinimum_redeem_points() {
        return minimum_redeem_points;
    }

    public int getNews_seconds() {
        return news_seconds;
    }

    public int getNews_reward() {
        return news_reward;
    }

    public double getRedeem_amount() {
        return redeem_amount;
    }

    public String[] getPayment_methods() {
        return payment_methods;
    }

    public String getFacebook_username() {
        return facebook_username;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public String getInstagram_username() {
        return instagram_username;
    }

    public String getTwitter_username() {
        return twitter_username;
    }

    public String getTelegram_username() {
        return telegram_username;
    }

    public String getYoutube_username() {
        return youtube_username;
    }

    public String getAbout_us_contact() {
        return about_us_contact;
    }

    public String getAbout_us_email() {
        return about_us_email;
    }

    public String getAbout_us_developer() {
        return about_us_developer;
    }

    public String getAbout_us_description() {
        return about_us_description;
    }
}
