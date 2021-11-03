package com.music.voiceline.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * kk-小德
 *
 **/
public class SiriWaveView extends View {

    private int mWidth, mHeight;
    private int maxAmplitude; //最大震动幅度

    private static final int MAX_WAVE_COUNT = 15;


    //paint
    private Paint mPaint;

    //random
    private Random random;

    private float mVolume; //
    private int mDrawCount; //

    //参数
    private float yAmplitude = 0.3f; //纵向振幅
    private float xAmplitude = 1f; //横向振幅
    private int waveCount = 0; //波形数量

    //波形
    private List<Wave> waves;


    //颜色
    private final int[] COLORS = {R.color.green, R.color.blue, R.color.pink};
    private final int[] lineColors = new int[]{0xFF111111, 0xFFFFFFFF, 0xFFFFFFFF, 0xFF111111};
    private final float[] linepositions = new float[]{0f, 0.1f, 0.9f, 1};

    //是否在动画
    private boolean running;
    private Handler handler;


    public SiriWaveView(Context context) {
        this(context,null);
    }

    public SiriWaveView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SiriWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint();
        random = new Random();
        waves = new ArrayList<>();
        running = false;

        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        running = true;
                        createWave();
                        activityWaveCount(waveCount);
                        postInvalidate();
                        break;
                }
            }
        };
    }

    /**
     * 0到100
     * @param volume [0,100]
     */
    public void setVolume(float volume) {
        if (volume < 0) {
            return;
        }
        mVolume = volume;
        if (volume <= 10) {
            yAmplitude = 0.1f;
            waveCount = 5;
            xAmplitude = 1.2f;
        } else if (volume < 40) {
            yAmplitude = 0.5f;
            waveCount = 10;
            xAmplitude = 1;
        } else if (volume <60) {
            yAmplitude = 0.9f;
            waveCount = 15;
        } else {
            yAmplitude = 1.2f;
            waveCount = 20;
        }

        activityWaveCount(waveCount);
    }

    public void startAnim() {
        if (!running)
            handler.sendEmptyMessageDelayed(1, 100);
    }

    public void stopAnim() {
        running = false;
        waveCount = 0;

        activityWaveCount(waveCount);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;

        maxAmplitude = h * 2 / 3;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }


    /**
     * 激活可以动的波形
     * @param count
     */
    private void activityWaveCount(int count) {
        int size = waves.size();
        if (count > size) {
            count = size;
        }
        for (int i = 0; i < size; i++) {
            waves.get(i).playing = i < count;
        }
    }


    private void createWave() {
        if (waves == null) {
            waves = new ArrayList<>();
        }
        waves.clear();
        for (int i = 0; i < MAX_WAVE_COUNT; i++) {
            Wave wave = new Wave();
            initAnimator(wave);
            waves.add(wave);
        }
    }

    /**
     * 绘制波形
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        if (!running) {
            return;
        }
        drawLine(canvas);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        for (Wave wave : waves) {
            if (wave.playing) {
                wave.draw(canvas, mPaint);
            }
        }
        postInvalidateDelayed(20);
        mDrawCount++;
        if (mDrawCount > 10 && mVolume > 0) {
            mVolume--;
            mDrawCount = 0;
            setVolume(mVolume--);
        }
    }

    private void initAnimator(final Wave waveBean) {
        ValueAnimator animator = ValueAnimator.ofInt(0, waveBean.maxHeight);
        animator.setDuration(waveBean.duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                waveBean.waveHeight = (int) animation.getAnimatedValue();
                if (waveBean.waveHeight > waveBean.maxHeight / 2) {
                    waveBean.waveHeight = waveBean.maxHeight - waveBean.waveHeight;
                }
//                Log.e("AAA-->", "initAnimator: " + waveBean.toString());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (waveBean.playing) {
                    waveBean.respawn();
                    initAnimator(waveBean);
                }
                //waves.remove(waveBean);
            }
        });
        animator.start();
    }

    private void drawLine(Canvas canvas) {
        canvas.save();
        LinearGradient shader = new LinearGradient(
                mWidth / 40f, 0,
                mWidth * 39f / 40, 0,
                lineColors,
                linepositions,
                Shader.TileMode.MIRROR);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        mPaint.setShader(shader);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(mWidth / 40f, mHeight / 2f, mWidth * 39f / 40, mHeight / 2f, mPaint);
        mPaint.setXfermode(null);
        mPaint.setShader(null);
        mPaint.clearShadowLayer();
        canvas.restore();
    }


    /**
     * 单个波形
     */
    class Wave {
        boolean playing = false;
        int maxHeight;
        int maxWidth;
        int color;
        float speed = 0.3f;
        double seed, open_class;

        int waveHeight;
        int duration;
        Paint mPaint;

        public Wave() {
            mHeight = getMeasuredHeight();
            mWidth = getMeasuredWidth();
            respawn();
        }

        public void respawn() {
            this.seed = Math.random();  // 位置
            maxWidth = (random.nextInt(mWidth / 16) + mWidth * 3 / 11);
            if (seed <= 0.2) {
                maxHeight = random.nextInt(maxAmplitude / 6) + maxAmplitude / 5;
                open_class = 2;
            } else if (seed <= 0.3) {
                maxHeight = random.nextInt(maxAmplitude / 3) + maxAmplitude / 5;
                open_class = 3;
            } else if (seed <= 0.7) {
                maxHeight = random.nextInt(maxAmplitude / 2) + maxAmplitude * 2 / 5;
                open_class = 3;
            } else if (seed <= 0.8) {
                maxHeight = random.nextInt(maxAmplitude / 3) + maxAmplitude / 5;
                open_class = 3;
            } else if (seed > 0.8) {
                maxHeight = random.nextInt(maxAmplitude / 6) + maxAmplitude / 5;
                open_class = 2;
            }
            duration = random.nextInt(1000) + 1000;
            color = COLORS[random.nextInt(COLORS.length)];
        }

        double equation(double i) {
            i = Math.abs(i);
            return -yAmplitude * Math.pow(1 / (1 + Math.pow(open_class * i, 2)), 2);
        }

        public void draw(Canvas canvas, Paint mPaint) {
            this.mPaint = mPaint;

            this._draw(1, canvas);
        }

        private void _draw(int m, Canvas canvas) {

            Path path = new Path();
            Path pathN = new Path();
            path.moveTo(mWidth / 4f, mHeight / 2f);
            pathN.moveTo(mWidth / 4f, mHeight / 2f);
            double x_base = mWidth / 2f  // 波浪位置
                    + (-mWidth / 6f + this.seed
                    * (mWidth / 3f));
            double y_base = mHeight / 2f;

            double x, y, x_init = 0;
            double i = -1;
            while (i <= 1) {
                x = x_base + i * maxWidth * xAmplitude;
                double function = equation(i) * waveHeight;
                y = y_base + function;
                if (x_init > 0 || x > 0) {
                    x_init = mWidth / 4f;
                }
                if (y > 0.1) {
                    path.lineTo((float) x, (float) y);
                    pathN.lineTo((float) x, (float) (y_base - function));
                }
                i += 0.01;
            }
            mPaint.setColor(getResources().getColor(color));
            canvas.drawPath(path, mPaint);
            canvas.drawPath(pathN, mPaint);

        }

        @Override
        public String toString() {
            return "Wave{" +
                    "maxHight=" + maxHeight +
                    ", maxWidth=" + maxWidth +
                    ", color=" + color +
                    ", speed=" + speed +
                    ", amplitude=" + yAmplitude +
                    ", seed=" + seed +
                    ", open_class=" + open_class +
                    ", mPaint=" + mPaint +
                    '}';
        }
    }
}
