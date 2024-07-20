package com.andromob.andronews.activity;

import static com.andromob.andronews.utils.Constant.profileBitmap;
import static com.andromob.andronews.utils.LoadingDialog.dismissdialog;
import static com.andromob.andronews.utils.LoadingDialog.startLoadingdialog;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.andromob.andronews.utils.AdsManager;
import com.applovin.mediation.ads.MaxAdView;
import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.interfaces.DialogClickListener;
import com.andromob.andronews.models.GetResponse;
import com.andromob.andronews.models.GetResponseDetailed;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.ExifUtils;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.startapp.sdk.ads.banner.Banner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    MaterialToolbar toolbar;
    FloatingActionButton btnChangePicture;
    LinearLayout lyt_profile_update, lyt_password_update;
    ShapeableImageView user_img;
    Bitmap userProfileBitmap;
    User user;
    Methods methods;
    Uri profileImgUri = null;
    EditText editTextName, editTextEmail, editTextPhone, editTextPassword, editTextConfirmPassword;
    TextView userName;
    MaterialButton btnUpdate;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    FrameLayout adContainerView;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        initUiComponents(type);
    }

    private void initUiComponents(String type) {
        toolbar = findViewById(R.id.toolbar);
        if (type.equals("profile")) {
            toolbar.setTitle(getString(R.string.edit_profile));
        } else {
            toolbar.setTitle(getString(R.string.change_password));
        }
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back));
        setSupportActionBar(toolbar);
        checkPermissions();
        adContainerView = findViewById(R.id.adContainerView);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        user = LoginPrefManager.getInstance(getApplicationContext()).getUser();
        methods = new Methods(this, new DialogClickListener() {
            @Override
            public void onClick(String action) {
                switch (action) {
                    case "updateSuccess":
                        finish();
                        break;
                    case "failed":
                        //
                        break;
                }
            }
        });

        user_img = findViewById(R.id.user_img);
        userName = findViewById(R.id.user_name);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnUpdate = findViewById(R.id.btn_udpate);
        btnChangePicture = findViewById(R.id.btn_change_picture);
        lyt_profile_update = findViewById(R.id.lyt_profile_update);
        lyt_password_update = findViewById(R.id.lyt_password_update);
        userName.setText(user.getName());
        if (type.equals("profile")) {
            addValue(user);
            btnChangePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                }
            });
            lyt_password_update.setVisibility(View.GONE);
            lyt_profile_update.setVisibility(View.VISIBLE);
        } else {
            if (profileBitmap != null) {
                user_img.setImageBitmap(profileBitmap);
            } else {
                methods.loadShapeableImageView(user_img, user.getImg_url());
            }
            btnChangePicture.setVisibility(View.GONE);
            lyt_profile_update.setVisibility(View.GONE);
            lyt_password_update.setVisibility(View.VISIBLE);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("profile")) {
                    dataValidationProfile();
                } else {
                    dataValidationPassword();
                }
            }
        });
    }

    private void dataValidationPassword() {
        final String password = editTextPassword.getText().toString().trim();
        final String confirm_pass = editTextConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(getString(R.string.field_empty_msg));
            editTextPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirm_pass)) {
            editTextConfirmPassword.setError(getString(R.string.enter_confirm_password_msg));
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (!confirm_pass.equals(password)) {
            editTextConfirmPassword.setError(getString(R.string.password_not_match_msg));
            editTextConfirmPassword.requestFocus();
            return;
        }
        updatePassword(user.getUser_id(), user.getEmail(), password);
    }

    private void updatePassword(int id, String email, String password) {
        startLoadingdialog(EditProfileActivity.this);
        Call<GetResponse> loginResponseCall = Methods.getApi().updatePassword("updatePassword", BuildConfig.API_KEY, id, email, password);
        loginResponseCall.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetResponse> call, @NonNull Response<GetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dismissdialog();
                    if (!response.body().isError()) {
                        editTextPassword.getText().clear();
                        editTextConfirmPassword.getText().clear();
                        methods.showDialog(EditProfileActivity.this, getString(R.string.success), response.body().getMessage(), "success", "updateSuccess");
                    } else {
                        methods.showDialog(EditProfileActivity.this, getString(R.string.failed), response.body().getMessage(), "alert", "failed");
                    }
                } else {
                    dismissdialog();
                    methods.showDialog(EditProfileActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetResponse> call, @NonNull Throwable t) {
                dismissdialog();
                Toast.makeText(EditProfileActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addValue(User user) {
        editTextName.setText(user.getName());
        editTextEmail.setText(user.getEmail());
        editTextPhone.setText(user.getPhone());
        if (profileBitmap != null) {
            user_img.setImageBitmap(profileBitmap);
        } else {
            methods.loadShapeableImageView(user_img, user.getImg_url());
        }
    }

    private void dataValidationProfile() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
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

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError(getString(R.string.field_empty_msg));
            editTextPhone.requestFocus();
            return;
        }

        updateProfileData(userProfileBitmap != null, name, email, phone, String.valueOf(user.getUser_id()));

    }

    private void updateProfileData(Boolean isImage, String name, String email, String phone, String id) {
        startLoadingdialog(EditProfileActivity.this);
        Call<GetResponseDetailed> loginResponseCall = null;
        if (isImage) {
            String uniqueID = UUID.randomUUID().toString();
            File file = new File(getApplicationContext().getCacheDir(), uniqueID + "-temp-profile.png");
            try {
                file.createNewFile();
                Bitmap bitmap = userProfileBitmap;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            RequestBody requestFile = RequestBody.create(file, MediaType.parse(getContentResolver().getType(profileImgUri)));
            MultipartBody.Part imgBody =
                    MultipartBody.Part.createFormData("img_url", file.getName(), requestFile);

            RequestBody nameBody = RequestBody.create(name, MediaType.parse("multipart/form-data"));
            RequestBody emailBody = RequestBody.create(email, MediaType.parse("multipart/form-data"));
            RequestBody phoneBody = RequestBody.create(phone, MediaType.parse("multipart/form-data"));
            RequestBody IdBody = RequestBody.create(String.valueOf(id), MediaType.parse("multipart/form-data"));

            loginResponseCall = Methods.getApi().updateProfileWithImage("updateProfile",
                    BuildConfig.API_KEY, nameBody, emailBody, phoneBody, imgBody, IdBody);
            profileBitmap = userProfileBitmap;
        } else {
            loginResponseCall = Methods.getApi().updateProfile("updateProfile",
                    BuildConfig.API_KEY, name, email, phone, id);
        }
        loginResponseCall.enqueue(new Callback<GetResponseDetailed>() {
            @Override
            public void onResponse(@NonNull Call<GetResponseDetailed> call, @NonNull Response<GetResponseDetailed> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dismissdialog();
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
                        new Prefs(getApplicationContext()).addFavorite(user.getFav_ids());
                        methods.showDialog(EditProfileActivity.this, getString(R.string.success), response.body().getMessage(), "success", "updateSuccess");
                    } else {
                        methods.showDialog(EditProfileActivity.this, getString(R.string.failed), response.body().getMessage(), "alert", "failed");
                    }
                } else {
                    dismissdialog();
                    methods.showDialog(EditProfileActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetResponseDetailed> call, @NonNull Throwable t) {
                dismissdialog();
                Toast.makeText(EditProfileActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            if (result.getData() != null) {
                                profileImgUri = result.getData().getData();
                                userProfileBitmap = ExifUtils.getRotatedBitmap(getApplicationContext(), profileImgUri);
                                user_img.setImageBitmap(userProfileBitmap);
                            }
                        } catch (Exception e) {
                            Log.d("TAG", "" + e.getLocalizedMessage());
                        }
                    }
                }
            });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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