package com.benbaba.dadpat.host.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.callback.OnBlueToothSettingCallBack;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BlueToothSettingDialogFragment extends BaseDialogFragment {

    private OnBlueToothSettingCallBack mCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBlueToothSettingCallBack) {
            mCallBack = (OnBlueToothSettingCallBack) context;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_bluetooth_setting;
    }

    @Override
    public void initView(View view) {
        getDialog().setCanceledOnTouchOutside(false);
    }

    @OnClick(R.id.id_dialog_bluetooth_setting)
    public void onViewClicked() {
        mCallBack.confirmBlueToothSetting();
        dismiss();
    }
}
