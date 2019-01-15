package com.benbaba.module.device.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.benbaba.dadpat.niosocketlib.ReceiveType;
import com.benbaba.module.device.Constants;
import com.benbaba.module.device.bean.DrumBean;
import com.benbaba.module.device.db.DeviceDao;
import com.benbaba.module.device.db.DeviceInfo;
import com.benbaba.module.device.db.MyDataBase;
import com.benbaba.module.device.utils.CommonUtils;
import com.benbaba.module.device.utils.DeviceUdpUtils;
import com.benbaba.module.device.utils.OnSocketCallBack;
import com.benbaba.module.device.utils.SocketWork;
import com.benbaba.module.device.utils.WifiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 操作鼓设备得管理类
 */
public class DeviceManager {
    public static final int GPS_SETTING_REQUEST_CODE = 0x021;
    private static final int WIFI_CONNECT_TIMEOUT = 6; //连接WIFI的超时时间
    private WifiUtils mWifiUtils;
    private Disposable mSearchDisposable;
    private Activity mContext;
    private OnSearchResultListener mListener;
    private Condition mCondition;
    private Lock mLock;
    private boolean isConnectSuccess;// 连接成功
    private boolean isSendMsgSuccess;// 发送消息成功
    int sendNum = 0;
    private String connectSSID;
    private DrumBean mDrumBean;


    /**
     * 接收到消息
     */
    private OnSocketCallBack mCallBack = new OnSocketCallBack() {
        @Override
        public void receiveMsg(DrumBean bean, ReceiveType type) {
            Log.i("TAG", "receiveMsg:" + bean.toString());
            if (type == ReceiveType.RECEIVE_TYPE_UDP) {
                String action = bean.getHeader().getAction();
                if (action.equals("network")) {
                    //设置网络得广播
                    mLock.lock();
                    mDrumBean = bean;
                    isSendMsgSuccess = true;
                    mCondition.signalAll();
                    mLock.unlock();
                }
                Log.i("TAG", "DeviceManager OnSocketCallBack:" + isSendMsgSuccess);
            }
        }
    };

    public DeviceManager(Activity context) {
        mContext = context;
        mLock = new ReentrantLock();
        mCondition = mLock.newCondition();
        mWifiUtils = new WifiUtils(context);
        SocketWork.getInstance().setOnSocketCallBack(mCallBack);
    }

    public void setOnSearchResultListener(OnSearchResultListener listener) {
        this.mListener = listener;
    }

