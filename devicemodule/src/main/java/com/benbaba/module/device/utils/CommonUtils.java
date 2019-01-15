package com.benbaba.module.device.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
    @SuppressLint("SimpleDateFormat")
    public static String getFormatTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return format.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentime( ) {
        return getFormatTime(System.currentTimeMillis());
    }

}
