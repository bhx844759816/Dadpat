package com.benbaba.dadpat.host.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.ui.SearchDeviceActivity;
import com.benbaba.dadpat.host.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 设置的Dialog
 */
public class SettingDialogFragment extends BaseDialogFragment {

    @BindView(R.id.id_setting_sound_effect)
    CheckBox mCheckBox;
    @BindView(R.id.id_setting_bj)
    ImageView mContent;
    @BindView(R.id.id_parent)
    ConstraintLayout mParent;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_setting;
    }

    @Override
    public void initData() {
        boolean isPlaySoundEffect = (boolean) SPUtils.get(mContext, "isPlaySoundEffect", true);
        mCheckBox.setChecked(isPlaySoundEffect);
    }

    @Override
    public void initView(View view) {
    }

    @OnClick({R.id.id_setting_selectWifi, R.id.id_setting_finish, R.id.id_setting_sound_effect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_setting_selectWifi:
                mContext.startActivity(new Intent(mContext, SearchDeviceActivity.class));
                break;
            case R.id.id_setting_sound_effect:
                SPUtils.put(mContext, "isPlaySoundEffect", mCheckBox.isChecked());
                break;
            case R.id.id_setting_finish:
                dismissAllowingStateLoss();
                break;
        }
    }

}
