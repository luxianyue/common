package com.music.visualize.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;


/**
 *
 *
 * Describe: In WAVE mode, show spectrum base on a horizontal line, it will link all points of spectrum so like a wave.
 */
public class WaveVisualizeView extends AudioVisualizeView{

    public WaveVisualizeView(Context context) {
        super(context);
    }

    public WaveVisualizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveVisualizeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void handleAttr(TypedArray typedArray) {

    }

    @Override
    protected void drawChild(Canvas canvas) {
        mStrokeWidth = (mRect.width() - (mSpectrumCount - 1) * mItemMargin) / mSpectrumCount;
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        mPath.moveTo(0, centerY);

        for (int i = 0; i < mSpectrumCount; i++) {
            mPath.lineTo(mRect.width() * i / mSpectrumCount, 2 + mRect.height() / 2 + mRawAudioBytes[i]);
        }
        mPath.lineTo(mRect.width(), centerY);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        mPath.reset();
    }
}
