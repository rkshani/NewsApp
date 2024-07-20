package com.andromob.andronews.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.andromob.andronews.R;
import com.andromob.andronews.utils.Methods;

public class WebViewFragment extends Fragment {
    private WebView webview;
    private ProgressBar progressBar;
    private String title, url;

    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance(String title, String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            url = getArguments().getString("url");
        } else {
            Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            return null;
        }
        new Methods(requireContext()).getActionBar().setTitle(title);
        webview = view.findViewById(R.id.webview);
        progressBar = view.findViewById(R.id.progressBar);
        webview.setBackgroundColor(Color.WHITE);
        webview.setFocusableInTouchMode(false);
        webview.setFocusable(false);
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);

        webview.loadUrl(url);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webview.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }

}
