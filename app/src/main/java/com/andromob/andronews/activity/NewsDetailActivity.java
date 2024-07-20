package com.andromob.andronews.activity;

import static com.andromob.andronews.utils.Constant.isCommentMadeFinal;
import static com.andromob.andronews.utils.Constant.newCommentCount;
import static com.andromob.andronews.utils.Constant.profileBitmap;
import static com.andromob.andronews.utils.LoadingDialog.dismissdialog;
import static com.andromob.andronews.utils.LoadingDialog.startLoadingdialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.adapter.NewsAdapter;
import com.andromob.andronews.interfaces.CommentListener;
import com.andromob.andronews.interfaces.DialogClickListener;
import com.andromob.andronews.models.News;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsDetailActivity extends AppCompatActivity implements CommentListener {
    ImageView btnBack, btnFontResize, btnReport, btnShare, btnSave, thumb, btnPlay;
    TextView title, time, views, commentsCount, btnViewAll, lyt_related, tv_timer, rewardUnlockMsg;
    RelativeLayout lyt_video_button, rewardTimerLyt;
    LinearLayout rewardCountsLyt;
    private WebView mWebView;
    ShapeableImageView userImg;
    EditText commentSection;
    ImageButton sendComment;
    News newsList;
    User user;
    private Prefs sharedPref;
    static boolean isCommentMade = false;
    Methods methods;
    ProgressBar progressBar, progressBar_timer;
    RecyclerView recyclerView;
    NewsAdapter adapter;
    FrameLayout adContainerView, adContainerViewNative;
    NestedScrollView nestedScrollView;
    long startTime = 0;
    boolean isRewardAdded = false;
    private Handler mHandler;
    private Runnable mRunnable;
    Prefs prefs;
    CountDownTimer timer;
    long timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        prefs = Prefs.getInstance(this);
        mHandler = new Handler();
        startTime = System.currentTimeMillis();
        sharedPref = new Prefs(this);
        methods = new Methods(this, this);
        user = LoginPrefManager.getInstance(this).getUser();
        List<News> data = this.getIntent().getExtras().getParcelableArrayList("data");
        newsList = (News) data.get(0);
        initViews();
        setData();
        checkUserSpentTime();
    }

    @SuppressLint("SetJavaScriptEnabled")
    void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFontResize = findViewById(R.id.btnFontResize);
        btnReport = findViewById(R.id.btnReport);
        btnShare = findViewById(R.id.btnShare);
        btnSave = findViewById(R.id.btnSave);
        thumb = findViewById(R.id.thumb);
        title = findViewById(R.id.title);
        mWebView = (WebView) findViewById(R.id.mWebView);
        mWebView.setBackgroundColor(0);
        mWebView.getSettings().setJavaScriptEnabled(true);
        time = findViewById(R.id.time);
        views = findViewById(R.id.views);
        commentsCount = findViewById(R.id.commentsCount);
        userImg = findViewById(R.id.userImg);
        commentSection = findViewById(R.id.commentSection);
        sendComment = findViewById(R.id.sendComment);
        recyclerView = findViewById(R.id.recyclerView);
        btnViewAll = findViewById(R.id.btnViewAll);
        lyt_video_button = findViewById(R.id.lyt_video_button);
        btnPlay = findViewById(R.id.btnPlay);
        lyt_related = findViewById(R.id.lyt_related);
        progressBar = findViewById(R.id.progressBar);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        adContainerView = findViewById(R.id.adContainerView);
        adContainerViewNative = findViewById(R.id.adContainerViewNative);
        rewardCountsLyt = findViewById(R.id.rewardCountsLyt);
        progressBar_timer = findViewById(R.id.progressBar_timer);
        tv_timer = findViewById(R.id.tv_timer);
        rewardUnlockMsg = findViewById(R.id.rewardUnlockMsg);
        rewardTimerLyt = findViewById(R.id.rewardTimerLyt);
        checkFav(btnSave);
    }

    private void setData() {
        methods.loadNewsImage(thumb, newsList.getThumbnail());
        title.setText(newsList.getNews_title());
        time.setText(newsList.getPosted_at());
        views.setText(newsList.getViews());
        if (newsList.getType().equals("video")) {
            lyt_video_button.setVisibility(View.VISIBLE);
            lyt_video_button.setOnClickListener(v -> {
                if (new Methods(this).isYoutubeUrl(newsList.getVideo_url())) {
                    Intent intent = new Intent(NewsDetailActivity.this, YoutubePlayerActivity.class);
                    intent.putExtra("url", newsList.getVideo_url());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(NewsDetailActivity.this, VideoPlayerActivity.class);
                    intent.putExtra("url", newsList.getVideo_url());
                    startActivity(intent);
                }
            });
        }
        commentsCount.setText(String.valueOf(newsList.getComments()));
        if (LoginPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
            commentSection.setHint("Comment as " + user.getName());
        }
        if (profileBitmap != null && !profileBitmap.isRecycled()) {
            userImg.setImageBitmap(profileBitmap);
        } else {
            methods.loadShapeableImageView(userImg, user.getImg_url());
        }
        displayNews();
        methods.postView("updateNewsViews", newsList.getId());
        btnFontResize.setOnClickListener(v -> showFontResizeDialog());
        btnBack.setOnClickListener(v -> onBackPressed());
        btnReport.setOnClickListener(v ->{
            if (LoginPrefManager.getInstance(this).isLoggedIn()){
                methods.doReport("news", user.getUser_id(), newsList.getId(), 0);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
        btnShare.setOnClickListener(v -> {
            methods.shareNews(thumb, newsList.getNews_title(), newsList.getId());
        });
        sendComment.setOnClickListener(v -> commentValidation());
        btnSave.setOnClickListener(v -> {
            if (LoginPrefManager.getInstance(this).isLoggedIn()) {
                addRemoveFav(newsList.getId(), btnSave);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
                    Intent intent = new Intent(NewsDetailActivity.this, CommentsActivity.class);
                    intent.putExtra("type", "news");
                    intent.putExtra("post_id", newsList.getId());
                    startActivity(intent);
                } else {
                    startActivity(new Intent(NewsDetailActivity.this, LoginActivity.class));
                }
            }
        });
        fetchRelated(newsList.getId(), newsList.getCid());

        if (LoginPrefManager.getInstance(this).isLoggedIn()) {
            if (prefs.getAppSetting().get(0).isNews_reward_status()) {
                rewardCountsLyt.setVisibility(View.VISIBLE);
                final int maxTime = prefs.getAppSetting().get(0).getNews_seconds() * 1000;
                timer = new CountDownTimer(maxTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        timeLeft = millisUntilFinished;
                        long finishedSeconds = maxTime - millisUntilFinished;
                        int total = (int) (((float) finishedSeconds / (float) maxTime) * 100.0);
                        progressBar_timer.setProgress(total);
                        int getSeconds = (int) (millisUntilFinished / 1000);
                        tv_timer.setText(String.valueOf(getSeconds));
                    }

                    @SuppressLint("SetTextI18n")
                    public void onFinish() {
                        progressBar.setProgress(100);
                        rewardTimerLyt.setVisibility(View.GONE);
                        rewardUnlockMsg.setText(getString(R.string.adding_reward_msg));
                    }
                }.start();
            }
        } else {
            rewardCountsLyt.setVisibility(View.GONE);
        }
    }

    void commentValidation() {
        String comment = commentSection.getText().toString().trim();

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, getString(R.string.comment_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if (LoginPrefManager.getInstance(NewsDetailActivity.this).isLoggedIn()) {
            methods.doComment("news", user.getUser_id(), newsList.getId(), 0, comment);
        } else {
            finish();
            startActivity(new Intent(NewsDetailActivity.this, LoginActivity.class));
        }
    }

    private void checkFav(ImageView imageView) {
        String favIDList = sharedPref.getFavorite();
        if (favIDList != null && !favIDList.isEmpty()) {
            if (favIDList.contains("," + newsList.getId() + ",")) {
                imageView.setImageResource(R.drawable.ic_favorite_filled);
            } else {
                imageView.setImageResource(R.drawable.ic_favorite);
            }
        } else {
            imageView.setImageResource(R.drawable.ic_favorite);
        }
    }

    private void addRemoveFav(int favID, ImageView imageView) {
        String message;
        String favIDList = sharedPref.getFavorite();

        if (!favIDList.isEmpty()) {
            if (favIDList.contains("," + favID + ",")) {
                favIDList = favIDList.replace("," + favID + ",", ",");
                sharedPref.addFavorite(favIDList);
                imageView.setImageResource(R.drawable.ic_favorite);
                message = getString(R.string.removed_from_fav);
            } else {
                favIDList = favIDList + favID + ",";
                sharedPref.addFavorite(favIDList);
                imageView.setImageResource(R.drawable.ic_favorite_filled);
                message = getString(R.string.added_to_fav);
            }
        } else {
            favIDList = "," + favID + ",";
            sharedPref.addFavorite(favIDList);
            imageView.setImageResource(R.drawable.ic_favorite_filled);
            message = getString(R.string.added_to_fav);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        methods.addRemoveFav(newsList.getId(), user.getUser_id());
    }

    public void displayNews() {
        String str;
        String str2;
        String newsData = newsList.getNews();
        int textSize = sharedPref.getTextSize();
        String str3 = "body{font-size:14px;}";
        if (textSize == 0) {
            str3 = "body{font-size:12px;}";
        } else if (1 != textSize) {
            if (2 == textSize) {
                str3 = "body{font-size:16px;}";
            } else if (3 == textSize) {
                str3 = "body{font-size:17px;}";
            } else if (4 == textSize) {
                str3 = "body{font-size:20px;}";
            }
        }

        str = "<style channelType=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/opensans.ttf\")}body,* {color:#161616; font-family: MyFont; text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style><style type=\"text/css\">" + str3 + "</style>";

        str2 = str + "<div>" + newsData + "</div>";
        mWebView.loadDataWithBaseURL("", str2, "text/html", "utf-8", (String) null);
    }

    private void showFontResizeDialog() {
        final String[] sizes = {"Extra Small", "Small", "Medium", "Large", "Extra Large"};
        final int checkedItem;
        int fontSize = sharedPref.getTextSize();
        if (fontSize == 0) {
            checkedItem = 0;
        } else if (fontSize == 1) {
            checkedItem = 1;
        } else if (fontSize == 2) {
            checkedItem = 2;
        } else if (fontSize == 3) {
            checkedItem = 3;
        } else if (fontSize == 4) {
            checkedItem = 4;
        } else {
            checkedItem = 3;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(NewsDetailActivity.this, R.style.ThemeDialog);
        builder.setTitle("Select Font Size")
                .setSingleChoiceItems(sizes, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(LanguageActivity.this,""+which,Toast.LENGTH_SHORT).show();
                        if (sizes[which].equals("Extra Small")) {
                            sharedPref.setTextSize(0);
                        }
                        if (sizes[which].equals("Small")) {
                            sharedPref.setTextSize(1);
                        }
                        if (sizes[which].equals("Medium")) {
                            sharedPref.setTextSize(2);
                        }
                        if (sizes[which].equals("Large")) {
                            sharedPref.setTextSize(3);
                        }
                        if (sizes[which].equals("Extra Large")) {
                            sharedPref.setTextSize(4);
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        displayNews();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void showLoading() {
        startLoadingdialog(this);
    }

    @Override
    public void hideLoading() {
        dismissdialog();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCommentSuccess() {
        isCommentMade = true;
        isCommentMadeFinal = true;
        newCommentCount++;
        newCommentCount = newCommentCount + newsList.getComments();
        if (newCommentCount == 0) {
            commentsCount.setText(Integer.toString(newsList.getComments()));
        } else {
            commentsCount.setText(Integer.toString(newCommentCount));
            newCommentCount = 0;
        }
        commentSection.getText().clear();
        isCommentMade = false;
        Toast.makeText(this, getString(R.string.comment_submitted), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCommentError() {
        commentSection.getText().clear();
        Toast.makeText(this, getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
    }

    private void fetchRelated(int post_id, int cid) {
        lyt_related.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Call<List<News>> dataCall = Methods.getApi().getRelatedNews("news", BuildConfig.API_KEY, "related", post_id, cid);
        dataCall.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call, @NonNull Response<List<News>> response) {
                progressBar.setVisibility(View.GONE);
                lyt_related.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isEmpty()) {
                        LinearLayoutManager llm = new LinearLayoutManager(NewsDetailActivity.this);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        llm.setReverseLayout(false);
                        recyclerView.setLayoutManager(llm);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        adapter = new NewsAdapter(NewsDetailActivity.this, true, user);
                        adapter.setData(response.body());
                        recyclerView.setAdapter(adapter);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        lyt_related.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    lyt_related.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<News>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                lyt_related.setVisibility(View.GONE);
            }
        });
    }

    void checkUserSpentTime() {
        if (LoginPrefManager.getInstance(NewsDetailActivity.this).isLoggedIn()) {
            if (prefs.getAppSetting().get(0).isNews_reward_status()) {
                rewardCountsLyt.setVisibility(View.VISIBLE);
                if (!isRewardAdded) {
                    final int delay = 1000; // 1000 milliseconds == 1 second
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            long End = System.currentTimeMillis();
                            long timeElapsed = End - startTime;
                            int getTimeInSeconds = (int) (timeElapsed / 1000);
                            int eligbleTime = prefs.getAppSetting().get(0).getNews_seconds();
                            if (getTimeInSeconds >= eligbleTime) {
                                isRewardAdded = true;
                                mHandler.removeCallbacks(mRunnable);
                                methods.addPoints(user.getUser_id(), newsList.getId(), getString(R.string.news_read), rewardUnlockMsg);
                            } else {
                                mHandler.postDelayed(mRunnable, delay);
                            }
                        }
                    };
                    mHandler.postDelayed(mRunnable, delay);
                }
            }
        } else {
            new Methods(this, new DialogClickListener() {
                @Override
                public void onClick(String action) {
                    startActivity(new Intent(NewsDetailActivity.this, LoginActivity.class));
                }
            }).showDialog(this, getString(R.string.did_you_know), getString(R.string.you_can_earn_msg), "login", "login");
        }
    }

    private void updateAds() {
        AdsManager.getInstance(this).showBannerAd(this, adContainerView);
        AdsManager.getInstance(this).showNativeAd(this, adContainerViewNative);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdsManager.getInstance(this).destroyNativeAds();
        AdsManager.getInstance(this).destroyBannerAds();
        if (LoginPrefManager.getInstance(NewsDetailActivity.this).isLoggedIn()) {
            mHandler.removeCallbacks(mRunnable);
            if (timer != null) {
                timer.cancel();
            }
            rewardTimerLyt.setVisibility(View.GONE);
            rewardCountsLyt.setVisibility(View.GONE);
            rewardUnlockMsg.setText(getResources().getString(R.string.cant_get_reward_msg));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        if (isCommentMade) {
            isCommentMade = false;
            commentsCount.setText(Integer.toString(newCommentCount));
            newCommentCount = 0;
        }
        if (LoginPrefManager.getInstance(NewsDetailActivity.this).isLoggedIn()) {
            if (isRewardAdded) {
                rewardUnlockMsg.setText(getString(R.string.congrats) + " " + getString(R.string.reward_earned_msg));
            }
            if (prefs.getAppSetting().get(0).isNews_reward_status()) {
                rewardCountsLyt.setVisibility(View.VISIBLE);
            }
        }
        updateAds();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}