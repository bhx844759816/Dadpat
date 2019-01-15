package com.benbaba.module.device.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.benbaba.module.device.bean.WifiBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/11/10.
 */
public class WifiSearch {

    private static final int WIFI_SEARCH_TIMEOUT = 3; //扫描WIFI的超时时间
    private Context mContext;
    private WifiManager mWifiManager;
    private WiFiScanReceiver mWifiReceiver;
    private Lock mLock;
    private Condition mCondition;
    private SearchWifiListener mSearchWifiListener;
    private boolean mIsWifiScanCompleted = false;
    private List<WifiBean> mWifiBeanList;

    public enum ErrorType {
        SEARCH_WIFI_TIMEOUT, //扫描WIFI超时（一直搜不到结果）
        NO_WIFI_FOUND,       //扫描WIFI结束，没有找到任何WIFI信号
    }

    //扫描结果通过该接口返回给Caller
    public interface SearchWifiListener {
        void onSearchWifiFailed(ErrorType errorType);

        void onSearchWifiSuccess(List<WifiBean> results);
    }

    public WifiSearch(Context context, SearchWifiListener listener) {
        mContext = context;
        mSearchWifiListener = listener;
        mLock = new ReentrantLock();
        mCondition = mLock.newCondition();
        mWifiBeanList = new ArrayList<>();
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WiFiScanReceiver();
    }

    /**
     * 获取当前连接得
     */
    public String getConnectWifiInfo() {
        if (mWifiManager != null) {
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            return wifiInfo.getSSID();
        }
        return null;
    }

    /**
     * 搜索局域网wifi
     */
    public void search() {
        ThreadPoolManager.execu(new Runnable() {
            @Override
            public void run() {
                //如果WIFI没有打开，则打开WIFI
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);
                }
                //开始扫描
                mWifiManager.startScan();
                mLock.lock();
                try {
                    mIsWifiScanCompleted = false;
                    mCondition.await(WIFI_SEARCH_TIMEOUT, TimeUnit.SECONDS);
                    if (!mIsWifiScanCompleted && mSearchWifiListener != null) {
                        mSearchWifiListener.onSearchWifiFailed(ErrorType.SEARCH_WIFI_TIMEOUT);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mLock.unlock();
            }
        });
    }

    //系统WIFI扫描结果消息的接收者
    protected class WiFiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //提取扫描结果
            List<ScanResult> scanResults = mWifiManager.getScanResults();
            if (mSearchWifiListener != null) {
                //检测扫描结果
                if (scanResults.isEmpty()) {
                    mSearchWifiListener.onSearchWifiFailed(ErrorType.NO_WIFI_FOUND);
                } else {
                    mWifiBeanList.clear();
                    for (ScanResult scanResult : scanResults) {
//                        int level = WifiManager.calculateSignalLevel(
//                                scanResult.level, 100);
                        String ssid = scanResult.SSID.replace("/", "");
                        WifiBean bean = new WifiBean();
                        bean.setSsid(ssid);
                        bean.setUid(scanResult.BSSID);
                        bean.setLevel(scanResult.level);
                        mWifiBeanList.add(bean);
                    }
                    mSearchWifiListener.onSearchWifiSuccess(mWifiBeanList);
                }
                mLock.lock();
                mIsWifiScanCompleted = true;
                mCondition.signalAll();
                mLock.unlock();
            }

        }
    }

    /**
     * 注册广播接收
     */
    public void registerReceiver() {
        //注册接收WIFI扫描结果的监听类对象
        mContext.registerReceiver(mWifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    /**
     * 取消注册广播接收
     */
    public void unRegisterReceiver() {
        if (mWifiReceiver != null) {
            mContext.unregisterReceiver(mWifiReceiver);
        }
    }

    /**
     * 断开wifi连接
     */
    public void disConnectWifi() {
        if (mWifiManager != null) {
            mWifiManager.disconnect();
        }
    }

}
