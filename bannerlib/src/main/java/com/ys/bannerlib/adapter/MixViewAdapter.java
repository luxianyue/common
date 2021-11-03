package com.ys.bannerlib.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager2.widget.ViewPager2;

import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.ys.bannerlib.PageChangeListener;
import com.ys.bannerlib.PageTransformerUtils;
import com.ys.bannerlib.loader.MixViewLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MixViewAdapter<T extends BannerMixItem> extends BannerAdapter<T, MixViewHolder> {

    private int mPageTransformerValue = 1;
    private int mCurrentIndex;
    private final Banner mBanner;
    private MixViewLoader mMixLoader;
    private Runnable mVideoRunnable;

    private OnPageLongClickListener mLongClickListener;
    private ViewPager2.PageTransformer pageTransformer;
    private final List<OnPageChangeCallback> mOnPageChangeCallback = new ArrayList<>();

    public MixViewAdapter(List<T> datas, Banner banner) {
        this(datas, banner, null);
    }

    public MixViewAdapter(List<T> datas, Banner banner, MixViewLoader loader) {
        super(datas);
        this.mBanner = banner;
        this.mMixLoader = loader;
        initBanner();
        mVideoRunnable = ()->{};
    }

    public void setMixLoader(MixViewLoader loader) {
        this.mMixLoader = loader;
        notifyDataSetChanged();
    }

    @Override
    public MixViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (mMixLoader != null) {
            itemView = mMixLoader.createDisplayView(parent, position -> mVideoRunnable.run());
        }
        return new MixViewHolder(itemView);
    }


    @Override
    public void onBindView(MixViewHolder holder, T data, int position, int size) {
        holder.itemView.setOnLongClickListener(v -> {
            if (mLongClickListener != null) {
                return mLongClickListener.onLongClick(v, position);
            }
            return false;
        });
        if (mMixLoader != null) {
            mMixLoader.displayView(data, holder.getView(), position);
        }
    }

    public void setPageTransformer(int value) {
        mPageTransformerValue = value;
    }

    private void initBanner() {
        if (mBanner == null) {
            return;
        }
        mBanner.addOnPageChangeListener(new PageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
                BannerMixItem data = getData(position);
                boolean isLast = position == getRealCount() - 1;
                for (OnPageChangeCallback callback : mOnPageChangeCallback) {
                    callback.onPageChange(position, isLast);
                }
                if (!MixViewAdapter.this.onPageSelected(position)) {
                    int formerValue = mPageTransformerValue;
                    if (formerValue == 1) {
                        formerValue = new Random().nextInt(22);
                    }
                    ViewPager2.PageTransformer former = PageTransformerUtils.getTransformer(formerValue);
                    if (pageTransformer != former) {
                        pageTransformer = former;
                        mBanner.setPageTransformer(pageTransformer);
                    }
                }
            }
        });
    }

    public void setVideoRunnable(Runnable run) {
        if (run != null) {
            mVideoRunnable = run;
        }
    }

    public void setLongClickListener(OnPageLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    public void addOnPageChangeCallback(OnPageChangeCallback callback) {
        if (callback != null) {
            mOnPageChangeCallback.add(callback);
        }
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public boolean onPageSelected(int position) {
        return false;
    }

    public MixViewLoader getMixLoader() {
        return mMixLoader;
    }

    public interface OnPageChangeCallback {
        void onPageChange(int position, boolean isLast);
    }

    public interface OnPageLongClickListener {
        boolean onLongClick(View v, int position);
    }

}
