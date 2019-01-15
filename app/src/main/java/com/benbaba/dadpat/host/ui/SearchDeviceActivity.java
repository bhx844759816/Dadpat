package com.benbaba.dadpat.host.ui;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseActivity;
import com.benbaba.dadpat.host.bean.drum.DefaultBody;
import com.benbaba.dadpat.host.bean.WifiBean;
import com.benbaba.dadpat.host.callback.OnConnectDeviceCallBack;
import com.benbaba.dadpat.host.callback.OnWifiListCallBack;
import com.benbaba.dadpat.host.callback.OnWifiSearchCallBack;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.ui.fragment.ConnectDeviceFragment;
import com.benbaba.dadpat.host.ui.fragment.SearchDeviceFragment;
import com.benbaba.dadpat.host.ui.fragment.WifiListFragment;
import com.benbaba.dadpat.host.utils.DeviceUdpUtils;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.SocketManager;
import com.benbaba.dadpat.host.utils.ToastUtils;
import com.benbaba.dadpat.host.utils.WifiConnect;
import com.benbaba.dadpat.host.utils.WifiSearch;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("checkresult")
public class SearchDeviceActivity extends BaseActivity implements
        OnWifiSearchCallBack, OnWifiListCallBack, OnConnectDeviceCallBack {
    public static final int GPS_SETTING_REQUEST_CODE = 0x01;
    public static final int WIFI_SETTING_REQUEST_CODE = 0x02;
    private Fragment mCurrentFragment;
    private SearchDeviceFragment mSearchDeviceFragment;
    private WifiListFragment mWifiListFragment;
    private ConnectDeviceFragment mConnDeviceFragment;
    private State mCurrentState;
    private WifiSearch mWifiSearch;
    private WifiConnect mWifiConnect;
    private List<WifiBean> mList;
    private SocketManager mSocketManager;
    private Gson mGson;
    private String mSendSSID;
    private String mSendPSD;
    private boolean isAllowGPSPermisssion; // 是否允许了位置权限
    private boolean connectDeviceSuccess;
    private boolean isPause;


    public enum State {
        // 搜索设备，WiFi列表，发送WiFi密码到设备
        SEARCH_DEVICE, WIFI_LIST, SEND_WIFI_TO_DEVICE
    }

    private SocketManager.OnSocketReceiveCallBack mSocketCallBack = bean -> {
        String action = bean.getHeader().getAction();
        String resultCode = ((DefaultBody) bean.getBody()).getResult_code();
        if (action.equals("network") && resultCode.equals("0")) {
            //设备收到设置网络得消息
            L.i("setting wifi success");
        }
    };

//    /**
//     * Socket得Udp接收消息得回调类
//     */
//    private SocketManager.OnSocketReceiveCallBack mSocketCallBack = msg -> {
//        try {
//            L.i("setting wifi");
//            DrumBean drumBean = mGson.fromJson(msg, DrumBean.class);
//            String action = drumBean.getHeader().getAction();
//            String resultCode = drumBean.getBody().getResult_code();
//            if (action.equals("network") && resultCode.equals("0")) {
//                //设备收到设置网络得消息
//                L.i("setting wifi success");
//
//            }
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//    };

    /**
     * wifi扫描得监听
     */
    private WifiSearch.SearchWifiListener mSearchListener = new WifiSearch.SearchWifiListener() {
        @Override
        public void onSearchWifiFailed(WifiSearch.ErrorType errorType) {
            searchDevice(2000);
        }

        @Override
        public void onSearchWifiSuccess(List<WifiBean> results) {
            mList = results;
            searchDevice(2000);
            if (mCurrentFragment instanceof SearchDeviceFragment) {
                ((SearchDeviceFragment) mCurrentFragment).setSearchWifiResults(mList);
            } else if (mCurrentFragment instanceof WifiListFragment) {
                ((WifiListFragment) mCurrentFragment).notifyWifiList(mList);
            }
        }
    };

    /**
     * wifi连接得回调
     */
    private WifiConnect.WifiConnectListener mConnectListener = isConnected -> {
        L.i("isConnected:" + isConnected);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentFragment instanceof SearchDeviceFragment) {
                    DialogFactory.dismissLoadingDialog(SearchDeviceActivity.this);
                    if (isConnected) {
                        ToastUtils.showShortToast(SearchDeviceActivity.this, "连接设备成功");
                        //连接鼓成功了
                        showWifiListFragment();
                    } else {
                        ToastUtils.showShortToast(SearchDeviceActivity.this, "连接设备失败，请检查设备");
                    }
                } else if (mCurrentFragment instanceof ConnectDeviceFragment) {
                    //连接设备成功了
                    if (isConnected) {
                        //发送个消息
                        SearchDeviceActivity.this.finish();
                    } else {
                        ToastUtils.showShortToast(SearchDeviceActivity.this, "请检查wifi密码是否输入正确");
                    }
                } else if (mCurrentFragment instanceof WifiListFragment) {
                    if (isConnected) {
                        showWifiConnectFragment();
                    } else {
                        ToastUtils.showShortToast(SearchDeviceActivity.this, "请检查设备是否开启");
                    }
                }
            }
        });

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device2);
        ButterKnife.bind(this);
        checkGpsPermission();
        mWifiSearch = new WifiSearch(this, mSearchListener);
        mWifiConnect = new WifiConnect(this, mConnectListener);
        mGson = new Gson();
        initSocket();
        initFragment();
    }

    /**
     * 搜索附近得设备
     */
    private void searchDevice(long time) {
        if (isPause) {
            return;
        }
        Observable.timer(time, TimeUnit.MILLISECONDS, Schedulers.io())
                .compose(this.bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(aLong -> mWifiSearch.search());
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        mSearchDeviceFragment = SearchDeviceFragment.newInstance();
        mWifiListFragment = WifiListFragment.newInstance();
        mConnDeviceFragment = ConnectDeviceFragment.newInstance();
        transaction.add(R.id.id_fragment, mSearchDeviceFragment);
        transaction.add(R.id.id_fragment, mWifiListFragment);
        transaction.add(R.id.id_fragment, mConnDeviceFragment);
        transaction.hide(mWifiListFragment);
        transaction.hide(mConnDeviceFragment);
        transaction.commit();
        mCurrentFragment = mSearchDeviceFragment;
        mCurrentState = State.SEARCH_DEVICE;
    }

    /**
     * 初始化Socket
     */
    private void initSocket() {
        mSocketManager = new SocketManager(mSocketCallBack);
        mSocketManager.startReceiveUdpMsg();
    }

    /**
     * 展示SearchView
     */
    private void showSearchFragment() {
        if (mCurrentFragment instanceof SearchDeviceFragment)
            return;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mCurrentFragment != null)
            transaction.hide(mCurrentFragment);
        transaction.show(mSearchDeviceFragment);
        transaction.commit();
        mCurrentFragment = mSearchDeviceFragment;
        mCurrentState = State.SEARCH_DEVICE;
    }

    /**
     * 展示wifi列表得Fragment
     */
    private void showWifiListFragment() {
        if (mCurrentFragment instanceof WifiListFragment)
            return;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mCurrentFragment != null)
            transaction.hide(mCurrentFragment);
        transaction.show(mWifiListFragment);
        transaction.commit();
        mWifiListFragment.setWifiList(mList);
        mCurrentFragment = mWifiListFragment;
        mCurrentState = State.WIFI_LIST;
    }

    /**
     * 展示wifi连接得Fragment
     */
    private void showWifiConnectFragment() {
        if (mCurrentFragment instanceof ConnectDeviceFragment) {
            return;
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mCurrentFragment != null)
            transaction.hide(mCurrentFragment);
        transaction.show(mConnDeviceFragment);
        transaction.commit();
        mCurrentFragment = mConnDeviceFragment;
        mCurrentState = State.SEND_WIFI_TO_DEVICE;
    }

    /**
     * 检查GPS权限
     */
    public void checkGpsPermission() {
        new RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (locManager != null && !locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, GPS_SETTING_REQUEST_CODE); // 设置完成后返回到原来的界面
                            } else {
                                isAllowGPSPermisssion = true;
                            }
                        }
                    } else {

                    }
                });

    }


