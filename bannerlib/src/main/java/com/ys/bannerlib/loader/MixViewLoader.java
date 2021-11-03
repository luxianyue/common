package com.ys.bannerlib.loader;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ys.bannerlib.BannerMixHelper;
import com.ys.bannerlib.R;
import com.ys.bannerlib.adapter.BannerMixItem;
import com.ys.bannerlib.databinding.LayoutMixViewBinding;

public class MixViewLoader {

    private final SparseArray<SurfacePlay> mPlays = new SparseArray<>();

    public MixViewLoader() {

    }

    public void displayView(BannerMixItem item, View view, int position) {
        ImageView ivImage = view.findViewById(R.id.iv_image);
        TextureView surView = view.findViewById(R.id.surface_view);
        if (item.getType() == BannerMixHelper.TYPE_IMAGE) {
            surView.setVisibility(View.GONE);
            ivImage.setVisibility(View.VISIBLE);
            RequestManager manager = Glide.with(view.getContext());
            if (item.getUrl().endsWith(".gif")) {
                manager.asGif()
                        .load(item.getUrl())
                        .into(ivImage);
            } else {
                manager.asBitmap()
                        .load(item.getUrl())
                        .into(ivImage);
            }
        }
        if (item.getType() == BannerMixHelper.TYPE_VIDEO) {
            System.out.println("---------------------video");
            ivImage.setVisibility(View.GONE);
            surView.setVisibility(View.VISIBLE);
            SurfacePlay surfacePlay = mPlays.get(position);
            if (surfacePlay == null) {
                surfacePlay = new SurfacePlay(position);
                mPlays.put(position, surfacePlay);
            }
            surfacePlay.setSurfaceView(surView);
            surfacePlay.setPlayListener((SurfacePlay.OnPlayListener) surView.getTag());
            surfacePlay.setPlayUrl(item.getUrl());
            //surfacePlay.setPlayUrl("/sdcard/yuan.mp4");
        }
    }

    public View createDisplayView(ViewGroup parent, SurfacePlay.OnPlayListener listener) {
        LayoutMixViewBinding binding = LayoutMixViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.surfaceView.setTag(listener);
        binding.surfaceView.setVisibility(View.GONE);
        return binding.getRoot();
    }

    public void stopAll() {
        for(int i = 0; i < mPlays.size(); i++) {
            mPlays.valueAt(i).stopPlay();
        }
    }

    public void release(int pos) {
        SurfacePlay play = mPlays.get(pos);
        if (play != null) {
            play.release();
            mPlays.remove(pos);
        }
    }

    public void releaseAll() {
        for(int i = 0; i < mPlays.size(); i++) {
            mPlays.valueAt(i).release();
        }
        mPlays.clear();
    }
}
