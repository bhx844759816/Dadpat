package com.benbaba.dadpat.host.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.bean.WifiBean;
import com.benbaba.dadpat.host.callback.OnWifiSearchViewCallBack;
import com.benbaba.dadpat.host.utils.L;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 搜索附件设备得view
 */
@SuppressWarnings("checkresult")
public class SearchWifiView extends View {
    private static final int DEFAULT_RING_COLOR = Color.parseColor("#60FFFFFF");
    public static final int DEFAULT_RING_NUMS = 3;
    public static final int DEFAULT_RING_DISTANCE = 30;
    public static final int DEFAULT_RING_WIDTH = 5;
    public static final int DEFAULT_RING_ANGLE = 90;
    private Bitmap mWifiBitmap;
    private Bitmap mDeviceMarkBitmap;
    private Paint mBitmapPaint;
    private Paint mCirclePaint;
    private Paint mStartCirclePaint;
    private int mRingNums;//圆环得个数
    private int mRingDistance;//圆环得间距
    private int mRingWidth;//圆环得宽度
    private int mRingAngle;//圆环得角度
    private int mRingColor;//圆环得角度
    private int mViewWidth;
    private int mViewHeight;

    private List<Ring> mRingList;
    private List<Device> mDeviceList;
    private int mIndex;
    private OnWifiSearchViewCallBack mCallBack;
    int connCount;
    private boolean isLoop;

    public SearchWifiView(Context context) {
        this(context, null);
    }

    public SearchWifiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchWifiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * init
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mRingList = new ArrayList<>();
        mDeviceList = new ArrayList<>();
        mWifiBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wifi_center);
        mDeviceMarkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.device_mark);
        TypedArray typedArray = context.obtainStyledAttributes(attrs
                , R.styleable.SearchWifiView, defStyleAttr, 0);
        mRingColor = typedArray.getColor(R.styleable.SearchWifiView_ring_color, DEFAULT_RING_COLOR);
        mRingNums = typedArray.getInteger(R.styleable.SearchWifiView_ring_num, DEFAULT_RING_NUMS);
        mRingDistance = typedArray.getDimensionPixelSize(R.styleable.SearchWifiView_ring_distance,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_RING_DISTANCE,
                        getResources().getDisplayMetrics()));
        mRingWidth = typedArray.getDimensionPixelSize(R.styleable.SearchWifiView_ring_width,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_RING_WIDTH,
                        getResources().getDisplayMetrics()));
        mRingAngle = typedArray.getInteger(R.styleable.SearchWifiView_ring_angle, DEFAULT_RING_ANGLE);
        typedArray.recycle();
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mCirclePaint.setStrokeWidth(mRingWidth);
        mStartCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStartCirclePaint.setAntiAlias(true);
        mStartCirclePaint.setDither(true);
        mStartCirclePaint.setStyle(Paint.Style.FILL);
