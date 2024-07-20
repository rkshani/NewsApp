package com.andromob.andronews.activity;

import static com.andromob.andronews.utils.LoadingDialog.dismissdialog;
import static com.andromob.andronews.utils.LoadingDialog.startLoadingdialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.interfaces.DialogClickListener;
import com.andromob.andronews.models.GetResponse;
import com.andromob.andronews.utils.Methods;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordResetActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPhone;
    Button btn_reset;
    Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Methods(this).changeStatusBarColor(this);
        setContentView(R.layout.activity_password_reset);
        methods = new Methods(this, new DialogClickListener() {
            @Override
            public void onClick(String action) {
                switch (action){
                    case "resetSuccess":
                        startActivity(new Intent(PasswordResetActivity.this, LoginActivity.class));
                        break;
                    case "failed":
                        //
                        break;
                }
            }
        });
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        btn_reset = findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword() {
        final String email = editTextEmail.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.field_empty_msg));
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.enter_valid_email_msg));
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError(getString(R.string.field_empty_msg));
            editTextPhone.requestFocus();
            return;
        }
        doReset(email, phone);
    }

    private void doReset(String email, String phone) {
        startLoadingdialog(PasswordResetActivity.this);
        Call<GetResponse> loginResponseCall = Methods.getApi().resetPassword("reset", BuildConfig.API_KEY, email, phone);
        loginResponseCall.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetResponse> call, @NonNull Response<GetResponse> response) {
                dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isError()) {
                        editTextEmail.getText().clear();
                        editTextPhone.getText().clear();
                        methods.showDialog(PasswordResetActivity.this, getString(R.string.success), response.body().getMessage(), "success", "resetSuccess");
                    } else {
                        methods.showDialog(PasswordResetActivity.this, getString(R.string.failed), response.body().getMessage(), "alert","failed");
                    }
                } else {
                    dismissdialog();
                    methods.showDialog(PasswordResetActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                }

            }

            @Override
            public void onFailure(@NonNull Call<GetResponse> call, @NonNull Throwable t) {
                dismissdialog();
                Toast.makeText(PasswordResetActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}