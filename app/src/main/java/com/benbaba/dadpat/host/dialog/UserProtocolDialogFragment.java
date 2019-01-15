package com.benbaba.dadpat.host.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 缓存歌曲的Dialog
 */
@SuppressWarnings("checkresult")
public class UserProtocolDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_protocol_content)
    TextView mProtocolContent;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_protocol;
    }

    @Override
    public void initView(View view) {
        try {
            InputStream is = getActivity().getAssets().open("protocol.txt");
            String content = FileUtils.readText(is);
            mProtocolContent.setText(content);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.iv_protocol_confirm)
    public void onViewClicked() {
        dismissAllowingStateLoss();
    }


}
