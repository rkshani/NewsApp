package com.andromob.andronews.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.andromob.andronews.models.GetResponseDetailed;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;
import com.andromob.andronews.utils.ProgressLoadingDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextView tv_register, tv_forget;
    EditText editTextEmail, editTextPassword;
    Button btn_login, btn_skip;
    Methods methods;
    CallbackManager callbackManager;
    LoginButton login_button_fb;
    MaterialButton btnFbLogin, btnGoogleLogin;
    private FirebaseAuth mAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Methods(this).changeStatusBarColor(this);

        mAuth = FirebaseAuth.getInstance();
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        methods = new Methods(this, new DialogClickListener() {
            @Override
            public void onClick(String action) {
                switch (action) {
                    case "loginSuccess":
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        break;
                    case "failed":
                        //
                        break;
                }
            }
        });
        setContentView(R.layout.activity_login);
        pd = new ProgressDialog(LoginActivity.this);
        tv_register = findViewById(R.id.tv_register);
        tv_forget = findViewById(R.id.tv_forget);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btn_login = findViewById(R.id.btn_login);
        login_button_fb = findViewById(R.id.login_button_fb);
        login_button_fb.setReadPermissions(Arrays.asList("public_profile", "user_photos", "email"));
        btnFbLogin = findViewById(R.id.btnFbLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btn_skip = findViewById(R.id.btn_skip);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterClick();
            }
        });
        tv_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgetClick();
            }
        });
        btn_login.setOnClickListener(view -> loginUser());
        btn_skip.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
        btnFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Methods(LoginActivity.this).isNetworkAvailable()) {
                    login_button_fb.performClick();
                } else {
                    methods.showDialog(LoginActivity.this, getString(R.string.failed), getString(R.string.check_internet), "failed", "");
                }
            }
        });
        callbackManager = CallbackManager.Factory.create();

        login_button_fb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                getUserProfile(accessToken);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("FB-LOGIN", exception.getMessage());
            }
        });

        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Methods(LoginActivity.this).isNetworkAvailable()) {

                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();

                    GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 221);
                } else {
                    methods.showDialog(LoginActivity.this, getString(R.string.failed), getString(R.string.check_internet), "failed", "");
                }
            }
        });

    }

    private void onRegisterClick() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void onForgetClick() {
        startActivity(new Intent(this, PasswordResetActivity.class));
    }

    private void loginUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

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
        doNormalLogin(email, password);
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            Log.e("GraphResponse", "-------------" + response.toString());
                            String id = "", first_name = "", email = "", last_name = "", image_url = "";

                            if (object.has("first_name")) {
                                first_name = object.getString("first_name");
                            }
                            if (object.has("last_name")) {
                                last_name = object.getString("last_name");
                            }
                            if (object.has("email")) {
                                email = object.getString("email");
                            }
                            id = object.getString("id");
                            //profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            image_url = "http://graph.facebook.com/" + id + "/picture?type=large";
                            doSocialLogin("facebook", first_name + " " + last_name, email, image_url, id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,link, last_name,email,id, picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void doSocialLogin(String type, String name, String email, String img_url, String auth_id) {
        new ProgressLoadingDialog(LoginActivity.this).startLoadingdialog();
        Call<GetResponseDetailed> loginResponseCall = Methods.getApi().doSocialLogin("doSocialLogin",
                BuildConfig.API_KEY, type, name, email, img_url, auth_id);
        loginResponseCall.enqueue(new Callback<GetResponseDetailed>() {
            @Override
            public void onResponse(@NonNull Call<GetResponseDetailed> call, @NonNull Response<GetResponseDetailed> response) {
                new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isError()) {
                        User user = new User(
                                response.body().getUserList().get(0).getUser_id(),
                                response.body().getUserList().get(0).getUser_type(),
                                response.body().getUserList().get(0).getName(),
                                response.body().getUserList().get(0).getEmail(),
                                response.body().getUserList().get(0).getImg_url(),
                                response.body().getUserList().get(0).getPhone(),
                                response.body().getUserList().get(0).getAuth_id(),
                                response.body().getUserList().get(0).getRegistered_on(),
                                response.body().getUserList().get(0).getFav_ids());
                        LoginPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        methods.showDialog(LoginActivity.this, getString(R.string.success), response.body().getMessage(), "success", "loginSuccess");
                    } else {
                        new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                        try {
                            LoginManager.getInstance().logOut();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        methods.showDialog(LoginActivity.this, getString(R.string.failed), response.body().getMessage(), "alert", "failed");
                    }
                } else {
                    new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                    try {
                        LoginManager.getInstance().logOut();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    methods.showDialog(LoginActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetResponseDetailed> call, @NonNull Throwable t) {
                new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                Toast.makeText(LoginActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void doNormalLogin(String email, String password) {
        new ProgressLoadingDialog(LoginActivity.this).startLoadingdialog();
        Call<GetResponseDetailed> loginResponseCall = Methods.getApi().doLogin("login", BuildConfig.API_KEY, email, password);
        loginResponseCall.enqueue(new Callback<GetResponseDetailed>() {
            @Override
            public void onResponse(@NonNull Call<GetResponseDetailed> call, @NonNull Response<GetResponseDetailed> response) {
                new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isError()) {
                        User user = new User(
                                response.body().getUserList().get(0).getUser_id(),
                                response.body().getUserList().get(0).getUser_type(),
                                response.body().getUserList().get(0).getName(),
                                response.body().getUserList().get(0).getEmail(),
                                response.body().getUserList().get(0).getImg_url(),
                                response.body().getUserList().get(0).getPhone(),
                                response.body().getUserList().get(0).getAuth_id(),
                                response.body().getUserList().get(0).getRegistered_on(),
                                response.body().getUserList().get(0).getFav_ids());
                        new Prefs(getApplicationContext()).addFavorite(user.getFav_ids());
                        LoginPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                        methods.showDialog(LoginActivity.this, getString(R.string.success), response.body().getMessage(), "success", "loginSuccess");
                    } else {
                        new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                        methods.showDialog(LoginActivity.this, getString(R.string.failed), response.body().getMessage(), "alert", "failed");
                    }
                } else {
                    new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                    methods.showDialog(LoginActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                }

            }

            @Override
            public void onFailure(@NonNull Call<GetResponseDetailed> call, @NonNull Throwable t) {
                new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                Toast.makeText(LoginActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        new ProgressLoadingDialog(LoginActivity.this).startLoadingdialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            doSocialLogin("google", user.getDisplayName(), user.getEmail(), String.valueOf(user.getPhotoUrl()), user.getUid());
                        } else {
                            new ProgressLoadingDialog(LoginActivity.this).dismissdialog();
                            Toast.makeText(LoginActivity.this, "Failed to Sign IN", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 221) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            try {
                if (resultCode != 0) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    firebaseAuthWithGoogle(task.getResult().getIdToken());
                } else {
                    //Toast.makeText(LoginActivity.this, getString(R.string.err_login_goole), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                //Toast.makeText(LoginActivity.this, getString(R.string.err_login_goole), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}