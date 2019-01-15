package com.benbaba.module.device.view;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.benbaba.module.device.R;
import com.benbaba.module.device.adapter.WifiListAdapter;

import java.util.Iterator;
import java.util.List;

public class WifiListDialog extends DialogFragment {
    public static final String FRAGMENT_TAG = "wifi_list_dialog";
    private RecyclerView mRecyclerView;
    private List<ScanResult> mScanResults;// 扫描附近得WIFI
    private WifiListAdapter mAdapter;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * 展示Dialog
     *
     * @param activity
     */
    public static WifiListDialog showDialog(FragmentActivity activity) {
        FragmentManager mManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = mManager.beginTransaction();
        WifiListDialog dialog = (WifiListDialog) mManager.findFragmentByTag(FRAGMENT_TAG);
        if (dialog == null) {
            dialog = new WifiListDialog();
        }
        if (!dialog.isVisible() || !dialog.isAdded()) {
            transaction.add(dialog, FRAGMENT_TAG);
        }
        transaction.commit();
        return dialog;
    }

    public static void dismissDialog(FragmentActivity activity) {
        FragmentManager mManager = activity.getSupportFragmentManager();
        WifiListDialog dialog = (WifiListDialog) mManager.findFragmentByTag(FRAGMENT_TAG);
        if (dialog != null && dialog.isAdded() && dialog.isVisible()) {
            dialog.dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_wifi_list, container, false);
        mRecyclerView = view.findViewById(R.id.id_dialog_wifi_list_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        mAdapter = new WifiListAdapter(mContext, mScanResults);
        mAdapter.setOnAdapterClickListener(new WifiListAdapter.OnAdapterClickListener() {
            @Override
            public void clickItem(int pos) {
                dismiss();
                FragmentActivity activity = getActivity();
                if (activity != null)
                    WifiInputPassWordDialog.showDialog(activity)
                            .setWifiName(mScanResults.get(pos).SSID.replace("/", ""));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    /**
     * 设置扫描得
     */
    public void setScanResults(List<ScanResult> scanResults) {
        this.mScanResults = scanResults;
        Iterator<ScanResult> iterator = this.mScanResults.iterator();
        while (iterator.hasNext()) {
            ScanResult result = iterator.next();
            if (result.SSID.replace("/", "").equals("dadpat")) {
                iterator.remove();
            }
        }
    }


}
