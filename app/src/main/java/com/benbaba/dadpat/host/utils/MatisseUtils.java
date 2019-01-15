package com.benbaba.dadpat.host.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.ui.widget.CropImageView;

public class MatisseUtils {

    /**
     * @param activity
     * @param requestCode
     * @param maxSelectable
     */
    public static void openPhoto(Activity activity, int requestCode, int maxSelectable) {
        Matisse.from(activity)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.benbaba.dadpat.host.fileprovider"))
                .maxSelectable(maxSelectable)
                .isCrop(true)
                .cropStyle(CropImageView.Style.CIRCLE)
                .isCropSaveRectangle(false)
                .thumbnailScale(0.6f)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .imageEngine(new GlideEngine())
                .forResult(requestCode);
    }
}
