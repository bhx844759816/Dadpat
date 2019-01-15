package com.benbaba.dadpat.host.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.callback.OnBaseFragmentCallBack;
import com.benbaba.dadpat.host.utils.L;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Dialog得基类
 * Created by Administrator on 2018/3/2.
 */
public abstract class BaseDialogFragment extends DialogFragment implements OnBaseFragmentCallBack {
    private static final int DEFAULT_STYLES = R.style.dialog;
    protected Context mContext;
    private Unbinder unbinder;

    public BaseDialogFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        initData();
    }

    /**
     * 获取布局得ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 界面启动
     */
    @Override
    public void onStart() {
        super.onStart();
//        initWindow();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, DEFAULT_STYLES);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**
     * 初始化window参数
     */
    private void initWindow() {
        if (getDialog() != null) {
            Window dialogWindow = getDialog().getWindow();
            if (dialogWindow != null) {
                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
                dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;
                dialogWindow.setAttributes(lp);
            }
        }

    }


    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }



}
