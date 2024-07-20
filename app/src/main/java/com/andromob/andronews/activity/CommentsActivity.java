package com.andromob.andronews.activity;

import static com.andromob.andronews.activity.NewsDetailActivity.isCommentMade;
import static com.andromob.andronews.utils.Constant.profileBitmap;
import static com.andromob.andronews.utils.LoadingDialog.dismissdialog;
import static com.andromob.andronews.utils.LoadingDialog.startLoadingdialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.adapter.CommentsAdapter;
import com.andromob.andronews.interfaces.CommentListener;
import com.andromob.andronews.models.GetComment;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.Constant;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity implements CommentListener {
    RecyclerView recyclerView;
    CommentsAdapter adapter;
    ProgressBar progressBar;
    TextView no_cmts_tv;
    ImageButton sendComment;
    EditText commentSection;
    ShapeableImageView userImg;
    User user;
    String type;
    int post_id;
    Methods methods;
    Toolbar toolbar;
    FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.all_comments));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        user = LoginPrefManager.getInstance(this).getUser();
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        post_id = intent.getIntExtra("post_id", 0);
        methods = new Methods(this, this);

        adContainerView = findViewById(R.id.adContainerView);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        commentSection = findViewById(R.id.commentSection);
        no_cmts_tv = findViewById(R.id.no_cmts_tv);
        userImg = findViewById(R.id.userImg);
        sendComment = findViewById(R.id.sendComment);
        if (LoginPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
            commentSection.setHint("Comment as " + user.getName());
            if (profileBitmap != null && !profileBitmap.isRecycled()) {
                userImg.setImageBitmap(profileBitmap);
            } else {
                methods.loadShapeableImageView(userImg, user.getImg_url());
            }
        }
        sendComment.setOnClickListener(v -> commentValidation());
        fetchComments(type, post_id);
    }

    private void fetchComments(String type, int post_id) {
        progressBar.setVisibility(View.VISIBLE);
        no_cmts_tv.setVisibility(View.GONE);
        Call<List<GetComment>> dataCall = Methods.getApi().getComments("comments", BuildConfig.API_KEY, type, post_id);
        dataCall.enqueue(new Callback<List<GetComment>>() {
            @Override
            public void onResponse(@NonNull Call<List<GetComment>> call, @NonNull Response<List<GetComment>> response) {
                dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        no_cmts_tv.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()
                                , LinearLayoutManager.VERTICAL, false));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        adapter = new CommentsAdapter(CommentsActivity.this, response.body());
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        no_cmts_tv.setVisibility(View.GONE);
                        Constant.newCommentCount = response.body().size();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    no_cmts_tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GetComment>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                no_cmts_tv.setVisibility(View.VISIBLE);
                no_cmts_tv.setText(getString(R.string.something_went_wrong));
            }
        });
    }

    void commentValidation() {
        String comment = commentSection.getText().toString().trim();

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, getString(R.string.comment_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if (LoginPrefManager.getInstance(CommentsActivity.this).isLoggedIn()) {
            if (type.equals("news")) {
                methods.doComment(type, user.getUser_id(), post_id, 0, comment);
            } else if (type.equals("video")) {
                methods.doComment(type, user.getUser_id(), 0, post_id, comment);
            }
        } else {
            finish();
            startActivity(new Intent(CommentsActivity.this, LoginActivity.class));
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

    @Override
    public void onCommentSuccess() {
        commentSection.getText().clear();
        fetchComments(type, post_id);
        Toast.makeText(this, getString(R.string.comment_submitted), Toast.LENGTH_SHORT).show();
        isCommentMade = true;
        Constant.isCommentMadeFinal = true;
    }

    @Override
    public void onCommentError() {
        commentSection.getText().clear();
        Toast.makeText(this, getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateAds(){
        AdsManager.getInstance(this).showBannerAd(this, adContainerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAds();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdsManager.getInstance(this).destroyBannerAds();
    }
}