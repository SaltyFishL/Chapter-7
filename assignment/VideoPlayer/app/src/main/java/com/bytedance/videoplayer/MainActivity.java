package com.bytedance.videoplayer;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


public class MainActivity extends Activity {

    private VideoView mVideoView;
    private SeekBar mSeekBar;
    private TextView mTvTotalTime;
    private TextView mTvCurrentTime;
    private int mProgress;

    private static final String TAG = "MainActivity";
    public static final String VIDEO_PROGRESS = "videoProgress";

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mVideoView.isPlaying()) {
                int currentTime = mVideoView.getCurrentPosition();
                int totalTime = mVideoView.getDuration();
                int currentMin = currentTime / 1000 / 60;
                int currentSec = currentTime / 1000 % 60;
                mSeekBar.setProgress((int) (currentTime / (float) totalTime * 100));

                mTvCurrentTime.setText(String.format("%02d", currentMin) + ":" + String.format("%02d", currentSec));
            }
            mHandler.postDelayed(mRunnable, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        if (savedInstanceState != null) {
            mProgress = savedInstanceState.getInt(VIDEO_PROGRESS);
            Log.d(TAG, "onCreate: mProgress = " + mProgress);
        }


        mTvTotalTime = findViewById(R.id.text_view_total_time);
        mTvCurrentTime = findViewById(R.id.text_view_current_time);

        mVideoView = findViewById(R.id.videoView);
        Log.d(TAG, "onCreate: mVideoView链接上了");
        mVideoView.setVideoPath(getVideoPath(R.raw.bytedance));

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
                mHandler.postDelayed(mRunnable, 0);
                int totalTime = mVideoView.getDuration();
                int totalMin = totalTime / 1000 / 60;
                int totalSec = totalTime /1000 % 60;
                mTvTotalTime.setText(String.format("%02d", totalMin) + ":" + String.format("%02d", totalSec));
            }
        });

        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                } else {
                    mVideoView.start();
                }
            }
        });


        mSeekBar = findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Log.d("拖动过程中的值：", String.valueOf(progress) + ", " + String.valueOf(fromUser));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("开始滑动时的值：", String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("停止滑动时的值：", String.valueOf(seekBar.getProgress()));
                mVideoView.seekTo((int) (mVideoView.getDuration() * seekBar.getProgress() * 0.01));

                int currentTime = mVideoView.getCurrentPosition();
                int currentMin = currentTime / 1000 / 60;
                int currentSec = currentTime / 1000 % 60;
                mTvCurrentTime.setText(String.format("%02d", currentMin) + ":" + String.format("%02d", currentSec));


            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: progress = " + mVideoView.getCurrentPosition());
        outState.putInt(VIDEO_PROGRESS, mVideoView.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //横屏
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Remove title bar
//            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            //Remove notification bar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Log.d(TAG, "onConfigurationChanged: 横屏");
        } else {
            WindowManager.LayoutParams attr = MainActivity.this.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            MainActivity.this.getWindow().setAttributes(attr);
            Log.d(TAG, "onConfigurationChanged: 竖屏");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);

    }

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
}
