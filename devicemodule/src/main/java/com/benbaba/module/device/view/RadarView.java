package com.benbaba.module.device.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;


import com.benbaba.module.device.R;
import com.benbaba.module.device.db.DeviceInfo;
import com.benbaba.module.device.utils.RissUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 雷达图
 */
public class RadarView extends View {

    //默认的主题颜色
    private int DEFAULT_COLOR = Color.parseColor("#91D7F4");

    // 圆圈和交叉线的颜色
    private int mCircleColor = DEFAULT_COLOR;
    //圆圈的数量 不能小于1
    private int mCircleNum = 3;
    //圆中心得坐标X
    private int mCircleX;
    //圆中心得坐标Y
    private int mCircleY;
    //外圆得半径
    private int mRadius;
    //扫描的颜色 RadarView会对这个颜色做渐变透明处理
    private int mSweepColor = DEFAULT_COLOR;
    //水滴的颜色
    private int mRaindropColor = DEFAULT_COLOR;
    //水滴的数量 这里表示的是水滴最多能同时出现的数量。因为水滴是随机产生的，数量是不确定的
    private int mRaindropNum = 4;
    //是否显示交叉线
    private boolean isShowCross;
    //是否显示水滴
    private boolean isShowRaindrop;
    //扫描的转速，表示几秒转一圈
    private float mSpeed = 3.0f;
    //水滴显示和消失的速度
    private float mFlicker = 3.0f;
    private Paint mCirclePaint;// 圆的画笔
    private Paint mSweepPaint; //扫描效果的画笔
    private Paint mRaindropPaint;// 水滴的画笔
    private float mDegrees; //扫描时的扫描旋转角度。