    /**
     * 开始搜索附件wifi
     */
    @SuppressWarnings("CheckResult")
    public void startSearchWifi() {
        new RxPermissions(mContext)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                LocationManager locManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                                if (locManager != null && !locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    Intent intent = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    mContext.startActivityForResult(intent, GPS_SETTING_REQUEST_CODE); // 设置完成后返回到原来的界面
                                } else {
                                    search();
                                }
                            }
                        }
                    }
                });

    }

    /**
     * 搜索局域网wifi
     */
    private void search() {
        if (mSearchDisposable != null && !mSearchDisposable.isDisposed()) {
            return;
        }
        mSearchDisposable = Observable.interval(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        List<ScanResult> list = mWifiUtils.startScan();
                        mListener.searchResult(list);
                    }
                });
    }

    /**
     * 停止搜索附件得wifi
     */
    public void stopSearchWifi() {
        if (mSearchDisposable != null) {
            mSearchDisposable.dispose();
            mSearchDisposable = null;
        }
    }


    /**
     * 配置设备wifi并检查配置信息
     */
    @SuppressWarnings("checkresult")
    public void setUpDevice(final String ssid, final String psd, final OnSendWifiToDeviceCallBack callBack) {
        Observable.create(new ObservableOnSubscribe<DeviceState>() {
            @Override
            public void subscribe(ObservableEmitter<DeviceState> e) throws Exception {
                //第一步 连接Dadpat
                getBrocastIpAddress();
                Log.i("TAG", "注册广播");
                isConnectSuccess = false;
                e.onNext(DeviceState.CONNECT_DEVICE_WIFI);
                WiFiConnectReceiver receiver = new WiFiConnectReceiver();
                mContext.registerReceiver(receiver,
                        new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
                mLock.lock();
                connectSSID = Constants.DEVICE_WIFI_SSID;
                boolean result = mWifiUtils.connectWifi(Constants.DEVICE_WIFI_SSID,
                        Constants.DEVICE_WIFI_PSD, WifiUtils.Data.WIFI_CIPHER_WPA);
                if (result) {
                    mCondition.await(4, TimeUnit.SECONDS);
                }
                if (!isConnectSuccess) {
                    mLock.unlock();
                    e.onNext(DeviceState.CONNECT_DEVICE_WIFI_ERROR);
                    e.onComplete();
                    mContext.unregisterReceiver(receiver);
                    return;
                }
                isSendMsgSuccess = false;
                sendNum = 0;
                e.onNext(DeviceState.CONNECT_DEVICE_WIFI_SUCCESS);
                Log.i("TAG", "Observable Complete");
                int num = 0;
                // 发送wifi信息到设备
                e.onNext(DeviceState.SEND_WIFI_INFO);
                // 发送wifi信息到鼓
                sendSocketMsg(ssid, psd);
                if (!isSendMsgSuccess) {
                    //发送设置wifi信息得udp失败
                    e.onNext(DeviceState.SEND_WIFI_INFO_ERROR);
                    mLock.unlock();
                    e.onComplete();
                    mContext.unregisterReceiver(receiver);
                    return;
                }
                //将设备信息插入到本地数据库
                DeviceDao deviceDao = MyDataBase.init(mContext).getDeviceDao();
                DeviceInfo info = deviceDao.queryByDId(mDrumBean.getHeader().getDev_id());
                if (info == null) {
                    info = new DeviceInfo();
                    info.setGId(1);
                    info.setDId(mDrumBean.getHeader().getDev_id());
                    info.setBindWifiName(ssid);
                    info.setSetUpTime(System.currentTimeMillis());
                    deviceDao.insert(info);
                    Log.i("TAG", "插入设备信息到本地数据库");
                } else {
                    Log.i("TAG", "更新备信息到本地数据库");
                    info.setSetUpTime(System.currentTimeMillis());
                    info.setBindWifiName(ssid);
                    deviceDao.update(info);
                }
                mContext.unregisterReceiver(receiver);
                e.onNext(DeviceState.SEND_WIFI_INFO_SUCCESS);
                e.onComplete();
//                //第三步 获取设备是否连接成功(断开设备得wifi 连接刚刚输入得wifi信息 然后发送广播等待接收消息 轮询得发送接收)
//                isConnectSuccess = false;
//                connectSSID = ssid;
//                result = mWifiUtils.connectWifi(ssid, psd, WifiUtils.Data.WIFI_CIPHER_WPA);
//                if (result) {
//                    Log.i("TAG", "connectWifi ssid:" + ssid);
//                    mCondition.await(6, TimeUnit.SECONDS);
//                }
//                if (!isConnectSuccess) {
//                    mLock.unlock();
//                    e.onComplete();
//                    return;
//                }
//                num = 0;
//                isSendMsgSuccess = false;
//                while (num < 4) {
//                    Log.i("TAG", "sendUdpMsg Electric:" + num);
//                    SocketWork.getInstance().sendUdpMsg(DeviceUdpUtils.getDeviceElectric(), getBrocastIpAddress());
//                    mCondition.await(1, TimeUnit.SECONDS);
//                    if (isSendMsgSuccess) {
//                        break;
//                    }
//                    num++;
//                }
//                if (!isSendMsgSuccess) {
//                    //发送设置wifi信息得udp失败
//                    mLock.unlock();
//                    e.onComplete();
//                    return;
//                }
//                //将设备信息插入到本地数据库
//                DeviceInfo info = new DeviceInfo();
//                info.setGId(1);
//                info.setDId(mDrumBean.getHeader().getDev_id());
//                info.setBindWifiName(ssid);
//                Log.i("TAG", "插入设备信息到本地数据库");
//                MyDataBase.init(mContext).getDeviceDao().insert(info);
//                // 将配置信息存储到本地
//                Log.i("TAG", "解除接收广播");
//                //解除接收广播
//                mContext.unregisterReceiver(receiver);
//                e.onComplete();
            }
        })
//                .delay(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DeviceState>() {
                    @Override
                    public void accept(DeviceState deviceState) throws Exception {
                        if (callBack != null) {
                            callBack.result(deviceState);
                        }
                    }
                });
    }


    /**
     * 发送socket消息并阻塞
     *
     * @param ssid
     * @param psd
     */
    private void sendSocketMsg(String ssid, String psd) {
        SocketWork.getInstance().sendUdpMsg(DeviceUdpUtils.getWifiSettingJson(ssid, psd),
                "255.255.255.255");
        Log.i("TAG2", "time:" + CommonUtils.getCurrentime());
        sendNum++;
        try {
            mCondition.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!isSendMsgSuccess && sendNum < 5) {
            sendSocketMsg(ssid, psd);
        }
    }

    private String getBrocastIpAddress() {
        String ip = mWifiUtils.getLocalIpAddress();
        Log.i("TAG", "getBrocastIpAddress:" + ip);
        String ip_ = ip.substring(0, ip.lastIndexOf("."));
//        return String.valueOf("192.168.43" + "." + "255");
        return String.valueOf(ip_ + "." + "255");
    }


    /**
     * 搜索结果得回调接口
     */
    public interface OnSearchResultListener {
        void searchResult(List<ScanResult> results);
    }

    /**
     * 发送wifi到设备得回调
     */
    public interface OnSendWifiToDeviceCallBack {
        void result(DeviceState state);
    }

    /**
     * wifi连接的广播
     */
    private class WiFiConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                return;
            }
            mLock.lock();
            WifiInfo info = mWifiUtils.getConnectionInfo();
            if (info != null) {
                String info_ssid = info.getSSID().replace("\"", "");
                if (info_ssid.equals(connectSSID) &&
                        info.getSupplicantState() == SupplicantState.COMPLETED) {
                    if (!isConnectSuccess) {
                        mCondition.signalAll();
                    }
                    isConnectSuccess = true;
                }
            }
//            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//            if (info.getNetworkId() == mNetworkID &&
//                    networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
//                mIsConnected = true;
//                mCondition.signalAll();
//            }
            mLock.unlock();
        }
    }


    /**
     *
     */
    public enum DeviceState {
        CONNECT_DEVICE_WIFI, CONNECT_DEVICE_WIFI_SUCCESS, CONNECT_DEVICE_WIFI_ERROR,// 连接设备wifi 连接成功 连接失败
        SEND_WIFI_INFO, SEND_WIFI_INFO_SUCCESS, SEND_WIFI_INFO_ERROR// 发送设备wifi 发送设备成功 发送设备失败

    }

}
