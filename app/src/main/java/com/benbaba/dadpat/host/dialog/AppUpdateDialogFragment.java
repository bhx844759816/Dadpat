package com.benbaba.dadpat.host.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.callback.OnAppUpdateDialogCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 提示App更新得Dialog
 */
public class AppUpdateDialogFragment extends BaseDialogFragment {

    @BindView(R.id.id_update_version)
    TextView mVersion;
    @BindView(R.id.id_update_apk_size)
    TextView mApkSize;
    private OnAppUpdateDialogCallBack mCallBack;
    private String appVersionName;
    private String appSize;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAppUpdateDialogCallBack) {
            mCallBack = (OnAppUpdateDialogCallBack) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_update_app;
    }

    @Override
    public void initData() {
        setCancelable(false);
        mVersion.setText(this.appVersionName);
        mApkSize.setText(this.appSize);
    }

    public void setMessage(String appVersionName, String appSize) {
        this.appVersionName = String.valueOf("有新版" + appVersionName + "可以下载");
        this.appSize = String.valueOf("需要下载得大小:" + appSize);
    }

    @OnClick({R.id.id_update_app_cancel, R.id.id_update_app_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_update_app_confirm:
                if (mCallBack != null) {
                    mCallBack.confirmUpdateApp();
                }
                break;
        }
        dismissAllowingStateLoss();
    }
}