//    /**
//     * 搜索wifi
//     */
//    public void searchWifi() {
//        new RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.CHANGE_WIFI_STATE).subscribe(aBoolean -> {
//            if (!aBoolean) {
//                toast("请允许相关权限");
//                Intent intent = new Intent(
//                        Settings.ACTION_WIFI_SETTINGS);
//                startActivityForResult(intent, WIFI_SETTING_REQUEST_CODE); // 设置完成后返回到原来的界面
//            } else {
//
//            }
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        if (mWifiSearch != null && isAllowGPSPermisssion) {
            mWifiSearch.registerReceiver();
            searchDevice(0);
        }
        if (mWifiConnect != null && isAllowGPSPermisssion)
            mWifiConnect.registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        if (mWifiSearch != null && isAllowGPSPermisssion)
            mWifiSearch.unRegisterReceiver();
        if (mWifiConnect != null && isAllowGPSPermisssion)
            mWifiConnect.unRegisterReceiver();
    }

    @Override
    protected void onDestroy() {
        if (mWifiSearch != null) {
            String connect = mWifiSearch.getConnectWifiInfo().replace("\"", "");
            if (Constants.DEVICE_WIFI_SSID.equals(connect)) {
                mWifiSearch.disConnectWifi();
            }
            mWifiSearch.destroy();
        }
        if (mSocketManager != null) {
            mSocketManager.release();
        }
        super.onDestroy();
    }

    /**
     * 连接设备
     */
    @Override
    public void connectDevice() {
        String connectWifiSSID = mWifiSearch.getConnectWifiInfo().replace("\"", "");
        if (Constants.DEVICE_WIFI_SSID.equals(connectWifiSSID)) {
            //连接鼓成功了
            showWifiListFragment();
        } else {
            DialogFactory.showLoadingDialog(SearchDeviceActivity.this);
            if (mWifiConnect != null) {
                mWifiConnect.connect(Constants.DEVICE_WIFI_SSID, Constants.DEVICE_WIFI_PASSWORD, WifiConnect.SecurityMode.WPA2);
            }
        }
    }

    @Override
    public void connectWifi(String psd) {
        mSendPSD = psd;
        String connectWifiSSID = mWifiSearch.getConnectWifiInfo().replace("\"", "");
        if (connectWifiSSID.equals(Constants.DEVICE_WIFI_SSID)) {
            if (mSocketManager != null) {
                String msg = DeviceUdpUtils.getWifiSettingJson(mSendSSID, psd);
                L.i("sendMsg:" + msg);
                mSocketManager.sendReceiveUdpMsg(msg);
            }
//            SearchDeviceActivity2.this.finish();
//           if (mWifiConnect != null) {
//                mWifiConnect.connect(mSendSSID, mSendPSD, WifiConnect.SecurityMode.WPA);
//           }
        } else {
            mWifiConnect.connect(Constants.DEVICE_WIFI_SSID, Constants.DEVICE_WIFI_PASSWORD, WifiConnect.SecurityMode.WPA2);
        }
    }

    @Override
    public void wifiSettingHelp() {
        DialogFactory.showWifiSettingPromptDialog(this);
//        if (mSocketManager != null) {
//            String msg = DeviceUdpUtils.getWifiSettingJson("benbb", "123");
//            mSocketManager.sendReceiveUdpMsg(msg);
//        }
    }

    @Override
    public void sendWifiToDevice(String ssid) {
        // 获取当前连接得SSID
        mSendSSID = ssid;
        String connectWifiSSID = mWifiSearch.getConnectWifiInfo().replace("\"", "");
        if (connectWifiSSID.equals(Constants.DEVICE_WIFI_SSID)) {
            showWifiConnectFragment();
        } else {
            mWifiConnect.connect(Constants.DEVICE_WIFI_SSID, Constants.DEVICE_WIFI_PASSWORD, WifiConnect.SecurityMode.WPA);
        }
    }

    @OnClick(R.id.id_wifi_search_back)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        switch (mCurrentState) {
            case WIFI_LIST:
                showSearchFragment();
                break;
            case SEARCH_DEVICE:
                this.finish();
                break;
            case SEND_WIFI_TO_DEVICE:
                showWifiListFragment();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GPS_SETTING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                isAllowGPSPermisssion = true;
                searchDevice(0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
