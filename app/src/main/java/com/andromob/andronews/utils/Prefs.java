package com.andromob.andronews.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

import com.andromob.andronews.models.Settings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Prefs {
    private String APP_PREFS = "app_prefs";
    private SharedPreferences.Editor editor;
    private final SharedPreferences sharedPreferences;
    private static Prefs mInstance;
    private static Gson gson;

    public static synchronized Prefs getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Prefs(context);
            gson = new Gson();
        }
        return mInstance;
    }

    public Prefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public void saveAppSettings(List<Settings> dataList){
        String json = gson.toJson(dataList);
        this.editor.putString("appSettingsList", json);
        this.editor.apply();
    }

    public ArrayList<Settings> getAppSetting(){
        String json = sharedPreferences.getString("appSettingsList", null);
        Type type = new TypeToken<ArrayList<Settings>>() {}.getType();
        ArrayList<Settings> tempDataList;
        tempDataList = gson.fromJson(json, type);
        return tempDataList;
    }


    public void setTextSize(int i) {
        this.editor.putInt("text_size", i);
        this.editor.apply();
    }

    public int getTextSize() {
        return this.sharedPreferences.getInt("text_size", 2);
    }

    public void addFavorite(String str){
        this.editor.putString("favorite_list", str);
        this.editor.apply();
    }

    public void isFirstTime(Boolean b){
        this.editor.putBoolean("isFirstTime", b);
        this.editor.apply();
    }

    public boolean getIsFirstTime(){
        return this.sharedPreferences.getBoolean("isFirstTime", true);
    }

    public void isFirstReward(Boolean b){
        this.editor.putBoolean("isFirstReward", b);
        this.editor.apply();
    }

    public boolean getIsFirstReward(){
        return this.sharedPreferences.getBoolean("isFirstReward", true);
    }

    public String getFavorite(){
        return this.sharedPreferences.getString("favorite_list", "");
    }

    public void setAppCheck(boolean isAppCheck){
        this.editor.putBoolean("app_check", isAppCheck);
        this.editor.apply();
    }

    public boolean isAppCheckDone(){
        return this.sharedPreferences.getBoolean("app_check", false);
    }

    public void setSubscribed(boolean value){
        this.editor.putBoolean("fcm_subscription", value);
        this.editor.apply();
    }
    public Boolean isSubscribed() {
        return this.sharedPreferences.getBoolean("fcm_subscription", false);
    }

    public void isShowConsentDialog(Boolean b){
        this.editor.putBoolean("isShowConsentDialog", b);
        this.editor.apply();
    }

    public boolean getIsShowConsentDialog(){
        return this.sharedPreferences.getBoolean("isShowConsentDialog", true);
    }

}
