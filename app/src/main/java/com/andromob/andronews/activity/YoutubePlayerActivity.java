package com.andromob.andronews.activity;

import static com.andromob.andronews.activity.MainActivity.fragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.andromob.andronews.R;
import com.andromob.andronews.databinding.ActivityYoutubePlayerBinding;
import com.andromob.andronews.fragments.YoutubePlayerFragment;

public class YoutubePlayerActivity extends AppCompatActivity {
    ActivityYoutubePlayerBinding binding;
    String live_url;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = ActivityYoutubePlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        live_url = intent.getStringExtra("url");

        YoutubePlayerFragment youtubePlayerFragment = YoutubePlayerFragment.newInstance(live_url);
        fragmentManager.beginTransaction().replace(R.id.youtubeVideoPlayer, youtubePlayerFragment).commitAllowingStateLoss();
    }

}