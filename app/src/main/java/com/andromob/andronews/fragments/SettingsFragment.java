package com.andromob.andronews.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.ContactActivity;
import com.andromob.andronews.utils.Config;
import com.andromob.andronews.utils.Methods;

public class SettingsFragment extends Fragment {
    LinearLayout ll_1, ll_2, ll_3, ll_4, ll_5, ll_6;
    ImageView followBtnFb, followBtnInsta, followBtnTwitter, followBtnTelegram, followBtnYoutube;
    Methods methods;

    public SettingsFragment() {
        // Required empty public constructor
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.ll_1) {
                startActivity(new Intent(getActivity(), ContactActivity.class));
            } else if (id == R.id.ll_2) {
                methods.ratePLaystore();
            } else if (id == R.id.ll_3) {
                methods.shareApp();
            } else if (id == R.id.ll_4) {
                methods.replaceFragment(new AboutFragment());
            } else if (id == R.id.ll_5) {
                methods.replaceFragment(WebViewFragment.newInstance(getString(R.string.privacy_policy), Config.PRIVACY_POLICY_URL));
            } else if (id == R.id.ll_6) {
                methods.replaceFragment(WebViewFragment.newInstance(getString(R.string.terms_and_conditions), Config.TERMS_CONDITIONS_URL));
            } else if (id == R.id.followBtnFb) {
                methods.gotoFB();
            } else if (id == R.id.followBtnInsta) {
                methods.gotoinstagram();
            } else if (id == R.id.followBtnTelegram) {
                methods.joinTelegram();
            } else if (id == R.id.followBtnTwitter) {
                methods.gotoTwitter();
            } else if (id == R.id.followBtnYoutube) {
                methods.goToYoutube();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        methods = new Methods(requireContext());
        methods.getActionBar().setTitle(getString(R.string.settings));
        ll_1 = view.findViewById(R.id.ll_1);
        ll_2 = view.findViewById(R.id.ll_2);
        ll_3 = view.findViewById(R.id.ll_3);
        ll_4 = view.findViewById(R.id.ll_4);
        ll_5 = view.findViewById(R.id.ll_5);
        ll_6 = view.findViewById(R.id.ll_6);
        ll_1.setOnClickListener(onClickListener);
        ll_2.setOnClickListener(onClickListener);
        ll_3.setOnClickListener(onClickListener);
        ll_4.setOnClickListener(onClickListener);
        ll_5.setOnClickListener(onClickListener);
        ll_6.setOnClickListener(onClickListener);

        followBtnFb = view.findViewById(R.id.followBtnFb);
        followBtnInsta = view.findViewById(R.id.followBtnInsta);
        followBtnTwitter = view.findViewById(R.id.followBtnTwitter);
        followBtnTelegram = view.findViewById(R.id.followBtnTelegram);
        followBtnYoutube = view.findViewById(R.id.followBtnYoutube);
        followBtnFb.setOnClickListener(onClickListener);
        followBtnInsta.setOnClickListener(onClickListener);
        followBtnYoutube.setOnClickListener(onClickListener);
        followBtnTwitter.setOnClickListener(onClickListener);
        followBtnTelegram.setOnClickListener(onClickListener);
        return view;
    }
}