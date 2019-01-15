package com.benbaba.dadpat.host.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.callback.OnNetWorkRemindCallBack;

import butterknife.BindView;
import butterknife.OnClick;

public class NetWorkDialogFragment extends BaseDialogFragment {
    @BindView(R.id.id_network_content)
    TextView mContent;
    private OnNetWorkRemindCallBack mCallBack;
    private String mFileSize;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity instanceof OnNetWorkRemindCallBack) {
            mCallBack = (OnNetWorkRemindCallBack) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    @Override
    public void initView(View view) {
        setCancelable(false);
        mContent.setText(String.valueOf("当前是移动网络需要下载" + mFileSize + ",是否继续下载"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_network_change_prompt;
    }

    public void setData(String fileSize) {
        mFileSize = fileSize;
    }


    @OnClick({R.id.id_network_confirm, R.id.id_network_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_network_confirm:
                if (mCallBack != null)
                    mCallBack.continueDownLand();
                break;
        }
        dismissAllowingStateLoss();
    }

}
