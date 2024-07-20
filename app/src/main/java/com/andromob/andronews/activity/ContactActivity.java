package com.andromob.andronews.activity;

import static com.andromob.andronews.utils.LoadingDialog.dismissdialog;
import static com.andromob.andronews.utils.LoadingDialog.startLoadingdialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.interfaces.DialogClickListener;
import com.andromob.andronews.models.ModelPost;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity {

    TextInputEditText inputName, inputEmail, inputSubject, inputMessage;
    MaterialButton btnSubmit;
    User user;
    ProgressBar progressBar;
    FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.help_center));
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputSubject = findViewById(R.id.inputSubject);
        inputMessage = findViewById(R.id.inputMessage);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);
        adContainerView = findViewById(R.id.adContainerView);

        btnSubmit.setOnClickListener(v -> dataValidation());
        initData();
    }

    void initData(){
        if (LoginPrefManager.getInstance(this).isLoggedIn()){
            user = LoginPrefManager.getInstance(this).getUser();
            inputName.setText(user.getName());
            inputEmail.setText(user.getEmail());
            inputName.setEnabled(false);
            inputEmail.setEnabled(false);
        }
    }

    private void dataValidation() {
        final String name = inputName.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        final String subject = inputSubject.getText().toString().trim();
        final String message = inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            inputName.setError(getString(R.string.field_empty_msg));
            inputName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.field_empty_msg));
            inputEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputSubject.setError(getString(R.string.enter_valid_email_msg));
            inputSubject.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(subject)) {
            inputSubject.setError(getString(R.string.field_empty_msg));
            inputSubject.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(message)) {
            inputMessage.setError(getString(R.string.field_empty_msg));
            inputMessage.requestFocus();
            return;
        }

        submitContactForm(name, email, subject, message);

    }

    public void submitContactForm(String name, String email, String subject, String message) {
        startLoadingdialog(this);
        Call<List<ModelPost>> call = Methods.getApi().submitContactForm("contactform", BuildConfig.API_KEY, name, email, subject, message);
        call.enqueue(new Callback<List<ModelPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<ModelPost>> call, @NotNull Response<List<ModelPost>> response) {
                dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().get(0).isError()) {
                        new Methods(ContactActivity.this, new DialogClickListener() {
                            @Override
                            public void onClick(String action) {
                                if (action.equals("close")){
                                    finish();
                                }
                            }
                        }).showDialog(ContactActivity.this, getString(R.string.success), response.body().get(0).getMessage(), "success", "close");
                    } else {
                        new Methods(ContactActivity.this).showDialog(ContactActivity.this, getString(R.string.failed), response.body().get(0).getMessage(), "alert", "failed");
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ModelPost>> call, @NotNull Throwable t) {
                dismissdialog();
                new Methods(ContactActivity.this).showDialog(ContactActivity.this, getString(R.string.failed), t.getMessage(), "alert", "failed");
            }
        });
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