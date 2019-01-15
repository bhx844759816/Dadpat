package com.benbaba.module.device.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.benbaba.module.device.R;
import com.benbaba.module.device.adapter.SearchListAdapter;
import com.benbaba.module.device.db.DeviceInfo;
import com.benbaba.module.device.utils.OnSendWifiListener;
import com.benbaba.module.device.view.ArrowGuideView;
import com.benbaba.module.device.view.RadarView;
import com.benbaba.module.device.view.WifiListDialog;
import com.benbaba.module.device.wifi.DeviceManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 搜索附件设备
 * 1.搜索附近得玩具鼓
 * 2.搜索附近得蓝牙鼓
 */
public class SearchDeviceActivity2 extends AppCompatActivity implements View.OnClickListener, OnSendWifiListener {
    private DeviceManager mManager;
    private List<ScanResult> mWifiList;
    private List<DeviceInfo> mSearchDeviceList;


    private List<DeviceInfo> mDeviceList;
    private RadarView mRadarView;
    private static final String SSID = "dadpat";
    private ArrowGuideView mGuideView;
    private RecyclerView mRecyclerView;
    private SearchListAdapter mAdapter;
    private TextView mSelectTv;
    private int mScreenWidth;
    private CardView mCardView;
    private boolean isShow;//标识现在是否展示设备列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device2);
        mGuideView = findViewById(R.id.id_arrowGuideView);
        mRecyclerView = findViewById(R.id.id_search_device_recyclerView);
        mRadarView = findViewById(R.id.id_radarView);
        mCardView = findViewById(R.id.id_cardView);
        TextView connect = findViewById(R.id.id_connect);
        mSelectTv = findViewById(R.id.id_all_select);
        connect.setOnClickListener(this);
        mSelectTv.setOnClickListener(this);
        mGuideView.setOnClickListener(this);
        mDeviceList = new ArrayList<>();
        mSearchDeviceList = new ArrayList<>();
        mManager = new DeviceManager(this);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mManager.setOnSearchResultListener(new DeviceManager.OnSearchResultListener() {
            @Override
            public void searchResult(List<ScanResult> results) {
                mWifiList = results;
                mSearchDeviceList.clear();
                for (ScanResult scanResult : mWifiList) {
                    String ssid = scanResult.SSID.replace("/", "");
                    if (ssid.equals(SSID)) {
                        DeviceInfo deviceInfo = new DeviceInfo();
                        deviceInfo.setName(scanResult.SSID);
                        deviceInfo.setDId(scanResult.BSSID);
                        deviceInfo.setRssi(scanResult.level);
                        mSearchDeviceList.add(deviceInfo);
                    }
                }
                mRadarView.generateRaindrop(mSearchDeviceList);
                updateDeviceList();
            }
        });
        mAdapter = new
                SearchListAdapter(this, mDeviceList);
        mRecyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new
                DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);


    }

    /**
     * 更新设备列表
     */
    private void updateDeviceList() {
        Iterator<DeviceInfo> iterator = mDeviceList.iterator();
        while (iterator.hasNext()) {
            boolean isExist = false;
            String dId = iterator.next().getDId();
            for (DeviceInfo info : mSearchDeviceList) {
                if (info.getDId().equals(dId)) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                iterator.remove();
            }
        }
        for (DeviceInfo info : mSearchDeviceList) {
            if (!mDeviceList.contains(info)) {
                mDeviceList.add(info);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mManager.startSearchWifi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mManager.stopSearchWifi();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.id_connect) {
            connect();
        } else if (i == R.id.id_arrowGuideView) {
            skip();
        } else if (i == R.id.id_all_select) {
            for (DeviceInfo info : mDeviceList) {
                info.setCheck(!info.isCheck());
            }
            mAdapter.notifyDataSetChanged();

        }
    }

    private void connect() {
        final ArrayList<DeviceInfo> select = new ArrayList<>();
        for (DeviceInfo info : mDeviceList) {
            if (info.isCheck()) {
                select.add(info);
            }
        }
        if (select.isEmpty()) {
            Toast.makeText(this, "请至少选择一个设备进行配置", Toast.LENGTH_SHORT).show();
            return;
        }
        WifiListDialog.showDialog(this).setScanResults(mWifiList);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                if (x < mCardView.getX()) {
                    if (isShow) {
                        skip();
                        return true;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    ObjectAnimator animator;

    private void skip() {
        if (animator != null && animator.isRunning()) {
            return;
        }
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mCardView.getLayoutParams();
        int width = params.width + params.leftMargin + params.rightMargin;
        if (!isShow) {
            mGuideView.stopAnim();
            mGuideView.setVisibility(View.GONE);
            animator = ObjectAnimator.ofFloat(mCardView, View.TRANSLATION_X, 0, -width);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isShow = true;
                    mAdapter.notifyData(mDeviceList);
                    super.onAnimationEnd(animation);
                }
            });
        } else {
            animator = ObjectAnimator.ofFloat(mCardView, View.TRANSLATION_X, -width, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isShow = false;
                    mGuideView.startAnim();
                    mGuideView.setVisibility(View.VISIBLE);
                    super.onAnimationEnd(animation);
                }
            });
        }
        animator.setDuration(1000);
        animator.start();
    }

    @Override
    public void sendWifi(String ssid, String psd) {
        Intent intent = new Intent(this, DeviceSetUpActivity.class);
        intent.putExtra("SendSSID", ssid);
        intent.putExtra("PassWord", psd);
        intent.putExtra("DeviceInfo", (Serializable) mDeviceList);
        startActivity(intent);
    }
}
