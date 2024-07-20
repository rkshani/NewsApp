package com.andromob.andronews.activity;

import static com.andromob.andronews.utils.Constant.isCommentMadeFinal;
import static com.andromob.andronews.utils.Constant.newCommentCount;
import static com.andromob.andronews.utils.Constant.profileBitmap;
import static com.andromob.andronews.utils.LoadingDialog.dismissdialog;
import static com.andromob.andronews.utils.LoadingDialog.startLoadingdialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.fragments.YoutubePlayerFragment;
import com.andromob.andronews.adapter.VideosAdapter;
import com.andromob.andronews.fragments.ExoPlayerFragment;
import com.andromob.andronews.interfaces.CommentListener;
import com.andromob.andronews.models.User;
import com.andromob.andronews.models.Videos;
import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.Events;
import com.andromob.andronews.utils.GlobalBus;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoDetailActivity extends AppCompatActivity implements CommentListener {
    User user;
    Videos videosList;
    private int playerHeight;
    private FragmentManager fragmentManager;
    public static boolean isFullScreen = false;
    RelativeLayout embededPlayer_layout, lyt_video_button;
    ImageView thumbnail, btnDescription;
    FrameLayout videoPlayer, adContainerNative;
    NestedScrollView nestedScrollView;
    TextView title, time, views, commentsCount, btnViewAll, lyt_related;
    ShapeableImageView userImg;
    EditText commentSection;
    ImageButton sendComment;
    static boolean isCommentMade = false;
    Methods methods;
    ProgressBar progressBarVideo, progressBarRelated;
    RecyclerView recyclerView;
    VideosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalBus.getBus().register(this);
        setContentView(R.layout.activity_video_detail);
        methods = new Methods(this, this);
        List<Videos> data = this.getIntent().getExtras().getParcelableArrayList("data");
        videosList = (Videos) data.get(0);
        user = LoginPrefManager.getInstance(this).getUser();
        initViews();
        setData();
    }

    private void initViews() {
        embededPlayer_layout = findViewById(R.id.embededPlayer_layout);
        lyt_video_button = findViewById(R.id.lyt_video_button);
        thumbnail = findViewById(R.id.thumbnail);
        btnDescription = findViewById(R.id.btnDescription);
        videoPlayer = findViewById(R.id.videoPlayer);
        adContainerNative = findViewById(R.id.adContainerNative);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        title = findViewById(R.id.title);
        time = findViewById(R.id.time);
        views = findViewById(R.id.views);
        userImg = findViewById(R.id.userImg);
        commentsCount = findViewById(R.id.commentsCount);
        btnViewAll = findViewById(R.id.btnViewAll);
        commentSection = findViewById(R.id.commentSection);
        sendComment = findViewById(R.id.sendComment);
        progressBarVideo = findViewById(R.id.progressBarVideo);
        progressBarRelated = findViewById(R.id.progressBarRelated);
        recyclerView = findViewById(R.id.recyclerView);
        embededPlayer_layout = findViewById(R.id.embededPlayer_layout);
        lyt_related = findViewById(R.id.lyt_related);
        int columnWidth = methods.getScreenWidth();
        videoPlayer.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, (int) (columnWidth / 1.8)));
        playerHeight = videoPlayer.getLayoutParams().height;
        fragmentManager = getSupportFragmentManager();
    }

    private void setData() {
        if (videosList.getVideo_type().equals("live_url")) {
            progressBarVideo.setVisibility(View.GONE);
            ExoPlayerFragment exoPlayerFragment = ExoPlayerFragment.newInstance(videosList.getVideo_url());
            fragmentManager.beginTransaction().replace(R.id.videoPlayer, exoPlayerFragment).commitAllowingStateLoss();
        } else if (videosList.getVideo_type().equals("youtube")) {
            progressBarVideo.setVisibility(View.GONE);
            YoutubePlayerFragment youtubePlayerFragment = YoutubePlayerFragment.newInstance(videosList.getVideo_url());
            fragmentManager.beginTransaction().replace(R.id.videoPlayer, youtubePlayerFragment).commitAllowingStateLoss();
        } else {
            Glide.with(this).asBitmap().load(videosList.getThumbnail()).centerCrop().into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    progressBarVideo.setVisibility(View.GONE);
                    thumbnail.setImageBitmap(resource);
                    thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    embededPlayer_layout.setVisibility(View.VISIBLE);
                    lyt_video_button.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }

                @SuppressLint("ResourceAsColor")
                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    progressBarVideo.setVisibility(View.GONE);
                    embededPlayer_layout.setVisibility(View.VISIBLE);
                    lyt_video_button.setVisibility(View.VISIBLE);
                }
            });
        }
        title.setText(videosList.getVideo_title());
        time.setText(videosList.getPosted_at());
        views.setText(videosList.getViews());
        commentsCount.setText(String.valueOf(videosList.getComments()));
        if (LoginPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
            commentSection.setHint("Comment as " + user.getName());
        }
        if (profileBitmap != null && !profileBitmap.isRecycled()) {
            userImg.setImageBitmap(profileBitmap);
        } else {
            methods.loadShapeableImageView(userImg, user.getImg_url());
        }
        sendComment.setOnClickListener(v -> commentValidation());
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
                    Intent intent = new Intent(VideoDetailActivity.this, CommentsActivity.class);
                    intent.putExtra("type", "video");
                    intent.putExtra("post_id", videosList.getId());
                    startActivity(intent);
                } else {
                    startActivity(new Intent(VideoDetailActivity.this, LoginActivity.class));
                }
            }
        });
        btnDescription.setOnClickListener(v -> showDescription());
        methods.postView("updateVideoViews", videosList.getId());
        fetchRelated(videosList.getId(), videosList.getCid());
        lyt_video_button.setOnClickListener(v->{
                Intent intent = new Intent(VideoDetailActivity.this, WebViewActivity.class);
                intent.putExtra("url", videosList.getVideo_url());
                startActivity(intent);
        });
    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScreen = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            gotoFullScreen();
        } else {
            gotoPortraitScreen();
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void gotoPortraitScreen() {
        nestedScrollView.setVisibility(View.VISIBLE);
        videoPlayer.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, playerHeight));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        adContainerNative.setVisibility(View.VISIBLE);
    }

    private void gotoFullScreen() {
        nestedScrollView.setVisibility(View.GONE);
        adContainerNative.setVisibility(View.GONE);
        videoPlayer.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    void showDescription() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.sheetDialog);
        bottomSheetDialog.setContentView(R.layout.item_detail_bottom_sheet);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ImageView btn_close = bottomSheetDialog.findViewById(R.id.btn_close);
        TextView title = bottomSheetDialog.findViewById(R.id.title);
        TextView time = bottomSheetDialog.findViewById(R.id.time);
        TextView views = bottomSheetDialog.findViewById(R.id.views);
        TextView description = bottomSheetDialog.findViewById(R.id.description);
        title.setText(videosList.getVideo_title());
        time.setText(videosList.getPosted_at());
        views.setText(videosList.getViews());
        description.setText(videosList.getDescription());
        bottomSheetDialog.show();
        btn_close.setOnClickListener(view -> bottomSheetDialog.dismiss());
    }

    void commentValidation() {
        String comment = commentSection.getText().toString().trim();

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, getString(R.string.comment_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if (LoginPrefManager.getInstance(VideoDetailActivity.this).isLoggedIn()) {
            methods.doComment("video", user.getUser_id(), 0, videosList.getId(), comment);
        } else {
            finish();
            startActivity(new Intent(VideoDetailActivity.this, LoginActivity.class));
        }
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
        newCommentCount = newCommentCount + videosList.getComments();
        if (newCommentCount == 0) {
            commentsCount.setText(Integer.toString(videosList.getComments()));
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
        progressBarRelated.setVisibility(View.VISIBLE);
        Call<List<Videos>> dataCall = Methods.getApi().getRelatedVideos("videos", BuildConfig.API_KEY, "related", post_id, cid);
        dataCall.enqueue(new Callback<List<Videos>>() {
            @Override
            public void onResponse(@NonNull Call<List<Videos>> call, @NonNull Response<List<Videos>> response) {
                progressBarRelated.setVisibility(View.GONE);
                lyt_related.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isEmpty()) {
                        LinearLayoutManager llm = new LinearLayoutManager(VideoDetailActivity.this);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        llm.setReverseLayout(false);
                        recyclerView.setLayoutManager(llm);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        adapter = new VideosAdapter(VideoDetailActivity.this);
                        adapter.setData(response.body());
                        recyclerView.setAdapter(adapter);
                    } else {
                        progressBarRelated.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        lyt_related.setVisibility(View.GONE);
                    }
                } else {
                    progressBarRelated.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    lyt_related.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Videos>> call, @NonNull Throwable t) {
                progressBarRelated.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                lyt_related.setVisibility(View.GONE);
            }
        });
    }
    private void updateAds(){
        AdsManager.getInstance(this).showNativeAdVideo(VideoDetailActivity.this, adContainerNative);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdsManager.getInstance(this).destroyNativeAds();
    }

    @Override
    public void onBackPressed() {
            if (isFullScreen) {
                Events.FullScreen fullScreen = new Events.FullScreen();
                fullScreen.setFullScreen(false);
                GlobalBus.getBus().post(fullScreen);
            } else {
                super.onBackPressed();
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
        updateAds();
    }

}