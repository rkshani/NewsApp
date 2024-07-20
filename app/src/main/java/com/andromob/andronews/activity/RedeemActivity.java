package com.andromob.andronews.activity;

import static com.andromob.andronews.utils.LoadingDialog.dismissdialog;
import static com.andromob.andronews.utils.LoadingDialog.startLoadingdialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.Prefs;
import com.applovin.mediation.ads.MaxAdView;
import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.interfaces.DialogClickListener;
import com.andromob.andronews.models.ModelPost;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.Constant;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.startapp.sdk.ads.banner.Banner;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RedeemActivity extends AppCompatActivity {
    AutoCompleteTextView selectMethod;
    TextInputEditText inputDetails;
    ArrayAdapter<String> methodAdapter;
    String selectedMethod="";
    String txtBalance;
    TextView balance;
    FrameLayout adContainerView;
    User user;
    Methods methods;
    MaterialButton btnSubmit;
    int points;
    double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        user = LoginPrefManager.getInstance(this).getUser();
        methods = new Methods(this, new DialogClickListener() {
            @Override
            public void onClick(String action) {
             if (action.equals("submitSuccess")){
                 finish();
             }
            }
        });
        Intent intent = getIntent();
        points = intent.getIntExtra("points",0);
        amount = intent.getDoubleExtra("amount", 0);
        txtBalance = intent.getStringExtra("balance");
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.redeem_points));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back));
        setSupportActionBar(toolbar);
        selectMethod = findViewById(R.id.selectMethod);
        inputDetails = findViewById(R.id.inputDetails);
        btnSubmit = findViewById(R.id.btnSubmit);
        balance = findViewById(R.id.balance);
        adContainerView = findViewById(R.id.adContainerView);
        balance.setText(txtBalance);

        methodAdapter = new ArrayAdapter<String>(RedeemActivity.this, R.layout.item_list_dropdown, Prefs.getInstance(this).getAppSetting().get(0).getPayment_methods());
        selectMethod.setAdapter(methodAdapter);
        selectMethod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMethod = methodAdapter.getItem(position).toString();
                selectMethod.setError(null);
                selectMethod.clearFocus();
            }
        });

        btnSubmit.setOnClickListener(v -> dataValidation());
        updateAds();

    }

    void dataValidation(){
        final String details = inputDetails.getText().toString().trim();

        if (selectedMethod.isEmpty()) {
            selectMethod.setError(getString(R.string.field_empty_msg));
            selectMethod.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(details)) {
            inputDetails.setError(getString(R.string.field_empty_msg));
            inputDetails.requestFocus();
            return;
        }

        redeemPoints(selectedMethod, details);
    }

    private void redeemPoints(String method, String details) {
        startLoadingdialog(RedeemActivity.this);
        Call<List<ModelPost>> call = Methods.getApi().redeemPoints("redeem", BuildConfig.API_KEY, user.getUser_id(), points, amount, method, details);
        call.enqueue(new Callback<List<ModelPost>>() {
            @Override
            public void onResponse(@NonNull Call<List<ModelPost>> call, @NonNull Response<List<ModelPost>> response) {
                dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().get(0).isError()) {
                        inputDetails.getText().clear();
                        methods.showDialog(RedeemActivity.this, getString(R.string.success), response.body().get(0).getMessage(), "success", "submitSuccess");
                    } else {
                        methods.showDialog(RedeemActivity.this, getString(R.string.failed), response.body().get(0).getMessage(), "alert","failed");
                    }
                } else {
                    dismissdialog();
                    methods.showDialog(RedeemActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ModelPost>> call, @NonNull Throwable t) {
                dismissdialog();
                Toast.makeText(RedeemActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

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