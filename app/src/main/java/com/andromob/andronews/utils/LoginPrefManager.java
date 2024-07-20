package com.andromob.andronews.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.andromob.andronews.activity.LoginActivity;
import com.andromob.andronews.models.User;

@SuppressLint("StaticFieldLeak")
public class LoginPrefManager {

    private static final String SHARED_PREF_NAME = "sharedpref";
    private static final String KEY_USERTYPE = "keyusertype";
    private static final String KEY_NAME = "keyname";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_IMGURL = "keyimgurl";
    private static final String KEY_PHONE = "keyphone";
    private static final String KEY_AUTHID = "keyauthid";
    private static final String KEY_REGISTERED_ON = "keyregisteredon";
    private static final String KEY_FAV_IDS = "keyfavids";
    private static final String KEY_ID = "keyid";

    private static LoginPrefManager mInstance;
    private static Context mCtx;

    private LoginPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized LoginPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LoginPrefManager(context);
        }
        return mInstance;
    }

    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getUser_id());
        editor.putString(KEY_USERTYPE, user.getUser_type());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_IMGURL, user.getImg_url());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_AUTHID, user.getAuth_id());
        editor.putString(KEY_REGISTERED_ON, user.getRegistered_on());
        editor.putString(KEY_FAV_IDS, user.getFav_ids());
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME, null) != null;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_USERTYPE, null),
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_IMGURL, null),
                sharedPreferences.getString(KEY_PHONE, null),
                sharedPreferences.getString(KEY_AUTHID, null),
                sharedPreferences.getString(KEY_REGISTERED_ON, null),
                sharedPreferences.getString(KEY_FAV_IDS, "")
                );
    }

    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}