//        mStartCirclePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mCirclePaint.setColor(mRingColor);
        mStartCirclePaint.setColor(mRingColor);
        for (int i = 0; i < mRingNums; i++) {
            Ring ring = new Ring();
            ring.rectF = new RectF();
            ring.index = i;
            mRingList.add(ring);
        }
        isLoop = true;
    }


    /**
     * 设置回调监听
     *
     * @param callBack
     */
    public void setWifiSearchCallBack(OnWifiSearchViewCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0, 0);
        // 画中心得圆
        canvas.drawBitmap(mWifiBitmap, mViewWidth / 2 - mWifiBitmap.getWidth() / 2,
                mViewHeight / 2 - mWifiBitmap.getHeight() / 2, mBitmapPaint);
        for (int i = 0; i < mIndex; i++) {
            Ring ring = mRingList.get(i);
            drawRightRing(canvas, ring);
            drawLeftRing(canvas, ring);
        }
        canvas.save();
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        for (Device device : mDeviceList) {
            canvas.drawBitmap(mDeviceMarkBitmap, null, device.rect, mBitmapPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX() - mViewWidth / 2;
                int y = (int) event.getY() - mViewHeight / 2;
                for (Device device : mDeviceList) {
                    if (device.rect.contains(x, y)) {
                        mCallBack.touchDeviceView();
                    }
                }
                break;
        }

        return true;
    }

    /**
     * 画右边得圆环
     *
     * @param canvas 画布
     * @param ring   圆环对象
     */
    private void drawRightRing(Canvas canvas, Ring ring) {
        canvas.drawArc(ring.rectF, -mRingAngle / 2, mRingAngle, false, mCirclePaint);
        float cx = (float) (mViewWidth / 2 + Math.cos(Math.PI * (mRingAngle / 2) / 180) * (ring.rectLen));
        float cy = (float) (mViewHeight / 2 - Math.sin(Math.PI * (mRingAngle / 2) / 180) * (ring.rectLen));
        float cy_2 = cy + (float) (2 * Math.sin(Math.PI * (mRingAngle / 2) / 180) * (ring.rectLen));
//        canvas.drawCircle(cx, cy, mRingWidth / 2, mStartCirclePaint);
//        canvas.drawCircle(cx, cy_2, mRingWidth / 2, mStartCirclePaint);

    }

    /**
     * 画左边得圆环
     *
     * @param canvas 画布
     * @param ring   圆环对象
     */
    private void drawLeftRing(Canvas canvas, Ring ring) {
        canvas.drawArc(ring.rectF, 180 - mRingAngle / 2, mRingAngle, false, mCirclePaint);
        float cx = (float) (mViewWidth / 2 - Math.cos(Math.PI * (mRingAngle / 2) / 180) * (ring.rectLen));
        float cy = (float) (mViewHeight / 2 - Math.sin(Math.PI * (mRingAngle / 2) / 180) * (ring.rectLen));
        float cy_2 = (float) (cy + 2 * Math.sin(Math.PI * (mRingAngle / 2) / 180) * (ring.rectLen));
//        canvas.drawCircle(cx, cy, mRingWidth / 2, mStartCirclePaint);
//        canvas.drawCircle(cx, cy_2, mRingWidth / 2, mStartCirclePaint);
    }

    /**
     * 添加设备
     */
    public void addDeviceList(List<WifiBean> list) {
        connCount = 0;
        if (mDeviceList.size() > list.size()) {
            mDeviceList = mDeviceList.subList(0, mDeviceList.size() - list.size());
            invalidate();
        } else if (mDeviceList.size() < list.size()) {
            for (int i = mDeviceList.size(); i < list.size(); i++) {
                Device device = new Device();
                device.rect = getDeviceRect(list.get(i));
                mDeviceList.add(device);
            }
            invalidate();
        }
    }


    private Rect getUnionRect(WifiBean wifiBean) {
        while (connCount < 10 && mDeviceList.size() >= 1) {
            Rect rect = getDeviceRect(wifiBean);
            for (Device device : mDeviceList) {
                if (isCollision(device.rect, rect)) {
                    L.i("TAG:重合");
                    break;
                }
            }
            connCount++;
        }
        return null;
    }

    private boolean isCollision(Rect r1, Rect r2) {
        int x1 = r1.left;
        int y1 = r1.top;
        int w1 = r1.right - r1.left;
        int h1 = r1.bottom - r1.top;

        int x2 = r2.left;
        int y2 = r2.top;
        int w2 = r2.right - r2.left;
        int h2 = r2.bottom - r2.top;

        if (y1 + h1 < y2 /*上*/ || x1 + w1 < x2 /*左*/
                || y2 + h2 < y1 /*下*/ || x2 + w2 < x1 /*右*/) {
            return false;
        }
        return true;
    }

    /**
     * 获取设备得Device得显示得位置
     *
     * @param wifiBean
     * @return
     */
    private Rect getDeviceRect(WifiBean wifiBean) {
        int distance = mWifiBitmap.getWidth() + ((100 - wifiBean.getLevel()) / 100) * mRingWidth / 2;
        Rect rect = new Rect();
        int radius = (int) (Math.random() * 360);
        rect.left = (int) (Math.cos(radius * Math.PI / 180) * distance - mDeviceMarkBitmap.getWidth() / 2);
        rect.right = rect.left + mDeviceMarkBitmap.getWidth();
        rect.top = (int) (Math.sin(radius * Math.PI / 180) * distance - mDeviceMarkBitmap.getWidth() / 2);
        rect.bottom = rect.top + mDeviceMarkBitmap.getHeight();
        return rect;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureView(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mViewWidth, mViewHeight);
        for (Ring ring : mRingList) {
            int rectLen = mWifiBitmap.getWidth() / 2 + mRingDistance * (ring.index + 1);
            ring.rectF.left = mViewWidth / 2 - rectLen;
            ring.rectF.top = mViewHeight / 2 - rectLen;
            ring.rectF.right = mViewWidth / 2 + rectLen;
            ring.rectF.bottom = mViewHeight / 2 + rectLen;
            ring.rectLen = rectLen;
        }
    }

    public void startRingAnim() {
        isLoop = true;
        new Thread() {
            @Override
            public void run() {
                while (isLoop) {
                    if (mIndex == mRingNums) {
                        mIndex = 0;
                    } else {
                        mIndex++;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                super.run();
            }
        }.start();
    }

    public void stopRingAnim() {
        isLoop = false;
    }

    /**
     * 测量view得宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    private void measureView(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mViewWidth = widthSize;
        } else {
            mViewWidth = mWifiBitmap.getWidth() + 2 * mRingNums * (mRingWidth + mRingDistance);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mViewHeight = heightSize;
        } else {
            mViewHeight = mViewWidth;
        }
    }

    class Ring {
        // 圆环得矩形框
        RectF rectF;
        int index;
        int rectLen;

    }

    class Device {
        Rect rect;
    }
}
