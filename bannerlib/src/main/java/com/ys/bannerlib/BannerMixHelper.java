package com.ys.bannerlib;

import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.listener.OnPageChangeListener;
import com.ys.bannerlib.adapter.BannerMixItem;
import com.ys.bannerlib.adapter.MixViewAdapter;
import com.ys.bannerlib.loader.MixViewLoader;

import java.util.ArrayList;
import java.util.List;

public class BannerMixHelper<T extends BannerMixItem> {

    public static int TYPE_IMAGE = 1;
    public static int TYPE_VIDEO = 2;

    private boolean isAutoPlay;
    private final Banner<T, MixViewAdapter<T>> mBanner;
    private final MixViewAdapter<T> mAdapter;
    private final Runnable mDelayRunnable;
    private final List<OnPageChangePlayCallback> mOnPageChangePlayCallback = new ArrayList<>();

    public BannerMixHelper(Banner<T, MixViewAdapter<T>> banner) {
        this(banner, new ArrayList<>());
    }

    public BannerMixHelper(Banner<T, MixViewAdapter<T>> banner, List<T> data) {
        this(banner, data, null);
    }

    public BannerMixHelper(Banner<T, MixViewAdapter<T>> banner, MixViewLoader loader) {
        this(banner, null, loader);
    }

    public BannerMixHelper(Banner<T, MixViewAdapter<T>> banner, List<T> data, MixViewLoader loader) {
        this.mBanner = banner;
        this.mAdapter = new MixViewAdapter<>(data, banner);
        this.mBanner.setAdapter(mAdapter);
        this.mAdapter.setMixLoader(loader);

        mDelayRunnable = ()-> {
            for (OnPageChangePlayCallback callback : mOnPageChangePlayCallback) {
                int position = mAdapter.getCurrentIndex();
                boolean isLast = position == mAdapter.getRealCount() - 1;
                callback.onPageChange(position, isLast);
            }
            if (isAutoPlay) {
                nextPage();
            }
        };
        mAdapter.setVideoRunnable(mDelayRunnable);
        addOnPageChangeCallback((position, isLast) -> startPlay());

    }

    public void autoPlay() {
        isAutoPlay = true;
    }

    public void startPlay() {
        isAutoPlay = true;
        int index = getCurrentPageIndex();
        if (index < mBanner.getRealCount()) {
            T data = mAdapter.getData(index);
            if (data.getType() == BannerMixHelper.TYPE_IMAGE) {
                mBanner.removeCallbacks(mDelayRunnable);
                mBanner.postDelayed(mDelayRunnable, data.getLoopTime());
            }
        }
    }

    public void stopPlay() {
        isAutoPlay = false;
        mBanner.removeCallbacks(mDelayRunnable);
        if (mAdapter.getMixLoader() != null) {
            mAdapter.getMixLoader().stopAll();
        }
    }

    public void release() {
        stopPlay();
        if (mAdapter.getMixLoader() != null) {
            mAdapter.getMixLoader().releaseAll();
        }
    }

    public void prevPage() {
        setPrevNext(-1);
    }

    public void nextPage() {
        setPrevNext(1);
    }

    private void setPrevNext(int prev) {
        int count = mBanner.getItemCount();
        if (count == 0) {
            return;
        }
        int curr = mBanner.getCurrentItem();
        int index = (curr + prev) % count;
        index = index < 0 ? count + index : index;
        if (prev < 0 && curr == 0 || prev > 0 && curr == count -1) {
            mBanner.setCurrentItem(index, false);
        } else {
            mBanner.setCurrentItem(index);
        }
        T item = mAdapter.getRealData(index);
        if (item.getType() == BannerMixHelper.TYPE_IMAGE) {
            mBanner.removeCallbacks(mDelayRunnable);
            mBanner.postDelayed(mDelayRunnable, item.getLoopTime());
        }
    }

    public void setMixLoader(MixViewLoader loader) {
        mAdapter.setMixLoader(loader);
    }

    public void setDatas(List<T> datas) {
        mBanner.setDatas(datas);
    }

    public void addOnPageChangeCallback(MixViewAdapter.OnPageChangeCallback callback) {
        mAdapter.addOnPageChangeCallback(callback);
    }

    public void addOnPageChangePlayCallback(OnPageChangePlayCallback callback) {
        if (callback != null) {
            mOnPageChangePlayCallback.add(callback);
        }
    }

    public void setOnClickListener(OnBannerListener<T> listener) {
        mBanner.setOnBannerListener(listener);
    }

    public void setOnLongClickListener(MixViewAdapter.OnPageLongClickListener listener) {
        mAdapter.setLongClickListener(listener);
    }

    public void setPageTransformer(int value) {
        mAdapter.setPageTransformer(value);
    }

    public int getCurrentPageIndex() {
        return mAdapter.getCurrentIndex();
    }

    public static void setTypeImage(int type) {
        TYPE_IMAGE = type;
    }

    public static void setTypeVideo(int type) {
        TYPE_VIDEO = type;
    }

    public interface OnPageChangePlayCallback {
        void onPageChange(int position, boolean isLast);
    }
}
