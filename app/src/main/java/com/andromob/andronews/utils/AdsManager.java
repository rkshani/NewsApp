package com.andromob.andronews.utils;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.MyApplication;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkUtils;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.Mrec;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AdsManager {
    public static int inter_i = 0;
    public static InterstitialAd googleFullscreen;
    public static MaxInterstitialAd maxInterstitialAd;
    private StartAppAd startAppAd;

    private NativeAd mnativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private MaxAd maxNativeAd;
    private StartAppNativeAd nativeAd;
    private List<NativeAdDetails>
            nativeAdsStartApp = new ArrayList<>();
    private com.facebook.ads.InterstitialAd interstitialAd;
    private com.facebook.ads.NativeAd mNativeAdFB;

    private static AdsManager mInstance;
    private static Prefs prefs;

    AdView admobBigBanner;
    MaxAdView maxBigBanner;
    com.facebook.ads.AdView fbBigBanner;
    Banner startAppSmallBanner;

    AdView admobSmallBanner;
    MaxAdView maxSmallBanner;
    com.facebook.ads.AdView fbSmallBanner;
    Mrec startAppBigBanner;

    public static synchronized AdsManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AdsManager(context);
        }
        return mInstance;
    }

    public AdsManager(Context context) {
        prefs = Prefs.getInstance(context);
    }

    public interface InterAdListener {
        void onClick(String type);
    }

    public void initializeAds(MyApplication context) {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            switch (prefs.getAppSetting().get(0).getAd_network()) {
                case Constant.AD_TYPE_ADMOB:
                    MobileAds.initialize(context, initializationStatus -> {
                    });
                    AudienceNetworkAds.initialize(context);
                    break;
                case Constant.AD_TYPE_FACEBOOK:
                    AudienceNetworkAds.initialize(context);
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    AppLovinSdk.getInstance(context).setMediationProvider("max");
                    AppLovinSdk.initializeSdk(context, new AppLovinSdk.SdkInitializationListener() {
                        @Override
                        public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                        }
                    });
                    AppLovinSdk.getInstance(context).getSettings().setTestDeviceAdvertisingIds(Arrays.asList("f6335ae2-79a7-4287-841e-ccad39f5134e"));
                    AudienceNetworkAds.initialize(context);
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    StartAppSDK.setUserConsent(context,
                            "pas",
                            System.currentTimeMillis(),
                            false);
                    StartAppSDK.init(context, prefs.getAppSetting().get(0).getStartapp_app_id(), false);
                    StartAppAd.disableSplash();
                    break;
            }
        }
    }

    public void showBannerAd(Activity activity, FrameLayout adContainerView) {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            switch (prefs.getAppSetting().get(0).getAd_network()) {
                case Constant.AD_TYPE_ADMOB:
                    loadAdMOBBanner(activity, adContainerView);
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    showMaxBannerAd(activity, adContainerView);
                    break;
                case Constant.AD_TYPE_FACEBOOK:
                    showFBanner(activity, adContainerView);
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    showStartAppBanner(activity, adContainerView);
                    break;
            }
        } else {
            adContainerView.setVisibility(GONE);
        }
    }

    public void showBigBannerAd(Activity activity, FrameLayout adContainerView) {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            switch (prefs.getAppSetting().get(0).getAd_network()) {
                case Constant.AD_TYPE_ADMOB:
                    showAdMOBBigBanner(activity, adContainerView);
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    showMaxBigBannerAd(activity, adContainerView);
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    showStartAppBigBanner(activity, adContainerView);
                    break;
                case Constant.AD_TYPE_FACEBOOK:
                    showFbBigBanner(activity, adContainerView);
                    break;
            }
        } else {
            adContainerView.setVisibility(GONE);
        }
    }

    public void showNativeAd(Activity activity, FrameLayout adContainerViewNative) {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            switch (prefs.getAppSetting().get(0).getAd_network()) {
                case Constant.AD_TYPE_ADMOB:
                    showNativeAdAdMob(activity, false, adContainerViewNative);
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    showMaxNativeAd(false, activity, adContainerViewNative);
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    showStartAppNativeAd(false, activity, adContainerViewNative);
                    break;
                case Constant.AD_TYPE_FACEBOOK:
                    showNativeAdFb(false, activity, adContainerViewNative);
                    break;
            }
        } else {
            adContainerViewNative.setVisibility(GONE);
        }
    }

    public void showNativeAdVideo(Activity activity, FrameLayout adContainerViewNative) {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            switch (prefs.getAppSetting().get(0).getAd_network()) {
                case Constant.AD_TYPE_ADMOB:
                    showNativeAdAdMob(activity, true, adContainerViewNative);
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    showMaxNativeAd(true, activity, adContainerViewNative);
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    showStartAppNativeAd(true, activity, adContainerViewNative);
                    break;
                case Constant.AD_TYPE_FACEBOOK:
                    showNativeAdFb(true, activity, adContainerViewNative);
                    break;
            }
        } else {
            adContainerViewNative.setVisibility(GONE);
        }
    }

    public void showInterAdOnClick(Activity activity, InterAdListener interAdListener, final String type) {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            if (inter_i == prefs.getAppSetting().get(0).getInter_clicks()) {
                inter_i = 0;
                switch (prefs.getAppSetting().get(0).getAd_network()) {
                    case Constant.AD_TYPE_ADMOB:
                        if (googleFullscreen != null) {
                            googleFullscreen.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    interAdListener.onClick(type);
                                    loadInterAd(activity);
                                    super.onAdDismissedFullScreenContent();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    interAdListener.onClick(type);
                                    loadInterAd(activity);
                                    super.onAdFailedToShowFullScreenContent(adError);
                                }
                            });
                            googleFullscreen.show(activity);
                        } else {
                            interAdListener.onClick(type);
                            loadInterAd(activity);
                        }
                        break;
                    case Constant.AD_TYPE_APPLOVIN:
                        if (maxInterstitialAd != null) {
                            if (maxInterstitialAd.isReady()) {
                                maxInterstitialAd.setListener(new MaxAdListener() {
                                    @Override
                                    public void onAdLoaded(MaxAd ad) {

                                    }

                                    @Override
                                    public void onAdDisplayed(MaxAd ad) {

                                    }

                                    @Override
                                    public void onAdHidden(MaxAd ad) {
                                        interAdListener.onClick(type);
                                        loadInterAd(activity);
                                    }

                                    @Override
                                    public void onAdClicked(MaxAd ad) {

                                    }

                                    @Override
                                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                                        interAdListener.onClick(type);
                                        loadInterAd(activity);
                                    }

                                    @Override
                                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                        interAdListener.onClick(type);
                                        loadInterAd(activity);
                                    }
                                });
                                maxInterstitialAd.showAd(prefs.getAppSetting().get(0).getApplovin_inter());
                            } else {
                                interAdListener.onClick(type);
                                loadInterAd(activity);
                            }
                        } else {
                            interAdListener.onClick(type);
                            loadInterAd(activity);
                        }
                        break;
                    case Constant.AD_TYPE_STARTAPP:
                        if (startAppAd != null) {
                            startAppAd.showAd(new AdDisplayListener() {
                                @Override
                                public void adHidden(Ad ad) {
                                    interAdListener.onClick(type);
                                    loadInterAd(activity);
                                }

                                @Override
                                public void adDisplayed(Ad ad) {

                                }

                                @Override
                                public void adClicked(Ad ad) {

                                }

                                @Override
                                public void adNotDisplayed(Ad ad) {
                                    interAdListener.onClick(type);
                                    loadInterAd(activity);
                                }
                            });
                        } else {
                            interAdListener.onClick(type);
                            loadInterAd(activity);
                        }
                        break;
                    case Constant.AD_TYPE_FACEBOOK:
                        if (interstitialAd != null) {
                            if (interstitialAd.isAdLoaded()) {
                                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                                    @Override
                                    public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                                    }

                                    @Override
                                    public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                                        interAdListener.onClick(type);
                                        loadInterAd(activity);
                                    }

                                    @Override
                                    public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {

                                    }

                                    @Override
                                    public void onAdLoaded(com.facebook.ads.Ad ad) {

                                    }

                                    @Override
                                    public void onAdClicked(com.facebook.ads.Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(com.facebook.ads.Ad ad) {

                                    }
                                };
                                interstitialAd.buildLoadAdConfig()
                                        .withAdListener(interstitialAdListener)
                                        .build();
                                interstitialAd.show();
                            } else {
                                interAdListener.onClick(type);
                                loadInterAd(activity);
                            }
                        } else {
                            interAdListener.onClick(type);
                            loadInterAd(activity);
                        }
                        break;
                }
            } else {
                inter_i++;
                interAdListener.onClick(type);
            }
        } else {
            interAdListener.onClick(type);
        }
    }

    public void loadInterAd(Activity activity) {
        if (prefs.getAppSetting() != null && !prefs.getAppSetting().isEmpty()) {
            if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
                switch (prefs.getAppSetting().get(0).getAd_network()) {
                    case Constant.AD_TYPE_ADMOB:
                        InterstitialAd.load(
                                activity,
                                prefs.getAppSetting().get(0).getAdmob_inter(),
                                new AdRequest.Builder().build(),
                                new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                                        googleFullscreen = ad;
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                        googleFullscreen = null;
                                        inter_i = 0;
                                        //Toast.makeText(activity, "Ad Failed to load", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        break;
                    case Constant.AD_TYPE_APPLOVIN:
                        maxInterstitialAd = new MaxInterstitialAd(prefs.getAppSetting().get(0).getApplovin_inter(), activity);
                        maxInterstitialAd.loadAd();
                        break;
                    case Constant.AD_TYPE_STARTAPP:
                        startAppAd = new StartAppAd(activity);
                        startAppAd.loadAd(new AdEventListener() {
                            @Override
                            public void onReceiveAd(@NonNull Ad ad) {
                                //  Toast.makeText(activity, "AD LOADED", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailedToReceiveAd(@Nullable Ad ad) {
                                //Toast.makeText(activity, "AD FAILED TO LOAD" + ad.getErrorMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case Constant.AD_TYPE_FACEBOOK:
                        interstitialAd = new com.facebook.ads.InterstitialAd(activity, prefs.getAppSetting().get(0).getFacebook_inter());
                        interstitialAd.loadAd(
                                interstitialAd.buildLoadAdConfig()
                                        .build());
                        break;
                }
            }
        }
    }

    public void loadAdMOBBanner(Activity activity, FrameLayout adContainerView) {
        adContainerView.setVisibility(GONE);
        admobSmallBanner = new AdView(activity);
        admobSmallBanner.setAdUnitId(prefs.getAppSetting().get(0).getAdmob_small_banner());
        AdRequest adRequest =
                new AdRequest.Builder().build();
        AdSize adSize = getAdSize(activity);
        admobSmallBanner.setAdSize(adSize);
        admobSmallBanner.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adContainerView.setVisibility(GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainerView.removeAllViews();
                adContainerView.addView(admobSmallBanner);
                adContainerView.setVisibility(View.VISIBLE);
            }
        });
        admobSmallBanner.loadAd(adRequest);
    }

    public AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public void showMaxBannerAd(Activity activity, FrameLayout frameLayout) {
        frameLayout.setVisibility(GONE);
        maxSmallBanner = new MaxAdView(prefs.getAppSetting().get(0).getApplovin_small_banner(), activity);
        maxSmallBanner.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                frameLayout.setVisibility(GONE);
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(activity).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx(activity, heightDp);

        maxSmallBanner.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        maxSmallBanner.setExtraParameter("adaptive_banner", "true");
        frameLayout.addView(maxSmallBanner);
        maxSmallBanner.loadAd();
    }

    public void showStartAppBanner(Activity activity, FrameLayout frameLayout) {
        startAppSmallBanner = new Banner(activity);
        startAppSmallBanner.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLayout.addView(startAppSmallBanner);
        startAppSmallBanner.loadAd();
    }

    public void showAdMOBBigBanner(Activity activity, FrameLayout adContainerView) {
        adContainerView.setVisibility(GONE);
        admobBigBanner = new AdView(activity);
        admobBigBanner.setAdUnitId(prefs.getAppSetting().get(0).getAdmob_medium_banner());
        AdRequest adRequest =
                new AdRequest.Builder().build();
        admobBigBanner.setAdSize(AdSize.MEDIUM_RECTANGLE);
        admobBigBanner.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adContainerView.setVisibility(GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainerView.removeAllViews();
                adContainerView.addView(admobBigBanner);
                adContainerView.setVisibility(View.VISIBLE);
            }
        });
        admobBigBanner.loadAd(adRequest);
    }

    public void showMaxBigBannerAd(Activity activity, FrameLayout frameLayout) {
        frameLayout.setVisibility(GONE);
        maxBigBanner = new MaxAdView(prefs.getAppSetting().get(0).getApplovin_medium_banner(), activity);
        maxBigBanner.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                frameLayout.setVisibility(GONE);
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        int widthPx = AppLovinSdkUtils.dpToPx(activity, 300);
        int heightPx = AppLovinSdkUtils.dpToPx(activity, 250);
        maxBigBanner.setLayoutParams(new FrameLayout.LayoutParams(widthPx, heightPx));
        frameLayout.addView(maxBigBanner);
        maxBigBanner.loadAd();
    }

    public void showStartAppBigBanner(Activity activity, FrameLayout frameLayout) {
        startAppBigBanner = new Mrec(activity);
        startAppBigBanner.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLayout.addView(startAppBigBanner);
        startAppBigBanner.loadAd();
    }

    public void showMaxNativeAd(boolean isVideo, Activity activity, FrameLayout nativeAdContainer) {
        nativeAdLoader = new MaxNativeAdLoader(prefs.getAppSetting().get(0).getApplovin_native(), activity);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                // Clean up any pre-existing native ad to prevent memory leaks.
                if (maxNativeAd != null) {
                    nativeAdLoader.destroy(maxNativeAd);
                }
                maxNativeAd = ad;
                // Save ad for cleanup.

                // Add ad view to view.
                nativeAdContainer.removeAllViews();
                nativeAdContainer.addView(nativeAdView);
                nativeAdContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                // We recommend retrying with exponentially higher delays up to a maximum delay
            }

            @Override
            public void onNativeAdClicked(final MaxAd ad) {
                // Optional click callback
            }
        });

        nativeAdLoader.loadAd(createNativeAdView(isVideo, activity));
    }

    public MaxNativeAdView createNativeAdView(boolean isvideo, Activity activity) {
        if (isvideo) {
            MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(R.layout.ads_native_max_video)
                    .setTitleTextViewId(R.id.title_text_view)
                    .setBodyTextViewId(R.id.body_text_view)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build();
            return new MaxNativeAdView(binder, activity);
        } else {
            MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(R.layout.ads_native_max)
                    .setTitleTextViewId(R.id.title_text_view)
                    .setBodyTextViewId(R.id.body_text_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build();

            return new MaxNativeAdView(binder, activity);
        }
    }

    public void showNativeAdAdMob(Activity activity, boolean isVideo, FrameLayout adContainerNative) {
        adContainerNative.setVisibility(GONE);
        AdLoader.Builder builder = new AdLoader.Builder(activity, prefs.getAppSetting().get(0).getAdmob_native())
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeAdView adView;
                        if (isVideo) {
                            adView = (NativeAdView) activity.getLayoutInflater()
                                    .inflate(R.layout.ads_native_admob_video, null);
                        } else {
                            adView = (NativeAdView) activity.getLayoutInflater()
                                    .inflate(R.layout.ads_native_admob, null);
                        }
                        if (mnativeAd != null) {
                            mnativeAd.destroy();
                        }
                        mnativeAd = nativeAd;
                        populateNativeAdView(adView, mnativeAd);
                        adContainerNative.removeAllViews();
                        adContainerNative.addView(adView);
                        adContainerNative.setVisibility(View.VISIBLE);
                    }
                });

        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void populateNativeAdView(NativeAdView adView, NativeAd nativeAd) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
    }

    public void showStartAppNativeAd(boolean isVideo, Activity activity, FrameLayout frameLayout) {
        nativeAd = new StartAppNativeAd(activity);

        nativeAd.loadAd(new NativeAdPreferences()
                .setAdsNumber(3)
                .setAutoBitmapDownload(true)
                .setPrimaryImageSize(2), new AdEventListener() {
            @Override
            public void onReceiveAd(@NonNull Ad ad) {
                nativeAdsStartApp = nativeAd.getNativeAds();
                int i = new Random().nextInt(nativeAdsStartApp.size() - 1);
                RelativeLayout nativeAdView = null;
                if (isVideo) {
                    nativeAdView = (RelativeLayout) (activity).getLayoutInflater().inflate(R.layout.ads_native_startapp_video, null);
                } else {
                    nativeAdView = (RelativeLayout) (activity).getLayoutInflater().inflate(R.layout.ads_native_startapp, null);
                }
                populateStartAppNativeAdView(nativeAdsStartApp.get(i), nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
            }
        });
    }

    public void populateStartAppNativeAdView(NativeAdDetails nativeAdDetails, RelativeLayout nativeAdView) {
        ImageView icon = nativeAdView.findViewById(R.id.icon);
        TextView title = nativeAdView.findViewById(R.id.title);
        TextView description = nativeAdView.findViewById(R.id.description);
        Button button = nativeAdView.findViewById(R.id.button);
        icon.setImageBitmap(nativeAdDetails.getImageBitmap());
        title.setText(nativeAdDetails.getTitle());
        description.setText(nativeAdDetails.getDescription());
        button.setText(nativeAdDetails.isApp() ? "Install" : "Open");

    }

    public void showFBanner(Activity activity, FrameLayout adContainerView) {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            fbSmallBanner = new com.facebook.ads.AdView(activity, prefs.getAppSetting().get(0).getFacebook_small_banner(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            adContainerView.setVisibility(View.VISIBLE);
            adContainerView.removeAllViews();
            adContainerView.addView(fbSmallBanner);
            fbSmallBanner.loadAd();
        } else {
            adContainerView.setVisibility(View.GONE);
        }
    }

    public void showFbBigBanner(Activity activity, FrameLayout adContainerView) {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            fbBigBanner = new com.facebook.ads.AdView(activity, prefs.getAppSetting().get(0).getFacebook_medium_banner(), com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250);
            adContainerView.setVisibility(View.VISIBLE);
            adContainerView.removeAllViews();
            adContainerView.addView(fbBigBanner);
            fbBigBanner.loadAd();
        } else {
            adContainerView.setVisibility(View.GONE);
        }
    }

    public void showNativeAdFb(boolean isVideo, Activity activity, FrameLayout frameLayout) {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            mNativeAdFB = new com.facebook.ads.NativeAd(activity, prefs.getAppSetting().get(0).getFacebook_native());

            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(com.facebook.ads.Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e(TAG, "Native ad finished downloading all assets.");
                }

                @Override
                public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                    // Native ad failed to load
                    Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(com.facebook.ads.Ad ad) {
                    Log.d(TAG, "Native ad is Loaded!");
                    if (mNativeAdFB == null || mNativeAdFB != ad) {
                        return;
                    }
                    frameLayout.setVisibility(View.VISIBLE);
                    if (isVideo) {
                        populateFbNativeAdViewVideo(activity, frameLayout, mNativeAdFB);
                    } else {
                        View adView = com.facebook.ads.NativeAdView.render(activity, mNativeAdFB);
                        frameLayout.addView(adView, new ViewGroup.LayoutParams(MATCH_PARENT, 800));
                    }
                }

                @Override
                public void onAdClicked(com.facebook.ads.Ad ad) {
                    // Native ad clicked
                    Log.d(TAG, "Native ad clicked!");
                }

                @Override
                public void onLoggingImpression(com.facebook.ads.Ad ad) {
                    // Native ad impression
                    Log.d(TAG, "Native ad impression logged!");
                }
            };

            mNativeAdFB.loadAd(
                    mNativeAdFB.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                            .build());

        } else {
            frameLayout.setVisibility(View.GONE);
        }
    }

    public void populateFbNativeAdViewVideo(Activity activity, FrameLayout frameLayout, com.facebook.ads.NativeAd nativeAd) {
        nativeAd.unregisterView();
        // Add the Ad view into the ad container.
        NativeAdLayout nativeAdLayout = null;
        nativeAdLayout = (NativeAdLayout) activity.getLayoutInflater()
                .inflate(R.layout.ads_native_fb_video, null);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        frameLayout.removeAllViews();
        frameLayout.addView(nativeAdLayout);

        // Add the AdOptionsView
        RelativeLayout adChoicesContainer = activity.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.

        TextView nativeAdTitle = nativeAdLayout.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = nativeAdLayout.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = nativeAdLayout.findViewById(R.id.native_ad_sponsored_label);
        MediaView nativeAdIconView = nativeAdLayout.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = nativeAdLayout.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        // Set the Text.
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                nativeAdLayout, nativeAdIconView, clickableViews);
    }

    public void destroyBannerAds() {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            switch (prefs.getAppSetting().get(0).getAd_network()) {
                case Constant.AD_TYPE_ADMOB:
                    if (admobSmallBanner != null) {
                        admobSmallBanner.destroy();
                    }
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    if (maxSmallBanner != null) {
                        maxSmallBanner.removeAllViews();
                        maxSmallBanner.destroy();
                    }
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    if (startAppSmallBanner != null) {
                        startAppSmallBanner.hideBanner();
                    }
                    break;
                case Constant.AD_TYPE_FACEBOOK:
                    if (fbSmallBanner != null) {
                        fbSmallBanner.removeAllViews();
                        fbSmallBanner.destroy();
                    }
                    break;
            }
        }
    }

    public void destroyBigBannerAds() {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            switch (prefs.getAppSetting().get(0).getAd_network()) {
                case Constant.AD_TYPE_ADMOB:
                    if (admobBigBanner != null) {
                        admobBigBanner.removeAllViews();
                        admobBigBanner.destroy();
                    }
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    if (maxBigBanner != null) {
                        maxBigBanner.removeAllViews();
                        maxBigBanner.destroy();
                    }
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    if (startAppBigBanner != null) {
                        startAppBigBanner.hideBanner();
                    }
                    break;
                case Constant.AD_TYPE_FACEBOOK:
                    if (fbBigBanner != null) {
                        fbBigBanner.removeAllViews();
                        fbBigBanner.destroy();
                    }
                    break;
            }
        }
    }

    public void destroyInterAds() {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            switch (prefs.getAppSetting().get(0).getAd_network()) {
                case Constant.AD_TYPE_ADMOB:
                    if (googleFullscreen != null) {
                        googleFullscreen = null;
                    }
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    if (maxInterstitialAd != null) {
                        maxInterstitialAd.destroy();
                        maxInterstitialAd = null;
                    }
                    break;
                case Constant.AD_TYPE_FACEBOOK:
                    if (interstitialAd != null) {
                        interstitialAd.destroy();
                        interstitialAd = null;
                    }
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    if (startAppAd != null) {
                        startAppAd = null;
                    }
                    break;
            }
        }
    }

    public void destroyNativeAds() {
        if (prefs.getAppSetting().get(0).getAd_status().equalsIgnoreCase("on")) {
            switch (prefs.getAppSetting().get(0).getAd_network()) {
                case Constant.AD_TYPE_ADMOB:
                    if (mnativeAd != null) {
                        mnativeAd.destroy();
                    }
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    if (nativeAdLoader != null) {
                        if (maxNativeAd != null) {
                            nativeAdLoader.destroy(maxNativeAd);
                        }
                    }
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    if (nativeAdsStartApp != null) {
                        nativeAd.getNativeAds().clear();
                        nativeAdsStartApp.clear();
                    }
                    break;
                case Constant.AD_TYPE_FACEBOOK:
                    if (mNativeAdFB != null) {
                        mNativeAdFB.destroy();
                        mNativeAdFB.unregisterView();
                    }
                    break;
            }
        }
    }
}
