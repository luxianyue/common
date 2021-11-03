package com.music.visualize.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.music.visualize.VisualizeMode;
import com.music.voiceline.view.R;

/**
 *
 * Describe: In SINGLE mode, show spectrum base on a horizontal line, with jumping above of the center line , it's also default visualize mode.
 */
public class SingleVisualizeView extends AudioVisualizeView{

    private int mOrientation = VisualizeMode.HORIZONTAL_LINE_TOP;


    public SingleVisualizeView(Context context) {
        super(context);
    }

    public SingleVisualizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleVisualizeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void handleAttr(TypedArray typedArray) {
        mOrientation = typedArray.getInteger(R.styleable.AudioVisualizeView_visualize_orientation, mOrientation);
    }

    @Override
    protected void drawChild(Canvas canvas) {
        mStrokeWidth = (mRect.width() - (mSpectrumCount - 1) * mItemMargin) / mSpectrumCount;
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < mSpectrumCount; i++) {
            switch (mOrientation) {
                case VisualizeMode.HORIZONTAL_LINE_TOP:
                    float startX = mRect.width() * i / mSpectrumCount + mStrokeWidth/2;
                    float startY = mRect.height();
                    float stopY = 2 + startY - mRawAudioBytes[i];
                    if (stopY >= startY) {
                        stopY = startY  - (stopY - startY) - 1;
                    }
                    // stopY > startY 则line方向向下， 反之方向向上
                    canvas.drawLine(startX, startY, startX, stopY, mPaint);
                    break;
                case VisualizeMode.HORIZONTAL_LINE_BOTTOM:
                    canvas.drawLine(mRect.width() * i / mSpectrumCount, mRect.height() / 2,mRect.width() * i / mSpectrumCount, 2 + mRect.height() / 2 + mRawAudioBytes[i], mPaint);
                    break;
                default:
                    break;
            }
        }
    }
}
