package com.andromob.andronews.fragments;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.andromob.andronews.databinding.FragmentYoutubePlayerBinding;
import com.andromob.andronews.utils.Methods;

public class YoutubePlayerFragment extends Fragment {
    FragmentYoutubePlayerBinding binding;
    String videoId;

    public YoutubePlayerFragment() {
        // Required empty public constructor
    }

    public static YoutubePlayerFragment newInstance(String url) {
        YoutubePlayerFragment fragment = new YoutubePlayerFragment();
        Bundle args = new Bundle();
        args.putString("video_link", url);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentYoutubePlayerBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            videoId = new Methods(requireContext()).getVideoId(getArguments().getString("video_link"));
        } else {
            Toast.makeText(requireContext(), "Invalid Url", Toast.LENGTH_SHORT).show();
        }
        binding.webview.setBackgroundColor(Color.BLACK);
        binding.webview.setFocusableInTouchMode(false);
        binding.webview.setFocusable(false);
        binding.webview.getSettings().setDefaultTextEncodingName("UTF-8");
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.getSettings().setDomStorageEnabled(true);
        binding.webview.setVerticalScrollBarEnabled(false);
        binding.webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        binding.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        binding.webview.setScrollbarFadingEnabled(false);
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.webview.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("youtube")){
                    Toast.makeText(requireContext(), "Invalid Action !", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        binding.webview.setWebChromeClient(new myChrome());
        String html = getHTML(videoId);
        binding.webview.loadData(html, "text/html", "UTF-8");
        return binding.getRoot();
    }

    private class myChrome extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalSystemUiVisibility;

        myChrome() {
        }

        @SuppressLint("SourceLockedOrientationActivity")
        public void onHideCustomView() {
            ((FrameLayout) requireActivity().getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = requireActivity().getWindow().getDecorView().getSystemUiVisibility();
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) requireActivity().getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }

    public String getHTML(String videoId) {
        String html = "<iframe class=\"youtube-player\" " + "style=\"border: 0; width: 100%; height: 100%;"
                + "padding:0px; margin:0px\" " + "id=\"ytplayer\" type=\"text/html\" "
                + "src=\"https://www.youtube.com/embed/" + videoId
                + "?rel=0&theme=dark&autohide=2&modestbranding=1&showinfo=0&autoplay=1&enablejsapi=1\fs=0\" frameborder=\"0\" "
                + "allowfullscreen autobuffer allow=\"autoplay\" " + "controls onclick=\"this.play()\">\n" + "</iframe>\n";
        return html;
    }

}