package com.ys.bannerlib.loader;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.IOException;


public class SurfacePlay_bak {

    private SurfaceView mSurfaceView;
    private MediaPlayer mediaPlayer;
    private OnPlayListener playListener;
    private SurfaceHolder.Callback mHolderCallback;

    private String path;
    private int position;

    public SurfacePlay_bak(int position) {
        initMediaPlay();
        this.position = position;
    }

    private void initMediaPlay() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mp -> {
            System.out.println("---------------play plsy-----");
            mp.setDisplay(mSurfaceView.getHolder());
            mp.start();
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            if (playListener != null) {
                playListener.onComplete(position);
            }
        });
        mHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                System.out.println("---------------surfaceCreated-----");
                try {
                    stopPlay();
                    mediaPlayer.reset();
                    mediaPlayer.setDisplay(null);
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                System.out.println("---------------surfaceDestroyed-----");
                //holder.removeCallback(mHolderCallback);
                stopPlay();
                mSurfaceView.setVisibility(View.GONE);
            }
        };
    }

    public void setSurfaceView(SurfaceView view) {
        if (mSurfaceView != null && view != mSurfaceView) {
            mSurfaceView.getHolder().removeCallback(mHolderCallback);
        }
        if (view != mSurfaceView) {
            mSurfaceView = view;
            mSurfaceView.getHolder().addCallback(mHolderCallback);
        }
    }

    public void setPlayUrl(String path) {
        this.path = path;
    }

    public void stopPlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void release() {
        stopPlay();
        mediaPlayer.release();
    }

    public void setPlayListener(OnPlayListener listener) {
        this.playListener = listener;
    }

    public interface OnPlayListener{
        void onComplete(int position);
    }

}
