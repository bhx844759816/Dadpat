package com.benbaba.dadpat.host.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtils {

    public static Drawable bitmapToDrawable(Bitmap bitmap){
        return new BitmapDrawable(bitmap);
    }
    /**
     * 给Image添加光晕
     *
     * @param context       上下文
     * @param shadowColorId 光晕颜色id
     * @param radius        （外围光晕宽度，也可以根据图片尺寸按照比例来，根据实际需求）
     * @return 加完光晕的图片
     */
    public static Bitmap addHaloToImage(Context context, Bitmap resBitmap, int shadowColorId, float radius) {
        int mBitmapWidth = resBitmap.getWidth();
        int mBitmapHeight = resBitmap.getHeight();
        int shadowRadius = (int) (context.getResources().getDisplayMetrics().density * radius);
        //创建一个比原来图片大2个radius的图片对象
        Bitmap mHaloBitmap = Bitmap.createBitmap(mBitmapWidth + shadowRadius * 2, mBitmapHeight + shadowRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mHaloBitmap);
        //设置抗锯齿
        mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setColor(shadowColorId);
        //外发光
        mPaint.setMaskFilter(new BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.OUTER));
        //从原位图中提取只包含alpha的位图
        Bitmap alphaBitmap = resBitmap.extractAlpha();
        //在画布上（mHaloBitmap）绘制alpha位图
        mCanvas.drawBitmap(alphaBitmap, shadowRadius, shadowRadius, mPaint);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mCanvas.drawBitmap(resBitmap, null, new Rect(shadowRadius + 1, shadowRadius + 1, shadowRadius + mBitmapWidth - 1, shadowRadius + mBitmapHeight - 1), null);
        //回收
        resBitmap.recycle();
        alphaBitmap.recycle();
        return mHaloBitmap;
    }
}