    //保存水滴数据
    private ArrayList<Raindrop> mRaindrops = new ArrayList<>();

    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        init();
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        init();
    }

    /**
     * 获取自定义属性值
     *
     * @param context
     * @param attrs
     */
    private void getAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarView);
            mCircleColor = mTypedArray.getColor(R.styleable.RadarView_circleColor, DEFAULT_COLOR);
            mCircleNum = mTypedArray.getInt(R.styleable.RadarView_circleNum, mCircleNum);
            if (mCircleNum < 1) {
                mCircleNum = 3;
            }
            mSweepColor = mTypedArray.getColor(R.styleable.RadarView_sweepColor, DEFAULT_COLOR);
            mRaindropColor = mTypedArray.getColor(R.styleable.RadarView_raindropColor, DEFAULT_COLOR);
            mRaindropNum = mTypedArray.getInt(R.styleable.RadarView_raindropNum, mRaindropNum);
            isShowCross = mTypedArray.getBoolean(R.styleable.RadarView_showCross, true);
            isShowRaindrop = mTypedArray.getBoolean(R.styleable.RadarView_showRaindrop, true);
            mSpeed = mTypedArray.getFloat(R.styleable.RadarView_speed, mSpeed);
            if (mSpeed <= 0) {
                mSpeed = 3;
            }
            mFlicker = mTypedArray.getFloat(R.styleable.RadarView_flicker, mFlicker);
            if (mFlicker <= 0) {
                mFlicker = 3;
            }
            mTypedArray.recycle();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        // 初始化画笔
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(1);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setAntiAlias(true);

        mRaindropPaint = new Paint();
        mRaindropPaint.setStyle(Paint.Style.FILL);
        mRaindropPaint.setAntiAlias(true);

        mSweepPaint = new Paint();
        mSweepPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置宽高,默认200dp
        int defaultSize = dp2px(getContext(), 200);
        int width = measureWidth(widthMeasureSpec, defaultSize);
        int height = measureHeight(heightMeasureSpec, defaultSize);
        mRadius = Math.min(width, height) / 2;
        //计算圆的圆心
        mCircleX = getPaddingLeft() + (width - getPaddingLeft() - getPaddingRight()) / 2;
        mCircleY = getPaddingTop() + (height - getPaddingTop() - getPaddingBottom()) / 2;
        setMeasuredDimension(width, height);
    }

    /**
     * 测量宽
     *
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private int measureWidth(int measureSpec, int defaultSize) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        result = Math.max(result, getSuggestedMinimumWidth());
        return result;
    }

    /**
     * 测量高
     *
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private int measureHeight(int measureSpec, int defaultSize) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        result = Math.max(result, getSuggestedMinimumHeight());
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //或者外圈得圆
        drawCircle(canvas, mCircleX, mCircleY, mRadius);
        if (isShowCross) {
            drawCross(canvas, mCircleX, mCircleY, mRadius);
        }
        //正在扫描
        if (isShowRaindrop) {
            drawRaindrop(canvas);
        }
        drawSweep(canvas, mCircleX, mCircleY, mRadius);
        //计算雷达扫描的旋转角度
//        mDegrees = (mDegrees + (360 / mSpeed / 60)) % 360;
        //触发View重新绘制，通过不断的绘制实现View的扫描动画效果
//        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    ValueAnimator animator;

    public void startAnim() {
        animator = ValueAnimator.ofInt(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDegrees = (mDegrees + (360 / mSpeed / 60)) % 360;
                postInvalidate();
            }
        });
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * 画圆
     */
    private void drawCircle(Canvas canvas, int cx, int cy, int radius) {
        //画mCircleNum个半径不等的圆圈。
        for (int i = 0; i < mCircleNum; i++) {
            canvas.drawCircle(cx, cy, radius - (radius / mCircleNum * i), mCirclePaint);
        }
    }

    /**
     * 画交叉线
     */
    private void drawCross(Canvas canvas, int cx, int cy, int radius) {
        //水平线
        canvas.drawLine(cx - radius, cy, cx + radius, cy, mCirclePaint);

        //垂直线
        canvas.drawLine(cx, cy - radius, cx, cy + radius, mCirclePaint);
    }

    /**
     * 生成水滴。水滴的生成是随机的，并不是每次调用都会生成一个水滴。
     */
    public void generateRaindrop(List<DeviceInfo> infos) {
        // 移除没有扫描到得设备
        Iterator<Raindrop> iterator = mRaindrops.iterator();
        while (iterator.hasNext()) {
            String id = iterator.next().id;
            boolean result = true;
            for (DeviceInfo info : infos) {
                if (info.getDId().equals(id)) {
                    result = false;
                    break;
                }
            }
            if (result) {
                iterator.remove();
            }
        }
        //添加设备
        for (DeviceInfo info : infos) {
            double distance = RissUtils.getDistance(info.getRssi());
            int offset = (int) (distance * mRadius);
            int x, y;
            if ((int) (Math.random() * 2) == 0) {
                x = mCircleX - offset;
            } else {
                x = mCircleX + offset;
            }
            if ((int) (Math.random() * 2) == 0) {
                y = mCircleY - offset;
            } else {
                y = mCircleY + offset;
            }
            Raindrop raindrop = new Raindrop(info.getDId(), x, y, 0, mRaindropColor);
            if (!mRaindrops.contains(raindrop)) {
                mRaindrops.add(raindrop);
            }
        }
    }

    /**
     * 画雨点(就是在扫描的过程中随机出现的点)。
     */
    private void drawRaindrop(Canvas canvas) {
        for (Raindrop raindrop : mRaindrops) {
            mRaindropPaint.setColor(raindrop.changeAlpha());
            canvas.drawCircle(raindrop.x, raindrop.y, raindrop.radius, mRaindropPaint);
            //水滴的扩散和透明的渐变效果
            raindrop.radius += 1.0f * 20 / 60 / mFlicker;
            raindrop.alpha -= 1.0f * 255 / 60 / mFlicker;
            if (raindrop.radius > 20 || raindrop.alpha < 0) {
                raindrop.radius = 0;
                raindrop.alpha = 255;
            }
        }
    }

    /**
     * 画扫描效果
     */
    private void drawSweep(Canvas canvas, int cx, int cy, int radius) {
        //扇形的透明的渐变效果
        SweepGradient sweepGradient = new SweepGradient(cx, cy,
                new int[]{Color.TRANSPARENT, changeAlphas(mSweepColor, 0), changeAlphas(mSweepColor, 168),
                        changeAlphas(mSweepColor, 255), changeAlphas(mSweepColor, 255)
                }, new float[]{0.0f, 0.6f, 0.99f, 0.998f, 1f});
        mSweepPaint.setShader(sweepGradient);
        //先旋转画布，再绘制扫描的颜色渲染，实现扫描时的旋转效果。
        canvas.rotate(-90 + mDegrees, cx, cy);
        canvas.drawCircle(cx, cy, radius, mSweepPaint);
    }

    /**
     * 水滴数据类
     */
    private static class Raindrop {
        String id;//唯一标识
        int x;
        int y;
        float radius;
        int color;
        float alpha = 255;

        public Raindrop(String id, int x, int y, float radius, int color) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }

        /**
         * 获取改变透明度后的颜色值
         *
         * @return
         */
        public int changeAlpha() {
            return changeAlphas(color, (int) alpha);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Raindrop) {
                Raindrop raindrop = (Raindrop) obj;
                if (raindrop.id.equals(this.id)) {
                    return true;
                }
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }


    }

    /**
     * dp转px
     */
    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 改变颜色的透明度
     *
     * @param color
     * @param alpha
     * @return
     */
    private static int changeAlphas(int color, int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

}
