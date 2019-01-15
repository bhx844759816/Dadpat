package com.benbaba.dadpat.host.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import me.jessyan.autosize.internal.CancelAdapt;
import me.jessyan.autosize.internal.CustomAdapt;


/**
 * 所有activity得基类
 * Created by Administrator on 2017/12/16.
 */
public abstract class BaseActivity extends RxFragmentActivity  implements CustomAdapt {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideBottomUIMenu();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 跳转到指定Activity
     *
     * @param clazz 指定类名的Activity
     */
    protected void startActivity(Class clazz) {
        startActivity(new Intent(BaseActivity.this, clazz));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        AutoSize.autoConvertDensity(this, 640, true);
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 弹出Toast
     *
     * @param msg
     */
    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 360;
    }
}
