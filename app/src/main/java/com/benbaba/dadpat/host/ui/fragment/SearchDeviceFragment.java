package com.benbaba.dadpat.host.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.bean.WifiBean;
import com.benbaba.dadpat.host.callback.OnWifiSearchCallBack;
import com.benbaba.dadpat.host.ui.SearchDeviceActivity;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.view.SearchWifiView;
import com.trello.rxlifecycle2.components.RxFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 搜索附件得wifi
 */
public class SearchDeviceFragment extends RxFragment {
    @BindView(R.id.id_search_wifi_view)
    SearchWifiView mSearchWifiView;
    private Context mContext;
    Unbinder unbinder;
    private OnWifiSearchCallBack mWifiSearchCallBack;
    private List<WifiBean> mDeviceList;


    public static SearchDeviceFragment newInstance() {
        SearchDeviceFragment fragment = new SearchDeviceFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof SearchDeviceActivity) {
            mWifiSearchCallBack = (SearchDeviceActivity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mWifiSearchCallBack = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        L.i("SearchDeviceFragment  onResume");
        if (mSearchWifiView != null)
            mSearchWifiView.startRingAnim();
    }

    @Override
    public void onPause() {
        super.onPause();
        L.i("SearchDeviceFragment   onPause");
        if (mSearchWifiView != null)
            mSearchWifiView.stopRingAnim();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_select_wifi_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mDeviceList = new ArrayList<>();
        mSearchWifiView.setWifiSearchCallBack(() -> mWifiSearchCallBack.connectDevice());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.id_wifi_help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_wifi_help:
                if (mWifiSearchCallBack != null) {
                    mWifiSearchCallBack.wifiSettingHelp();
                }
                break;
        }
    }

    /**
     * 设置搜索wifi得列表
     *
     * @param scanResults
     */
    public void setSearchWifiResults(List<WifiBean> scanResults) {
        mDeviceList.clear();
        L.i("搜索到设备完成");
        for (WifiBean wifiBean : scanResults) {
            if (wifiBean.getSsid().equals(Constants.DEVICE_WIFI_SSID)) {
                L.i("搜索到设备：" + Constants.DEVICE_WIFI_SSID);
                mDeviceList.add(wifiBean);
            }
        }
        mSearchWifiView.addDeviceList(mDeviceList);
    }

}
