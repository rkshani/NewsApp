package com.andromob.andronews.fragments;

import android.annotation.SuppressLint;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;

public class AboutFragment extends Fragment {
    public View view;
    TextView tvAboutVersion, tvContact, tvEmail, tvDeveloper, tvDescription;
    Prefs prefs;
    public AboutFragment() {
        // Required empty public constructor
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);
        new Methods(requireContext()).getActionBar().setTitle(getString(R.string.about_us));
        prefs = Prefs.getInstance(requireContext());
        tvAboutVersion = view.findViewById(R.id.tvAboutVersion);
        tvContact = view.findViewById(R.id.tvContact);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvDeveloper = view.findViewById(R.id.tvDeveloper);
        tvDescription = view.findViewById(R.id.tv_description);
        tvAboutVersion.setText(BuildConfig.VERSION_NAME);

        tvContact.setText(prefs.getAppSetting().get(0).getAbout_us_contact());
        tvEmail.setText(prefs.getAppSetting().get(0).getAbout_us_email());
        tvDeveloper.setText(prefs.getAppSetting().get(0).getAbout_us_developer());
        tvDescription.setText(prefs.getAppSetting().get(0).getAbout_us_description());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tvDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            } else {
                tvDescription.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }
        }

        return view;
    }

}
