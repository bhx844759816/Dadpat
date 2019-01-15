package com.benbaba.module.device.view;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 引导箭头得View
 */
public class ArrowGuideView extends View {
    public static final int DEFAULT_WIDTH = 30;
    private static final int DEFAULT_HEIGHT = 20;

    private int mViewWidth;
    private int mViewHeight;

    private Paint mPaint;

    private int mDrawNums = 1;// 绘制得数目
    private ValueAnimator mAnimator;
    private Path[] mPaths;
    private int mDistance;
    private Disposable mDisposable;

    public ArrowGuideView(Context context) {
        this(context, null);
    }

    public ArrowGuideView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowGuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#808080"));
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(dp2px(getContext(), 3));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaths = new Path[3];

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getSize(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mViewWidth = widthSize;
        } else {
            mViewWidth = dp2px(getContext(), DEFAULT_WIDTH);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mViewHeight = heightSize;
        } else {
            mViewHeight = dp2px(getContext(), DEFAULT_HEIGHT);
        }
        mDistance = mViewWidth - mViewHeight - 5;
        initPaths();
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    /**
     * 初始化Path
     */
    private void initPaths() {
        for (int i = 0; i < 3; i++) {
            Path path = new Path();
            path.moveTo(mViewWidth - mDistance * i, 5);
            path.lineTo(mViewWidth - mDistance * i - mViewHeight / 2, mViewHeight / 2);
            path.lineTo(mViewWidth - mDistance * i, mViewHeight - 5);
            mPaths[i] = path;
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mDrawNums; i++) {
            if (mPaths[i] != null) {
                canvas.drawPath(mPaths[i], mPaint);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    /**
     * 开始动画
     */
    @SuppressWarnings("checkresult")
    public void startAnim() {
        if (mDisposable != null) {
            return;
        }
        mDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mDrawNums++;
                        mDrawNums = mDrawNums > 3 ? 1 : mDrawNums;
                        postInvalidate();
                    }
                });
    }

    /**
     * 停止动画
     */
    public void stopAnim() {
        mDrawNums = 3;
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
