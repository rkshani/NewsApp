package com.andromob.andronews.activity;

import static com.andromob.andronews.utils.Config.TERMS_CONDITIONS_URL;
import static com.andromob.andronews.utils.LoadingDialog.dismissdialog;
import static com.andromob.andronews.utils.LoadingDialog.startLoadingdialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class RegisterActivity extends AppCompatActivity {
    TextView tv_login, btnTerms;
    EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextPhone;
    Button btn_register;
    Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        new Methods(this).changeStatusBarColor(this);
        methods = new Methods(this, new DialogClickListener() {
            @Override
            public void onClick(String action) {
                switch (action) {
                    case "registerSuccess":
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        break;
                    case "failed":
                        //
                        break;
                }
            }
        });
        tv_login = findViewById(R.id.tv_login);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        btn_register = findViewById(R.id.btn_register);
        btnTerms = findViewById(R.id.btnTerms);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        btnTerms.setOnClickListener(v->{
            Intent intent = new Intent(RegisterActivity.this, WebViewActivity.class);
            intent.putExtra("url", TERMS_CONDITIONS_URL);
            startActivity(intent);
        });

    }

    private void onLoginClick() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError(getString(R.string.field_empty_msg));
            editTextName.requestFocus();
            return;
        }

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

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(getString(R.string.field_empty_msg));
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError(getString(R.string.field_empty_msg));
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (!confirmPassword.equals(password)) {
            editTextConfirmPassword.setError(getString(R.string.password_not_match_msg));
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError(getString(R.string.field_empty_msg));
            editTextPhone.requestFocus();
            return;
        }

        doRegister(name, email, confirmPassword, phone);
    }

    private void doRegister(String name, String email, String password, String phone) {
        startLoadingdialog(RegisterActivity.this);
        Call<GetResponse> loginResponseCall = Methods.getApi().doRegister("register", BuildConfig.API_KEY, "normal", name, email, password, phone, "0");
        loginResponseCall.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetResponse> call, @NonNull Response<GetResponse> response) {
                dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isError()) {
                        editTextEmail.getText().clear();
                        editTextPhone.getText().clear();
                        methods.showDialog(RegisterActivity.this, getString(R.string.success), response.body().getMessage(), "success", "registerSuccess");
                    } else {
                        methods.showDialog(RegisterActivity.this, getString(R.string.failed), response.body().getMessage(), "alert", "failed");
                    }
                } else {
                    dismissdialog();
                    methods.showDialog(RegisterActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetResponse> call, @NonNull Throwable t) {
                dismissdialog();
                Toast.makeText(RegisterActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }


}