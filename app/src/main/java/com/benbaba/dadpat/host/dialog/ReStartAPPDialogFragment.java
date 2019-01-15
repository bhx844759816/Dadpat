package com.benbaba.dadpat.host.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.callback.OnReStartAppDialogCallBack;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 重新启动App
 * Created by Administrator on 2018/9/23.
 */
public class ReStartAPPDialogFragment extends BaseDialogFragment {
    private OnReStartAppDialogCallBack mCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReStartAppDialogCallBack) {
            mCallBack = (OnReStartAppDialogCallBack) context;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_restart_app;
    }


    @OnClick({R.id.id_restart_app_cancel, R.id.id_restart_app_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_restart_app_confirm:
                if (mCallBack != null) {
                    mCallBack.confirmRestartApp();
                }
                break;
            case R.id.id_restart_app_cancel:
                if (mCallBack != null) {
                    mCallBack.cancelRestartApp();
                }
                break;
        }
        dismissAllowingStateLoss();
    }
}
