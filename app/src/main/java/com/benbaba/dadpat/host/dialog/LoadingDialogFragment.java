package com.benbaba.dadpat.host.dialog;


import android.text.TextUtils;
import android.view.View;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;

import java.io.IOException;

import butterknife.BindView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2018/3/13.
 */
public class LoadingDialogFragment extends BaseDialogFragment {
    @BindView(R.id.id_loading)
    GifImageView mGifImg;
    private String mLoadingGifName = "gif/loading_dialog.gif";

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_loading;
    }

    @Override
    public void initView(View view) {
        setCancelable(false);
    }

    @Override
    public void initData() {
        super.initData();
        try {
            GifDrawable mGifDrawable = new GifDrawable(getActivity().getAssets(), mLoadingGifName);
            mGifImg.setImageDrawable(mGifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLoadingGifName(String loadingGifName) {
        if (!TextUtils.isEmpty(loadingGifName)) {
            this.mLoadingGifName = loadingGifName;
        }
    }
}
