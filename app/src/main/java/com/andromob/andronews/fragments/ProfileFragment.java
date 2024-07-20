package com.andromob.andronews.fragments;

import static com.andromob.andronews.utils.Constant.profileBitmap;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.AddNewsActivity;
import com.andromob.andronews.activity.EditProfileActivity;
import com.andromob.andronews.activity.LoginActivity;
import com.andromob.andronews.activity.MainActivity;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileFragment extends Fragment {
    User user;
    ShapeableImageView userImg;
    TextView userName, userEmail;
    LinearLayout ll_addnews, ll_dashboard, ll_reward_history, ll_edit_profile, ll_change_password, ll_delete_account, ll_settings, ll_logout;
    Methods methods;
    RelativeLayout layout_login, mainLyt;
    MaterialButton btnLogin;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        methods = new Methods(requireContext());
        methods.getActionBar().setTitle(getString(R.string.profile));
        layout_login = view.findViewById(R.id.layout_login);
        mainLyt = view.findViewById(R.id.mainLyt);
        btnLogin = view.findViewById(R.id.btnLogin);
        userImg = view.findViewById(R.id.user_img);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        ll_logout = view.findViewById(R.id.ll_logout);
        ll_addnews = view.findViewById(R.id.ll_addnews);
        ll_dashboard = view.findViewById(R.id.ll_dashboard);
        ll_reward_history = view.findViewById(R.id.ll_reward_history);
        ll_edit_profile = view.findViewById(R.id.ll_edit_profile);
        ll_change_password = view.findViewById(R.id.ll_change_password);
        ll_settings = view.findViewById(R.id.ll_settings);
        ll_delete_account = view.findViewById(R.id.ll_delete_account);

        if (!Prefs.getInstance(getContext()).getAppSetting().get(0).isNews_reward_status()) {
            ll_reward_history.setVisibility(View.GONE);
        }

        ll_addnews.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddNewsActivity.class);
            intent.putExtra("type", "add");
            startActivity(intent);

        });

        ll_dashboard.setOnClickListener(v -> methods.replaceFragment(new MyDashboardFragment()));

        ll_reward_history.setOnClickListener(v -> methods.replaceFragment(new RewardFragment()));

        ll_logout.setOnClickListener(v ->
                LoginPrefManager.getInstance(getApplicationContext()).logout());

        ll_edit_profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("type", "profile");
            startActivity(intent);
        });

        ll_change_password.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("type", "password");
            startActivity(intent);
        });

        ll_delete_account.setOnClickListener(v -> {
            new Methods(getContext()).showDialog(getActivity(), getString(R.string.delete_my_account), getString(R.string.delete_account_instruction) + "\n\nEmail : " + getString(R.string.about_us_email_text), "alert", "dismiss");
        });

        ll_settings.setOnClickListener(v -> methods.replaceFragment(new SettingsFragment()));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        if (!LoginPrefManager.getInstance(getContext()).isLoggedIn()) {
            layout_login.setVisibility(View.VISIBLE);
            mainLyt.setVisibility(View.GONE);
        } else {
            updateUserData();
        }
        return view;
    }

    private void updateUserData() {
        user = LoginPrefManager.getInstance(getActivity()).getUser();
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        if (profileBitmap != null) {
            userImg.setImageBitmap(profileBitmap);
        } else {
            methods.loadShapeableImageView(userImg, user.getImg_url());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LoginPrefManager.getInstance(getContext()).isLoggedIn()) {
            mainLyt.setVisibility(View.VISIBLE);
            layout_login.setVisibility(View.GONE);
            updateUserData();
        } else {
            layout_login.setVisibility(View.VISIBLE);
            mainLyt.setVisibility(View.GONE);
        }
        MainActivity.setBottomItemHome("profile");
    }
}