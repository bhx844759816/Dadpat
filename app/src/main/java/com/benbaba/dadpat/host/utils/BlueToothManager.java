package com.benbaba.dadpat.host.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.benbaba.dadpat.host.bean.BleAdvertisedData;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙工具类
 */
public class BlueToothManager {


    private BluetoothAdapter mBluetoothAdapter;
    private List<BleAdvertisedData> mDeviceList;

    BluetoothAdapter.LeScanCallback mScanCallback = (device, rssi, scanRecord) -> {
//        if (!mDeviceList.contains(device) && !TextUtils.isEmpty(device.getName())) {
//            mDeviceList.add(device);
//            L.i("device:" + device.toString());
//        }
//        L.i("device:" + device.getName());
////        L.i("device nums:" + mDeviceList.size());

        final BleAdvertisedData data = BleUtil.parseAdertisedData(scanRecord);
        data.setAddress(device.getAddress());
        String deviceName = device.getName();
//        if (deviceName == null) {
//            data.setName(deviceName);
//        }
        if( deviceName == null ){
            deviceName = data.getName();
        }
        L.i("TAG2","deviceName:"+deviceName);
//        if (!mDeviceList.contains(data)) {
//            mDeviceList.add(data);
//        }
    };


    public BlueToothManager(Context context) {
        mDeviceList = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager != null) mBluetoothAdapter = manager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter.startLeScan(mScanCallback);
        } else {
            mBluetoothAdapter.startDiscovery();
        }
    }

    /**
     * 停止扫描附近蓝牙
     */
    public void stopScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter.stopLeScan(mScanCallback);
        } else {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * 获取设备列表
     *
     * @return
     */
    public List<BleAdvertisedData> getDeviceList() {
        return mDeviceList;
    }
}

