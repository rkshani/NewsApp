package com.andromob.andronews.fragments;

import static android.view.View.VISIBLE;

import static com.andromob.andronews.activity.VideoDetailActivity.isFullScreen;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.andromob.andronews.R;
import com.andromob.andronews.utils.Events;
import com.andromob.andronews.utils.GlobalBus;

import org.greenrobot.eventbus.Subscribe;

public class ExoPlayerFragment extends Fragment {
    public View view;
    private ExoPlayer player;
    private StyledPlayerView playerView;
    private String video_url;
    private ProgressBar progressBar;
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory mediaDataSourceFactory;
    MediaSource videoSource;
    MediaItem mediaItem;
    Uri uri;
    ImageView imgFull;
    private long playerPosition;
    boolean isPlaying = false;
    ImageButton btnPlay, btnPause;

    public ExoPlayerFragment() {
        // Required empty public constructor
    }

    public static ExoPlayerFragment newInstance(String url) {
        ExoPlayerFragment f = new ExoPlayerFragment();
        Bundle args = new Bundle();
        args.putString("video_link", url);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_exo_player, container, false);
        GlobalBus.getBus().register(this);

        if (getArguments() != null) {
            video_url = getArguments().getString("video_link");
        } else {
            Toast.makeText(getActivity(), "Url is empty", Toast.LENGTH_SHORT).show();
        }

        playerView = view.findViewById(R.id.player_view);
        progressBar = view.findViewById(R.id.progressBar);
        btnPlay = view.findViewById(R.id.exo_play);
        btnPause = view.findViewById(R.id.exo_pause);
        imgFull = view.findViewById(R.id.img_full_scr);
        progressBar.setVisibility(VISIBLE);

        BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(getActivity()).build();
        mediaDataSourceFactory = buildDataSourceFactory(true);

        initPlayer();

        btnPlay.setOnClickListener(v ->{
            changePlayPause();
        });

        btnPause.setOnClickListener(v ->{
            changePlayPause();
        });

        player.addListener(new Player.Listener() {

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {

            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                errorDialog();
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                    isPlaying = true;
                    progressBar.setVisibility(View.GONE);
                    btnPlay.setVisibility(View.GONE);
                    btnPause.setVisibility(VISIBLE);
                }
                if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    playerView.setKeepScreenOn(true);
                } else if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(VISIBLE);
                    player.setPlayWhenReady(true);
                } else if (playbackState == Player.STATE_ENDED) {
                    progressBar.setVisibility(View.GONE);
                    isPlaying = false;
                    player.seekTo(0);
                    player.pause();
                    player.setPlayWhenReady(false);
                    btnPlay.setVisibility(VISIBLE);
                    btnPause.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    playerView.setKeepScreenOn(false);
                    isPlaying = false;
                }
            }
        });

        imgFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    isFullScreen = false;
                    Events.FullScreen fullScreen = new Events.FullScreen();
                    fullScreen.setFullScreen(false);
                    GlobalBus.getBus().post(fullScreen);
                    playerPosition = player.getCurrentPosition();
                } else {
                    isFullScreen = true;
                    Events.FullScreen fullScreen = new Events.FullScreen();
                    fullScreen.setFullScreen(true);
                    GlobalBus.getBus().post(fullScreen);
                    playerPosition = player.getCurrentPosition();
                }
            }
        });

        return view;
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
            } else {
                initPlayer();
            }
        }
    }

    private void initPlayer(){
        player = new ExoPlayer.Builder(requireActivity()).build();
        mediaItem = MediaItem.fromUri(video_url);
        player.clearMediaItems();
        player.setMediaItem(mediaItem);
        uri = Uri.parse(video_url);
        videoSource = buildMediaSource(uri, mediaItem, video_url);
        player.addMediaSource(videoSource);
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.requestFocus();
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
        return new DefaultDataSource.Factory(requireActivity(), buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSource.Factory().setUserAgent(Util.getUserAgent(requireActivity(), "ExoPlayerDemo")).setTransferListener(bandwidthMeter);
    }

    public void errorDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
        alertDialog.setTitle(getResources().getString(R.string.msg_whoops));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(getResources().getString(R.string.msg_failed));
        alertDialog.setPositiveButton(getResources().getString(R.string.retry), (dialog, which) ->
                initPlayer());
        alertDialog.setNegativeButton(getResources().getString(R.string.no),
                (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScreen = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            imgFull.setImageResource(R.drawable.ic_fullscreen_exit);
        } else {
            imgFull.setImageResource(R.drawable.ic_fullscreen);
        }
    }

    @Override
    public void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onDestroy();
    }
}
