package com.andromob.andronews.utils;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Constant implements Serializable {
    public static final String AD_TYPE_ADMOB = "admob", AD_TYPE_FACEBOOK = "facebook", AD_TYPE_STARTAPP = "startapp", AD_TYPE_APPLOVIN = "applovin";

    public static Bitmap profileBitmap = null;
    public static int newCommentCount = 0;
    public static boolean isCommentMadeFinal = false;
    public static final int defaultTabs = 2;
}