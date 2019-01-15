package com.benbaba.dadpat.host.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * wifi连接的工具类
 * Created by Administrator on 2017/11/10.
 */
public class WifiConnect {

    private static final int WIFI_CONNECT_TIMEOUT = 6; //连接WIFI的超时时间

    private Context mContext;
    private WifiManager mWifiManager;
    private Lock mLock;
    private Condition mCondition;
    private WiFiConnectReceiver mWifiConnectReceiver;
    private WifiConnectListener mWifiConnectListener;
    private boolean mIsConnected = false;
    private int mNetworkID = -1;


    //网络加密模式
    public enum SecurityMode {
        OPEN, WEP, WPA, WPA2
    }

    //通知连接结果的监听接口
    public interface WifiConnectListener {
        void OnWifiConnectCompleted(boolean isConnected);
    }

    public WifiConnect(Context context, WifiConnectListener listener) {
        mContext = context;
        mLock = new ReentrantLock();
        mCondition = mLock.newCondition();
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiConnectReceiver = new WiFiConnectReceiver();
        mWifiConnectListener = listener;
    }

    public void registerReceiver() {
        //注册连接结果监听对象
        mContext.registerReceiver(mWifiConnectReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
    }

    public void unRegisterReceiver() {
        if (mWifiConnectReceiver != null)
            mContext.unregisterReceiver(mWifiConnectReceiver);
    }

    public void connect(final String ssid, final String password, final SecurityMode mode) {
        new Thread(() -> {
            //如果WIFI没有打开，则打开WIFI
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
            }
            //连接指定SSID
            if (!onConnect(ssid, password, mode)) {
                mWifiConnectListener.OnWifiConnectCompleted(false);
            } else {
                mWifiConnectListener.OnWifiConnectCompleted(true);
            }
        }).start();
    }

    /**
     * 连接wifi
     *
     * @param ssid
     * @param password
     * @param mode
     * @return
     */
    private boolean onConnect(String ssid, String password, SecurityMode mode) {
        //添加网络配置
        mLock.lock();
        mIsConnected = false;
        removeWifi(ssid);
        if (removeWifi(ssid)) {
            L.i("TAG2", "removeWifi true");
            mNetworkID = mWifiManager.addNetwork(createWifiConfig(ssid, password, mode));
        } else {
            L.i("TAG2", "removeWifi false");
            WifiConfiguration config = isExist(ssid);
            if (null != config) {
                L.i("TAG2", "isExist true");
                mNetworkID = config.networkId;
            } else {
                L.i("TAG2", "isExist false");
                mNetworkID = mWifiManager.addNetwork(createWifiConfig(ssid, password, mode));
            }
        }
        L.i("mNetWorkID:" + mNetworkID);
        //连接该网络
        boolean result = mWifiManager.enableNetwork(mNetworkID, true);
        L.i("result:" + result);
        if (!result) {
            mLock.unlock();
            return false;
        }
        try {
            //等待连接结果
            mCondition.await(WIFI_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mLock.unlock();
        return mIsConnected;
    }

    /**
     * config里存在； 在mWifiManager移除；
     */
    private boolean removeWifi(String ssid) {
        WifiConfiguration config = isExist(ssid);
        if (config != null) {
            return mWifiManager.removeNetwork(config.networkId);
        } else {
            return false;
        }
    }

    /**
     * 配置网络
     *
     * @param ssid
     * @param password
     * @param mode
     * @return
     */
    private WifiConfiguration createWifiConfig(String ssid, String password, SecurityMode mode) {
        //初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
//        //如果有相同配置的，就先删除
//        WifiConfiguration tempConfig = isExist(ssid);
//        if (tempConfig != null) {
//            mWifiManager.removeNetwork(tempConfig.networkId);
//        }
        //指定对应的SSID
        config.SSID = "\"" + ssid + "\"";
        //不需要密码的场景
        if (mode == SecurityMode.OPEN) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else if (mode == SecurityMode.WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        } else if (mode == SecurityMode.WPA) {
            config.preSharedKey = ("\"" + password + "\"");
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else if (mode == SecurityMode.WPA2) {
            config.preSharedKey = ("\"" + password + "\"");
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    /**
     * 是否存在SSID
     *
     * @param ssid
     * @return
     */
    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }

    /**
     * 断开连接wifi
     */
    public void disconnectWifi() {
        if (mWifiManager != null)
            mWifiManager.disconnect();
    }

    public String getWifiSSID() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null)
            return wifiInfo.getSSID().replace("\"", "");
        else
            return null;
    }

    //监听系统的WIFI连接消息
    protected class WiFiConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                return;
            }
            mLock.lock();
            WifiInfo info = mWifiManager.getConnectionInfo();
            L.i("wifi:"+info.getSSID());
//            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            // TODO 需要测试下这个连接广播是不是可以达到连接成功在回调
            if (info != null && info.getNetworkId() == mNetworkID &&
                    info.getSupplicantState() == SupplicantState.COMPLETED) {
                mIsConnected = true;
                mCondition.signalAll();
            }
//            if (info.getNetworkId() == mNetworkID &&
//                    networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
//                mIsConnected = true;
//                mCondition.signalAll();
//            }
            mLock.unlock();
        }
    }


}
