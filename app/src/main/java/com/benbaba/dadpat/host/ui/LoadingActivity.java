package com.benbaba.dadpat.host.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;


import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseActivity;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.utils.MatisseUtils;
import com.benbaba.dadpat.host.utils.SPUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * loading页
 */
@SuppressLint("CheckResult")
public class LoadingActivity extends BaseActivity {

    @BindView(R.id.id_loading_gif)
    GifImageView mLoadingGif;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        initPlayer();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        try {
            AssetFileDescriptor fd = getAssets().openFd("main_loading_bgm.mp3");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.setVolume(0.3f, 0.3f);
            mMediaPlayer.start();
            setImageDrawable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Gif动画
     */
    private void setImageDrawable() {
        try {
            GifDrawable drawable = new GifDrawable(getAssets(), "gif/main_loading.gif");
            drawable.addAnimationListener(loopNumber -> requestSdPermission());
            mLoadingGif.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SD卡存储权限
     */
    private void requestSdPermission() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(permission -> {
            if (permission.granted) {
                jumpActivity();
            } else if (!permission.shouldShowRequestPermissionRationale) {
                DialogFactory.showPermissionRefuseDialog(LoadingActivity.this);
            }
        });
    }

    /**
     * 跳转到指定得逻辑页面
     */
    private void jumpActivity() {
        boolean isLogin = (boolean) SPUtils.get(getApplicationContext(), Constants.SP_LOGIN, false);
        if (isLogin) {
            getUserInfo();
        } else {
            startActivity(LoginActivity.class);
            LoadingActivity.this.finish();
        }

    }


    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        HttpManager.getInstance().getUser()
                .subscribe(user -> {
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    intent.putExtra("User", user);
                    startActivity(intent);
                    LoadingActivity.this.finish();
                }, throwable -> {
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
//                    intent.putExtra("User", user);
                    startActivity(intent);
                    LoadingActivity.this.finish();
//                    startActivity(LoginActivity.class);
//                    LoadingActivity.this.finish();
                });

//        HttpManager
//                .getInstance()
//                .getUser()
//                .subscribe(user -> {
//                    Log.i("TAG", "user: " + user.toString());
//                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
//                    intent.putExtra("User", user);
//                    startActivity(intent);
//                    LoadingActivity.this.finish();
//                }, throwable -> {
////                    if (throwable instanceof ApiException) {
////                        ApiException ex = (ApiException) throwable;
////                        if (ApiException.CODE_JWT_EXPIRED.equals(ex.getCode()) ||
////                                ApiException.CODE_JWT_UNAUTHORIZED.equals(ex.getCode())) {
////                            //TOKEN异常
////                            App app = (App) getApplication();
////                            app.setToken("");
////                            //设置未登陆
////                            SPUtils.put(LoadingActivity.this.getApplicationContext(), Constants.SP_LOGIN, true);
////                        } else if (ApiException.CODE_NETWORK_ERROR.equals(ex.getCode())) {
////                            ToastUtils.showShortToast(LoadingActivity.this, ex.getMessage());
////                        }
////                    }
//                    startActivity(LoginActivity.class);
//                    LoadingActivity.this.finish();
//                });
    }
}
