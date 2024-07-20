package com.andromob.andronews.activity;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import com.andromob.andronews.R;

public class VideoPlayerActivity extends AppCompatActivity {
    private ExoPlayer player;
    private ImageView imageView, imageView_resize_mode;
    private StyledPlayerView playerView;
    private String video_url;
    private ProgressBar progressBar;
    private boolean isLandScape = false;
    int RESIZE_MODE = 0;
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory mediaDataSourceFactory;
    MediaSource videoSource;
    MediaItem mediaItem;
    Uri uri;
    ImageButton btnPlay, btnPause;
    boolean isPlaying = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Intent intent = getIntent();
        if (!intent.getStringExtra("url").isEmpty()) {
            video_url = intent.getStringExtra("url");
        } else {
            errorDialog();
        }

        imageView = findViewById(R.id.imageView_full_video_play);
        imageView_resize_mode = findViewById(R.id.imageView_resize_mode);
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progressbar_player);
        btnPlay = findViewById(R.id.exo_play);
        btnPause = findViewById(R.id.exo_pause);
        progressBar.setVisibility(VISIBLE);

        btnPlay.setOnClickListener(v ->{
            changePlayPause();
        });

        btnPause.setOnClickListener(v ->{
            changePlayPause();
        });

        BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(this).build();
        TrackSelector trackSelector = new DefaultTrackSelector(this);
        mediaDataSourceFactory = buildDataSourceFactory(true);
        player = new ExoPlayer.Builder(this).setTrackSelector(trackSelector).build();

        initPlayer();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                errorDialog();
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                    isPlaying = true;
                    progressBar.setVisibility(View.GONE);
                }
                if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    playerView.setKeepScreenOn(true);
                } else if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(VISIBLE);
                    player.setPlayWhenReady(true);
                } else {
                    progressBar.setVisibility(View.GONE);
                    playerView.setKeepScreenOn(false);
                    isPlaying = false;
                }
            }
        });

        imageView_resize_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RESIZE_MODE == 0) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                    RESIZE_MODE = 1;
                    //Toast.makeText(getApplicationContext(), "FIXED WIDTH", Toast.LENGTH_SHORT).show();
                } else if (RESIZE_MODE == 1) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                    RESIZE_MODE = 2;
                    //Toast.makeText(getApplicationContext(), "FIXED HEIGHT", Toast.LENGTH_SHORT).show();
                } else if (RESIZE_MODE == 2) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    RESIZE_MODE = 3;
                    //Toast.makeText(getApplicationContext(), "FILL", Toast.LENGTH_SHORT).show();
                } else if (RESIZE_MODE == 3) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    RESIZE_MODE = 4;
                    //Toast.makeText(getApplicationContext(), "FIT", Toast.LENGTH_SHORT).show();
                } else if (RESIZE_MODE == 4) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    RESIZE_MODE = 0;
                    //Toast.makeText(getApplicationContext(), "ZOOM", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLandScape) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.exo_controls_fullscreen_enter));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    isLandScape = true;
                } else {
                    imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.exo_controls_fullscreen_exit));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    isLandScape = false;
                }
            }
        });

    }

    void changePlayPause(){
        if (isPlaying){
            if (player != null && player.isPlaying()){
                isPlaying = false;
                player.pause();
                btnPlay.setVisibility(VISIBLE);
                btnPause.setVisibility(View.GONE);
            }
        } else {
            if (player != null) {
                player.play();
                isPlaying = true;
                btnPlay.setVisibility(View.GONE);
                btnPause.setVisibility(VISIBLE);
            }
        }
    }

    private void initPlayer(){
        mediaItem = MediaItem.fromUri(video_url);
        player.clearMediaItems();
        player.setMediaItem(mediaItem);
        uri = Uri.parse(video_url);
        videoSource = buildMediaSource(uri, mediaItem, video_url);
        player.addMediaSource(videoSource);
        playerView.setPlayer(player);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri, MediaItem mediaItem, String video_url) {
        int type = Util.inferContentType(uri);
        mediaItem = MediaItem.fromUri(video_url);
        switch (type) {
            case C.CONTENT_TYPE_SS:
                return new SsMediaSource.Factory(new DefaultSsChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(mediaItem);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSource.Factory(this, buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSource.Factory().setUserAgent(Util.getUserAgent(this, "ExoPlayerDemo")).setTransferListener(bandwidthMeter);
    }

    public void errorDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.msg_whoops));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(getResources().getString(R.string.msg_failed));
        alertDialog.setPositiveButton(getResources().getString(R.string.retry), (dialog, which) ->
                initPlayer());
        alertDialog.setNegativeButton(getResources().getString(R.string.no),
                (dialogInterface, i) -> finish());
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onDestroy();
    }
}