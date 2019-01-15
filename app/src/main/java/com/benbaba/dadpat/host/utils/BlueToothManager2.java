package com.benbaba.dadpat.host.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.benbaba.dadpat.host.bean.BleAdvertisedData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 蓝牙工具类
 */
public class BlueToothManager2 {


    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mDeviceList;
    private boolean isScanFinish;
    private Disposable mDisposable;


    public BlueToothManager2(Context context) {
        mDeviceList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager != null) mBluetoothAdapter = manager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(mReceiver, mFilter);

    }

    /**
     * 判断蓝牙是否打开
     *
     * @return
     */
    public boolean isEnabled() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * 开始扫描蓝牙设备
     */
    public void startScan() {
        isScanFinish = true;
        mDisposable = Observable.interval(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (isScanFinish && isEnabled()) {
                        mBluetoothAdapter.startDiscovery();
                        isScanFinish = false;
                    }
                });

    }

    /**
     * 停止扫描附近蓝牙
     */
    public void stopScan() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        mBluetoothAdapter.cancelDiscovery();
    }

    /**
     * 获取设备列表
     *
     * @return
     */
    public List<BluetoothDevice> getDeviceList() {
        return mDeviceList;
    }

    /**
     *
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!TextUtils.isEmpty(device.getName()) &&
                        device.getBondState() != BluetoothDevice.BOND_BONDED && !mDeviceList.contains(device)) {
                    mDeviceList.add(device);
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                isScanFinish = true;
                // 搜索完成
                L.i("搜索蓝牙设备完成");
            }
        }
    };
}

