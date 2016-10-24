package com.feliperrm.animationsproject.activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.feliperrm.animationsproject.R;


public class VideoPlayActivity extends BaseActivity {

    VideoView videoView;
    Uri videoUri;
    public static final String VIDEO_URI_KEY = "videourikey";
    MediaController mc;
    ProgressBar progressBar;
    TextView loaderText;

    int lastVideoPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide Status Bar
        setContentView(R.layout.activity_video_play);
        recoverInfoFromBundle();
        findViews();
        setUpViews();
    }

    private void recoverInfoFromBundle(){
        String url = getIntent().getStringExtra(VIDEO_URI_KEY);
        videoUri = Uri.parse(url);
    }

    private void findViews(){
        videoView = (VideoView) findViewById(R.id.videoView);
        progressBar = (ProgressBar) findViewById(R.id.videoLoading);
        loaderText = (TextView) findViewById(R.id.loadingText);
    }

    private void setUpViews(){
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                loaderText.setVisibility(View.GONE);
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mc = new MediaController(VideoPlayActivity.this, false);
                        videoView.setMediaController(mc);
                        mc.setAnchorView(videoView);
                        mc.show();
                    }
                });
            }
        });

        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        lastVideoPos = videoView.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mc!=null){
            videoView.seekTo(lastVideoPos);
            videoView.requestFocus();
            mc.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        videoView.stopPlayback();
    }
}
