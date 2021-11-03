package com.music.voiceline.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class BarGraphView extends View {

    private int barCount = 30;//条形的数量
    private float rectWidth = 15;//条形的宽度
    private int offset = 6;
    private int speed = 200;
    Paint mPaint;
    private Random random = new Random();

    public BarGraphView(Context context) {
        this(context, null);
    }

    public BarGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setBarCount(int barCount) {
        this.barCount = barCount;
    }

    public void setOffset(int offset) {
        this.offset = offset;

    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        rectWidth = getWidth() / barCount;
        for (int i = 0; i < barCount; i++) {
            //生成0-99的随机数，作为高度的百分比，得出一个随机高度
            int currentHeight = (random.nextInt(100)) * getHeight() / 100;
            canvas.drawRect(rectWidth * i + offset, currentHeight, rectWidth * (i + 1), getHeight(), mPaint);
        }
        postInvalidateDelayed(speed);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int width;
        int spacMode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (spacMode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = 300;
            if (spacMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, size);
            }
        }
        return width;
    }

    private int measureHeight(int heightMeasureSpec) {
        int height;
        int spacMode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (spacMode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = 300;
            if (spacMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, size);
            }
        }
        return height;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LinearGradient linearGradient = new LinearGradient(0, 0, rectWidth, getHeight(), Color.RED, Color.GREEN, Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
    }
}
