package com.benbaba.dadpat.host.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.callback.OnPromptDialogCallBack;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 提示DialogFragment
 * Created by Administrator on 2018/9/17.
 */
public class PromptDialogFragment extends BaseDialogFragment {
    @BindView(R.id.id_prompt_message)
    TextView mMessage;
    private OnPromptDialogCallBack mCallBack;

    private String mPluginName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity instanceof OnPromptDialogCallBack) {
            mCallBack = (OnPromptDialogCallBack) activity;
        }
    }

    @Override
    public void initView(View view) {
        setCancelable(false);
        mMessage.setText(String.valueOf("是否删除" + mPluginName));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCallBack != null) {
            mCallBack = null;
        }
    }

    /**
     * 设置删除得名字
     *
     * @param pluginName
     */
    public void setPluginName(String pluginName) {
        mPluginName = pluginName;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_prompt;
    }


    @OnClick({R.id.id_prompt_cancel, R.id.id_prompt_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_prompt_cancel:
                if (mCallBack != null)
                    mCallBack.cancelDelete();
                break;
            case R.id.id_prompt_confirm:
                if (mCallBack != null)
                    mCallBack.confirmDelete();
                break;
        }
        dismissAllowingStateLoss();
    }

}
