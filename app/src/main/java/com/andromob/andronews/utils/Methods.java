package com.andromob.andronews.utils;

import static com.andromob.andronews.activity.MainActivity.fragmentManager;
import static com.andromob.andronews.utils.Constant.profileBitmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.andromob.andronews.activity.MyApplication;
import com.andromob.andronews.models.Settings;
import com.andromob.andronews.models.VP;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.activity.LoginActivity;
import com.andromob.andronews.activity.MainActivity;
import com.andromob.andronews.api.Api;
import com.andromob.andronews.api.ApiClient;
import com.andromob.andronews.interfaces.CommentListener;
import com.andromob.andronews.interfaces.DialogClickListener;
import com.andromob.andronews.models.ModelPost;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Methods {
    private final static String reg = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";
    Context context;
    DialogClickListener dialogClickListener;
    CommentListener commentListener;

    public Methods(Context context) {
        this.context = context;
    }

    public Methods(Context context, DialogClickListener dialogClickListener) {
        this.context = context;
        this.dialogClickListener = dialogClickListener;
    }

    public Methods(Context context, CommentListener commentListener) {
        this.context = context;
        this.commentListener = commentListener;
    }

    public String getVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().length() <= 0)
            return null;

        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);

        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    public static Api getApi() {
        return ApiClient.getApiClient().create(Api.class);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isYoutubeUrl(String youTubeURl) {
        boolean success;
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        success = !youTubeURl.isEmpty() && youTubeURl.matches(pattern);
        return success;
    }

    public ActionBar getActionBar() {
        return ((MainActivity) context).getSupportActionBar();
    }

    public void errorDialog(String title, String message) {
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder((Activity) context, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();
            }
        });
        alertDialog.show();
    }

    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.setPackage("com.google.android.gm");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.about_us_email_text)});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        context.startActivity(intent);
    }

    public void ratePLaystore() {
        Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
        }
    }

    public void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
        String shareMessage = context.getString(R.string.share_app_text);
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        context.startActivity(Intent.createChooser(shareIntent, "choose one"));
    }

    public void goToYoutube() {
        context.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/" + Prefs.getInstance(context).getAppSetting().get(0).getYoutube_username())));
    }

    public void gotoFB() {
        Uri uri = Uri.parse("fb://page/" + Prefs.getInstance(context).getAppSetting().get(0).getFacebook_id());
        Intent data = new Intent(Intent.ACTION_VIEW, uri);
        data.setPackage("com.facebook.katana");

        try {
            context.startActivity(data);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/" + Prefs.getInstance(context).getAppSetting().get(0).getFacebook_username())));
        }
    }

    public void gotoinstagram() {
        Uri uri = Uri.parse("https://www.instagram.com/" + Prefs.getInstance(context).getAppSetting().get(0).getInstagram_username());
        Intent data = new Intent(Intent.ACTION_VIEW, uri);
        data.setPackage("com.instagram.android");

        try {
            context.startActivity(data);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/" + Prefs.getInstance(context).getAppSetting().get(0).getInstagram_username())));
        }
    }

    public void joinTelegram() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + Prefs.getInstance(context).getAppSetting().get(0).getTelegram_username()));
            context.startActivity(intent);
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/" + Prefs.getInstance(context).getAppSetting().get(0).getTelegram_username())));

        }
    }

    public void gotoTwitter() {
        if (context != null) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/" + Prefs.getInstance(context).getAppSetting().get(0).getTwitter_username())));
        }
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public void changeStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorWhite));
            window.setNavigationBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        }
    }

    public void showDialog(Activity activity, String title, String message, String type, String action) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.item_awesome_dialog);
        TextView dialog_title = dialog.findViewById(R.id.dialog_title);
        TextView dialog_message = dialog.findViewById(R.id.dialog_message);
        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        ImageView dialog_icon = dialog.findViewById(R.id.dialog_icon);
        dialog_title.setText(title);
        dialog_message.setText(message);
        btnCancel.setVisibility(View.GONE);

        switch (type) {
            case "success":
                btn_ok.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorSuccess));
                dialog_icon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_success));
                break;
            case "alert":
                btn_ok.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorAlert));
                dialog_icon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_alert));
                break;
            case "payment":
                btn_ok.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorAccent));
                dialog_icon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_alert));
                dialog_icon.setImageTintList(ContextCompat.getColorStateList(activity, R.color.colorAccent));
                break;
            case "reward":
                btn_ok.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorPrimaryDark));
                dialog_icon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_reward_box));
                break;
            case "login":
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setOnClickListener(view -> dialog.dismiss());
                btn_ok.setText(context.getString(R.string.login_now));
                btn_ok.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorSuccess));
                dialog_icon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_reward_box));
                break;
            case "redeem":
                dialog_message.setVisibility(View.GONE);
                btn_ok.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorSuccess));
                dialog_icon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_success));
                btn_ok.setText(R.string.redeem_points);
                break;
        }
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.rubber_band);
        dialog_icon.setAnimation(animation);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().width = FrameLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().getAttributes().height = FrameLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.equals("dismiss")) {
                    dialog.dismiss();
                } else if (!action.equals("failed")) {
                    dialog.dismiss();
                    dialogClickListener.onClick(action);
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    public void showUpdateDialog(String message, String app_link) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.update_dialog);
        TextView dialog_message = dialog.findViewById(R.id.dialog_message);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        ImageView dialog_icon = dialog.findViewById(R.id.dialog_icon);
        dialog_message.setText(message);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rubber_band);
        dialog_icon.setAnimation(animation);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().width = FrameLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().getAttributes().height = FrameLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(app_link)));
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            InputStream input;
            URL url = new URL(src);

            URLConnection conn = new URL(src).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            }
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    public void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).asBitmap().load(url).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).placeholder(R.drawable.user_img).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    public void loadNewsImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).asBitmap().load(url).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false).placeholder(R.drawable.placeholder_img).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        imageView.setVisibility(View.VISIBLE);
                        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }


    public void loadShapeableImageView(ShapeableImageView imageView, String url) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.user_img);
        Glide.with(imageView.getContext()).asBitmap().load(url).apply(options).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                profileBitmap = resource;
                imageView.setImageBitmap(resource);
                imageView.setVisibility(View.VISIBLE);
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    public void postView(String apicall, int id) {
        Call<List<ModelPost>> call = Methods.getApi().postView(apicall, id, BuildConfig.API_KEY);
        call.enqueue(new Callback<List<ModelPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<ModelPost>> call, @NotNull Response<List<ModelPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().get(0).isError()) {
                        // Toast.makeText(activity, "View Update Success", Toast.LENGTH_SHORT).show();
                    } else {
                        //  Toast.makeText(activity, "View Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ModelPost>> call, @NotNull Throwable t) {
                //Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addPoints(int user_id, int post_id, String activity, TextView rewardUnlockMsg) {
        Call<List<ModelPost>> call = Methods.getApi().addPoints("addPoints", BuildConfig.API_KEY, user_id, post_id, activity);
        call.enqueue(new Callback<List<ModelPost>>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<List<ModelPost>> call, @NotNull Response<List<ModelPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().get(0).isError()) {
                        showDialog((Activity) context, context.getString(R.string.congrats), context.getString(R.string.reward_earned_msg), "reward", "dismiss");
                        rewardUnlockMsg.setText(context.getString(R.string.congrats) + " " + context.getString(R.string.reward_earned_msg));
                        //Toast.makeText(context, response.body().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, response.body().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ModelPost>> call, @NotNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addRemoveFav(int post_id, int user_id) {
        Call<List<ModelPost>> call = Methods.getApi().addRemoveFav("addRemoveFav", BuildConfig.API_KEY, post_id, user_id);
        call.enqueue(new Callback<List<ModelPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<ModelPost>> call, @NotNull Response<List<ModelPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().get(0).isError()) {
                        //  Toast.makeText(context, response.body().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(context, response.body().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ModelPost>> call, @NotNull Throwable t) {
                // Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void shareNews(ImageView imageView, String title, int post_id) {
        Toast.makeText(context, context.getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "share", null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            bitmapUri.buildUpon();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            if (!Config.ITEM_PURCHASE_CODE.equals("demo")) {
                shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + "Download " + context.getResources().getString(R.string.app_name) + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            } else {
                shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + "Download " + context.getResources().getString(R.string.app_name) + "\n" + "https://codecanyon.net/item/andro-news-android-news-app-with-reward-system/42733609");
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share with"));
            postShare(post_id);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }


    public void postShare(int id) {
        Call<List<ModelPost>> call = Methods.getApi().postView("updateNewsShare", id, BuildConfig.API_KEY);
        call.enqueue(new Callback<List<ModelPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<ModelPost>> call, @NotNull Response<List<ModelPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().get(0).isError()) {
                        // Toast.makeText(activity, "View Update Success", Toast.LENGTH_SHORT).show();
                    } else {
                        //  Toast.makeText(activity, "View Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ModelPost>> call, @NotNull Throwable t) {
                //Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startThread(Activity activity) {
        Thread SplashThread = new Thread() {
            @Override
            public void run() {
                Call<VP> call = Methods.getApi().VP("verify", Config.ITEM_PURCHASE_CODE);
                call.enqueue(new Callback<VP>() {
                    @Override
                    public void onResponse(@NotNull Call<VP> stringCall, @NotNull Response<VP> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getP_count() == 1) {
                                Prefs.getInstance(activity).setAppCheck(true);
                                getAppSettings(activity);
                            } else {
                                Prefs.getInstance(activity).setAppCheck(false);
                                errorDialog(context.getString(R.string.whoops), "Purchase Verification Failed, Please Contact The App Owner : support@andromob.in");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<VP> stringCall, @NotNull Throwable t) {
                        Prefs.getInstance(activity).setAppCheck(false);
                        errorDialog(context.getString(R.string.whoops), t.getMessage());
                    }
                });
            }
        };
        SplashThread.start();
    }

    public void getAppSettings(Activity activity){
        Call<List<Settings>> call = Methods.getApi().getSettings("settings", BuildConfig.API_KEY);
        call.enqueue(new Callback<List<Settings>>() {
            @Override
            public void onResponse(@NonNull Call<List<Settings>> call, @NonNull Response<List<Settings>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body().get(0).isError()){
                        errorDialog(context.getString(R.string.whoops), response.body().get(0).getMessage());
                    } else {
                        Prefs.getInstance(activity).saveAppSettings(response.body());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AdsManager.getInstance(MyApplication.getInstance()).initializeAds(MyApplication.getInstance());
                            }
                        },500);
                        next(activity);
                    }
                } else {
                    errorDialog(context.getString(R.string.whoops), context.getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Settings>> call, @NonNull Throwable t) {
                errorDialog("Error!", "Invalid Packagename or Purchase Code");
            }
        });
    }

    private void next(Activity activity){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }
        },3500);
    }

    public void chk() {
        if (!Prefs.getInstance(context).isAppCheckDone()) {
            errorDialog("Purchase Verification Failed", "Please Contact The Owner");
        }
    }

    public void doReport(String type, int uid, int nid, int vid) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog((Activity) context);
        bottomSheetDialog.setContentView(R.layout.item_report);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final ImageView btn_close = bottomSheetDialog.findViewById(R.id.btn_close);
        EditText et_report = bottomSheetDialog.findViewById(R.id.et_report);
        MaterialButton btn_send = bottomSheetDialog.findViewById(R.id.btn_send);
        bottomSheetDialog.show();
        btn_close.setOnClickListener(view -> bottomSheetDialog.dismiss());
        btn_send.setOnClickListener(view -> {
            String getET_REPORT = et_report.getText().toString();
            if (getET_REPORT.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.cant_submit_empty_report), Toast.LENGTH_SHORT).show();
            } else {
                sendReport((Activity) context, type, uid, nid, vid, getET_REPORT, bottomSheetDialog);
            }
        });

    }

    public void sendReport(Activity activity, String type, int uid, int nid, int vid, String report, BottomSheetDialog bd) {
        Call<List<ModelPost>> call = Methods.getApi().doReport("report", BuildConfig.API_KEY, type, uid, nid, vid, report);
        call.enqueue(new Callback<List<ModelPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<ModelPost>> call, @NotNull Response<List<ModelPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().get(0).isError()) {
                        Toast.makeText(activity, activity.getString(R.string.report_sent_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.report_failed_msg), Toast.LENGTH_SHORT).show();
                    }
                    bd.dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ModelPost>> call, @NotNull Throwable t) {
                Toast.makeText(activity, activity.getString(R.string.report_failed_msg), Toast.LENGTH_SHORT).show();
                bd.dismiss();
            }
        });
    }

    public void doComment(String type, int uid, int nid, int vid, String comment) {
        commentListener.showLoading();
        Call<List<ModelPost>> call = Methods.getApi().doComment("addComment", BuildConfig.API_KEY, type, uid, nid, vid, comment);
        call.enqueue(new Callback<List<ModelPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<ModelPost>> call, @NotNull Response<List<ModelPost>> response) {
                commentListener.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().get(0).isError()) {
                        commentListener.onCommentSuccess();
                    } else {
                        commentListener.onCommentError();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ModelPost>> call, @NotNull Throwable t) {
                commentListener.hideLoading();
                commentListener.onCommentError();
            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.frag_slide_in_left,
                    R.anim.frag_slide_out_right, R.anim.frag_slide_in_right, R.anim.frag_slide_out_left);
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public void firstTimeRewardDialog() {
        if (Prefs.getInstance(context).getIsFirstTime()) {
            Prefs.getInstance(context).isFirstTime(false);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            BottomSheetBehavior<View> bottomSheetBehavior;
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.item_app_open_reward_dialog, null);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetBehavior = BottomSheetBehavior.from((View) (bottomSheetView.getParent()));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetView.setMinimumHeight((Resources.getSystem().getDisplayMetrics().heightPixels));
            ImageView btnClose = bottomSheetDialog.findViewById(R.id.btnClose);
            MaterialButton btnContinue = bottomSheetDialog.findViewById(R.id.btnContinue);
            bottomSheetDialog.show();
            btnClose.setOnClickListener(view -> bottomSheetDialog.dismiss());
            btnContinue.setOnClickListener(view -> bottomSheetDialog.dismiss());
        }
    }
}
