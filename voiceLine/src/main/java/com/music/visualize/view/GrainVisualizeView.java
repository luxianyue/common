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
 * Describe: In GRAIN mode, show spectrum base on a horizontal line, with grain above of the center line.
 */
public class GrainVisualizeView extends AudioVisualizeView{

    private int mOrientation;


    public GrainVisualizeView(Context context) {
        super(context);
    }

    public GrainVisualizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GrainVisualizeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void handleAttr(TypedArray typedArray) {
        mOrientation = typedArray.getInteger(R.styleable.AudioVisualizeView_visualize_orientation, VisualizeMode.HORIZONTAL_LINE_TOP);
    }

    @Override
    protected void drawChild(Canvas canvas) {
        mStrokeWidth = (mRect.width() - (mSpectrumCount - 1) * mItemMargin) / mSpectrumCount;
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < mSpectrumCount; i++) {
            switch (mOrientation) {
                case VisualizeMode.HORIZONTAL_LINE_TOP:
                    drawGrain(canvas, mRect.width() * i / mSpectrumCount, mRect.height() / 2, 2 + mRect.height() / 2 - mRawAudioBytes[i]);
                    break;
                case VisualizeMode.HORIZONTAL_LINE_BOTTOM:
                    drawGrain(canvas, mRect.width() * i / mSpectrumCount, mRect.height() / 2, 2 + mRect.height() / 2 + mRawAudioBytes[i]);
                    break;
                default:
                    break;
            }
        }
    }

    private void drawGrain(final Canvas canvas, final float x, float startY, float endY) {
        canvas.drawPoint(x, endY, mPaint);
        canvas.drawPoint(x, mRect.height() / 4 + endY/2, mPaint);
    }
}
