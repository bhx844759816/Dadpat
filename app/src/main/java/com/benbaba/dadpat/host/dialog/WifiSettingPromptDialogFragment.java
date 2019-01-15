package com.benbaba.dadpat.host.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 网络设置提示帮助dialog
 */
public class WifiSettingPromptDialogFragment extends BaseDialogFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wifi_setting_prompt;
    }


    @OnClick(R.id.tv_wifi_setting_prompt_confirm)
    public void onViewClicked() {
        dismissAllowingStateLoss();
    }
}
