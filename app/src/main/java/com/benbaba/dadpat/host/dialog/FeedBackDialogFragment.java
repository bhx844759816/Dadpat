package com.benbaba.dadpat.host.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.bean.User;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.http.entry.HttpResult;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * 反馈意见
 */
@SuppressWarnings("checkresult")
public class FeedBackDialogFragment extends BaseDialogFragment {
    @BindView(R.id.et_feedback_content)
    EditText mContent;
    private User mUser;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_feedback;
    }

    public void setUser(User user) {
        mUser = user;
    }

    @OnClick({R.id.iv_feedback_close, R.id.iv_feedback_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_feedback_close:
                dismissAllowingStateLoss();
                break;
            case R.id.iv_feedback_send:
                String content = mContent.getText().toString().trim();
                if(TextUtils.isEmpty(content)){
                    ToastUtils.showShortToast(mContext,"不能发送空得消息");
                    return;
                }
                sendContent(content);
                dismissAllowingStateLoss();
                break;
        }
    }

    /**
     * 发送消息
     */
    private void sendContent(String content) {
        Map<String, String> params = new HashMap<>();
        params.put("userName", mUser.getUserName());
        params.put("userId", mUser.getUserId());
        params.put("content", content);
        HttpManager.getInstance().postFeedBack(params).subscribe();
    }
}
