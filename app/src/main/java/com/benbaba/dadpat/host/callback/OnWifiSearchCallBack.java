package com.benbaba.dadpat.host.callback;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * 扫描wifi得
 */
public interface OnWifiSearchCallBack {

    /**
     * 连接设备
     */
    void connectDevice();

    /**
     * 重置鼓得wifi
     */
    void wifiSettingHelp();
}
