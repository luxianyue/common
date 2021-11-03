package com.music.voiceline.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class VisualizerView extends View {

    private byte[] mBytes;
    private float[] mPoints;
    //矩形区域
    private Rect mRect = new Rect();
    // 画笔
    private Paint mForePaint = new Paint();

    // 初始化画笔
    private void init() {
        mBytes = null;
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.GREEN);
    }

    public VisualizerView(Context context) {
        this(context, null);
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        mRect.set(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1f);
            mPoints[i * 4 + 1] = mRect.height() / 2f
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2f) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1f) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2f
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2f) / 128;
        }

        canvas.drawLines(mPoints, mForePaint);
    }

}