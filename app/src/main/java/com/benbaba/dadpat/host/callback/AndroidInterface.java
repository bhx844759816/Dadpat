package com.benbaba.dadpat.host.callback;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.benbaba.dadpat.host.App;
import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.utils.SPUtils;

import java.io.IOException;

/**
 *
 */
public class AndroidInterface {
    private Activity mActivity;
    private MediaPlayer mMediaPlayer;

    public AndroidInterface(Activity activity) {
        this.mActivity = activity;
    }

    @JavascriptInterface
    public void log(String msg) {
        Log.i("TAG", msg);
    }
    /**
     * 获取Token
     *
     * @return
     */
    @JavascriptInterface
    public String getUserToken() {
        return App.token;
    }
    /**
     * 退出App
     *
     * @return
     */
    @JavascriptInterface
    public void exitApp() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }


    /**
     * 设置屏幕方向
     *
     * @param orientation
     */
    @JavascriptInterface
    public void setScreenOrientation(int orientation) {
        switch (orientation) {
            case 0:// 竖屏
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case 1: //横屏
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 2://重力感应
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
        }
    }

    /**
     * 初始化音乐
     *
     * @param url
     */
    @JavascriptInterface
    public void initMusic(String url) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mActivity, Uri.parse(url));
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停音乐
     */
    @JavascriptInterface
    public void pauseMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    /**
     * 开始音乐
     */
    @JavascriptInterface
    public void startMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    /**
     * 释放player
     */
    @JavascriptInterface
    public void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
