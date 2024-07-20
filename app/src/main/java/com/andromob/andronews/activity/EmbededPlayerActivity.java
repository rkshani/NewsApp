package com.andromob.andronews.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andromob.andronews.R;

public class EmbededPlayerActivity extends AppCompatActivity {
    private String source_url;
    RelativeLayout embededPlayer;
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embeded_player);
        embededPlayer = findViewById(R.id.embededPlayer);
        webview = findViewById(R.id.webview);
        embededPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        Intent intent = getIntent();
        if (intent != null) {
            source_url = intent.getStringExtra("source_url");
        } else {
            Toast.makeText(this, "Url is empty", Toast.LENGTH_SHORT).show();
        }

        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setFocusableInTouchMode(false);
        webview.setFocusable(false);
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);

        webview.loadUrl(source_url);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webview.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webview != null) {
            webview.loadUrl("");
        }
    }
}