package com.benbaba.dadpat.host.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.adapter.WifiListAdapter;
import com.benbaba.dadpat.host.bean.WifiBean;
import com.benbaba.dadpat.host.callback.OnWifiListCallBack;
import com.benbaba.dadpat.host.ui.SearchDeviceActivity;
import com.trello.rxlifecycle2.components.RxFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WifiListFragment extends RxFragment {

    @BindView(R.id.id_select_wifi_list)
    RecyclerView mSelectWifiList;
    private Context mContext;
    Unbinder unbinder;
    private WifiListAdapter mWifiListAdapter;
    private List<WifiBean> mWifiList;
    private OnWifiListCallBack mCallBack;

    public static WifiListFragment newInstance() {
        WifiListFragment fragment = new WifiListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnWifiListCallBack) {
            mCallBack = (OnWifiListCallBack) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_select_wifi_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    /**
     *
     */
    private void init() {
        mWifiList = new ArrayList<>();
        mWifiListAdapter = new WifiListAdapter(mContext, mWifiList);
        mSelectWifiList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mSelectWifiList.setAdapter(mWifiListAdapter);
        mWifiListAdapter.setOnSendWifiListener(ssid -> mCallBack.sendWifiToDevice(ssid));
    }
    public void setWifiList(List<WifiBean> list){
        mWifiList.clear();
        mWifiList.addAll(list);
        mWifiListAdapter.notifyDataSetChanged();
    }
    public void notifyWifiList(List<WifiBean> list) {
        if (mWifiListAdapter != null) {
            mWifiList.clear();
            mWifiList.addAll(list);
            mWifiListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
