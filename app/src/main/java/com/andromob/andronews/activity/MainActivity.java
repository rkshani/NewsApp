package com.andromob.andronews.activity;

import static com.andromob.andronews.utils.FcmReceiver.initFCM;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.fragments.HomeFragment;
import com.andromob.andronews.fragments.NewsFragment;
import com.andromob.andronews.fragments.ProfileFragment;
import com.andromob.andronews.fragments.SearchFragment;
import com.andromob.andronews.fragments.VideosFragment;
import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.Constant;
import com.andromob.andronews.utils.DefaultExceptionHandler;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class MainActivity extends AppCompatActivity {
    private FrameLayout adContainerView;
    public static FragmentManager fragmentManager;
    public static BottomNavigationView bottomNavigationView;
    Methods methods;
    Prefs prefs;
    ConsentInformation consentInformation;
    AdsManager adsManager;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        prefs = Prefs.getInstance(this);
        methods = new Methods(this);
        fragmentManager = getSupportFragmentManager();
        adsManager = AdsManager.getInstance(this);

        adContainerView = findViewById(R.id.adContainerView);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        if (!prefs.isSubscribed()) {
            initFCM(this);
        }

        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    BuildConfig.APPLICATION_ID, //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        if (prefs.getAppSetting().get(0).getAd_network().equalsIgnoreCase(Constant.AD_TYPE_ADMOB)) {
            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId("686142CDA861074B93830E2B283E14C7")
                    .build();

            ConsentRequestParameters params = new ConsentRequestParameters
                    .Builder()
                  //  .setConsentDebugSettings(debugSettings)
                    .setTagForUnderAgeOfConsent(false)
                    .build();

            consentInformation = UserMessagingPlatform.getConsentInformation(this);

            if (consentInformation.canRequestAds()) {
                adsManager.initializeAds(MyApplication.getInstance());
                adsManager.loadInterAd(this);
                if (prefs.getIsShowConsentDialog()){
                    updateAds();
                }
                prefs.isShowConsentDialog(false);
            } else {
                consentInformation.requestConsentInfoUpdate(
                        this,
                        params,
                        (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                                    this,
                                    (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                        if (loadAndShowError != null) {
                                            // Consent gathering failed.
                                            Log.w("Consent Info", String.format("%s: %s",
                                                    loadAndShowError.getErrorCode(),
                                                    loadAndShowError.getMessage()));
                                        }

                                        // Consent has been gathered.
                                        if (consentInformation.canRequestAds()) {
                                            adsManager.initializeAds(MyApplication.getInstance());
                                            adsManager.loadInterAd(this);
                                            if (prefs.getIsShowConsentDialog()){
                                                updateAds();
                                            }
                                            prefs.isShowConsentDialog(false);
                                        }
                                    }
                            );
                        },
                        (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                            // Consent gathering failed.
                            Log.w("Consent Fail Info", String.format("%s: %s",
                                    requestConsentError.getErrorCode(),
                                    requestConsentError.getMessage()));
                        });
            }
        } else {
            adsManager.initializeAds(MyApplication.getInstance());
            adsManager.loadInterAd(this);
        }

        initBottomNavigation();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
        checkForUpdate();
        if (prefs.getAppSetting().get(0).isNews_reward_status()) {
            methods.firstTimeRewardDialog();
        }

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    public void initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.bottom_home) {
                    displaySelectedFragment(new HomeFragment());
                } else if (id == R.id.bottom_video) {
                    displaySelectedFragment(new VideosFragment());
                } else if (id == R.id.bottom_fav) {
                    Fragment fragment = NewsFragment.newInstance("favorite", -1);
                    displaySelectedFragment(fragment);
                } else if (id == R.id.bottom_search) {
                    displaySelectedFragment(new SearchFragment());
                } else if (id == R.id.bottom_profile) {
                    displaySelectedFragment(new ProfileFragment());
                }
                return true;
            }
        });
    }

    public static void setBottomItemHome(String which) {
        if (bottomNavigationView != null) {
            if (which.equals("home")) {
                bottomNavigationView.getMenu().findItem(R.id.bottom_home).setChecked(true);
            } else if (which.equals("video")) {
                bottomNavigationView.getMenu().findItem(R.id.bottom_video).setChecked(true);
            } else if (which.equals("search")) {
                bottomNavigationView.getMenu().findItem(R.id.bottom_search).setChecked(true);
            } else if (which.equals("fav")) {
                bottomNavigationView.getMenu().findItem(R.id.bottom_fav).setChecked(true);
            } else if (which.equals("profile")) {
                bottomNavigationView.getMenu().findItem(R.id.bottom_profile).setChecked(true);
            }
        }
    }

    private void showExitDialog() {
        final Dialog exit_dialog = new Dialog(this);
        exit_dialog.setContentView(R.layout.item_exit_dialog);
        FrameLayout adContainerView;
        ImageView closebtn = exit_dialog.findViewById(R.id.close_btn);
        Button no_btn = exit_dialog.findViewById(R.id.no_btn);
        Button yes_btn = exit_dialog.findViewById(R.id.yes_btn);
        adContainerView = exit_dialog.findViewById(R.id.adContainerView);
        AdsManager.getInstance(this).showBigBannerAd(this, adContainerView);
        exit_dialog.show();
        exit_dialog.setCanceledOnTouchOutside(false);
        exit_dialog.setCancelable(false);
        exit_dialog.getWindow().getAttributes().width = FrameLayout.LayoutParams.MATCH_PARENT;
        exit_dialog.getWindow().getAttributes().height = FrameLayout.LayoutParams.MATCH_PARENT;
        exit_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exit_dialog.setOnDismissListener(dialog -> AdsManager.getInstance(MainActivity.this).destroyBigBannerAds());
        closebtn.setOnClickListener(v -> {
            exit_dialog.dismiss();
        });
        no_btn.setOnClickListener(v -> {
            exit_dialog.dismiss();
        });
        yes_btn.setOnClickListener(v -> {
            exit_dialog.dismiss();
            finish();
        });
    }

    private void displaySelectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        //fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() >= 1) {
            fragmentManager.popBackStack();
        } else {
            showExitDialog();
        }
    }

    void checkForUpdate() {
        if (prefs.getAppSetting().get(0).isApp_update_status()) {
            if (!prefs.getAppSetting().get(0).getApp_new_version().equals(BuildConfig.VERSION_NAME)) {
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        methods.showUpdateDialog(prefs.getAppSetting().get(0).getApp_update_desc(), prefs.getAppSetting().get(0).getApp_redirect_url());
                    }
                };
                handler.postDelayed(r, 5000);
            }
        }
    }

    private void updateAds(){
        adsManager.showBannerAd(this, adContainerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getAppSetting().get(0).getAd_network().equalsIgnoreCase(Constant.AD_TYPE_ADMOB)) {
            if (!prefs.getIsShowConsentDialog()){
                updateAds();
            }
        } else {
            updateAds();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        adsManager.destroyBannerAds();
    }

    @Override
    protected void onDestroy() {
        adsManager.destroyInterAds();
        super.onDestroy();
    }
}
