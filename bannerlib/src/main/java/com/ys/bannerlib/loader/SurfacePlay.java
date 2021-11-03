package com.ys.bannerlib.loader;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.TextureView;
import androidx.annotation.NonNull;
import java.io.IOException;

public class SurfacePlay {

    private TextureView mSurfaceView;
    private MediaPlayer mediaPlayer;
    private OnPlayListener playListener;
    private TextureView.SurfaceTextureListener textureListener;

    private String path;
    private boolean isRelease;
    private final int position;

    public SurfacePlay(int position) {
        this.position = position;
        playListener = pos -> {};
        initMediaPlay();
        init();
    }

    private void init() {
        textureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture texture, int width, int height) {
                System.out.println("====================Available======> " + position);
                try {
                    stopPlay();
                    if (isRelease) {
                        initMediaPlay();
                    } else {
                        mediaPlayer.reset();
                    }
                    mediaPlayer.setSurface(new Surface(texture));
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                    mSurfaceView.postDelayed(()-> playListener.onComplete(position), 6000);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                System.out.println("====================Destroyed====== " + position);
                stopPlay();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        };
    }

    private void initMediaPlay() {
        isRelease = false;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mp -> {
            System.out.println("---------------play plsy-----");
            mp.start();
        });
        mediaPlayer.setOnCompletionListener(mp -> playListener.onComplete(position));
        mediaPlayer.setOnErrorListener((mp, what, extra)-> true);
    }

    public void setSurfaceView(TextureView view) {
        if (view != mSurfaceView) {
            mSurfaceView = view;
            mSurfaceView.setSurfaceTextureListener(textureListener);
        }
    }

    public void setPlayUrl(String path) {
        this.path = path;
    }

    public void stopPlay() {
        if (!isRelease && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void release() {
        stopPlay();
        isRelease = true;
        mediaPlayer.release();
    }

    public void setPlayListener(OnPlayListener listener) {
        if (listener != null) {
            this.playListener = listener;
        }
    }

    public interface OnPlayListener{
        void onComplete(int position);
    }

}
