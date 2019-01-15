package com.benbaba.module.device.ui;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benbaba.dadpat.niosocketlib.ReceiveType;
import com.benbaba.module.device.R;
import com.benbaba.module.device.db.DeviceInfo;
import com.benbaba.module.device.wifi.DeviceManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DeviceSetUpActivity extends AppCompatActivity {

    private static final int CONNECT_DEVICE_RESULT = 0x01; //连接设备wifi得结果
    private static final int SEND_WIFI_RESULT = 0x02; // 发送wifi密码到设备


    private LinearLayout mParent;
    private TextView mDeviceName;
    private TextView mDeviceMAC;
    private ArrayList<DeviceInfo> mDeviceList;
    private DeviceManager mDeviceManager;
    private int index;
    private int mSetUpIndex;
    private int mSteps;
    private TextView mTv;
    private ImageView mIv;
    private String mPassWord; // 需要发送得WIFI密码
    private String mSendSSID; // 需要发送得WIFI密码
    // Handler
    private MyHandler mHandler = new MyHandler(DeviceSetUpActivity.this);

    private class MyHandler extends Handler {
        private WeakReference<Activity> mWeakReference;

        MyHandler(Activity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference.get() == null) {
                return;
            }
            switch (msg.what) {
                case CONNECT_DEVICE_RESULT:
                    mDeviceManager.setUpDevice(mSendSSID, mPassWord, mCallBack);
                    break;
                case SEND_WIFI_RESULT:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private DeviceManager.OnSendWifiToDeviceCallBack mCallBack = new DeviceManager.OnSendWifiToDeviceCallBack() {
        @Override
        public void result(DeviceManager.DeviceState state) {
            switch (state) {
                case SEND_WIFI_INFO:
                    Log.i("TAG2", "SEND_WIFI_INFO");
                    addView("2.   发送wifi信息到设备...");
                    break;
                case SEND_WIFI_INFO_ERROR:
                case CONNECT_DEVICE_WIFI_ERROR:
                    Log.i("TAG2", "error");
                    mIv.setImageResource(R.drawable.error);
                    break;
                case SEND_WIFI_INFO_SUCCESS:
                    mSetUpIndex++;
                    mIv.setImageResource(R.drawable.correct);
                    if (mSetUpIndex < mDeviceList.size()) {
                        mHandler.sendEmptyMessageDelayed(CONNECT_DEVICE_RESULT,3 * 1000);
                    } else {
                        Toast.makeText(DeviceSetUpActivity.this, "配置完成", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CONNECT_DEVICE_WIFI_SUCCESS:
                    Log.i("TAG2", "success");
                    mIv.setImageResource(R.drawable.correct);
                    break;
                case CONNECT_DEVICE_WIFI:
                    Log.i("TAG2", "CONNECT_DEVICE_WIFI");
                    addView("1.   开始连接设备wifi...");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_set_up);
        mParent = findViewById(R.id.id_device_set_up_ll);
        mDeviceName = findViewById(R.id.id_device_set_up_name);
        mDeviceMAC = findViewById(R.id.id_device_set_up_mac);
        mDeviceManager = new DeviceManager(this);
        mDeviceList = (ArrayList<DeviceInfo>) getIntent().getSerializableExtra("DeviceInfo");
        mSendSSID = getIntent().getStringExtra("SendSSID");
        mPassWord = getIntent().getStringExtra("PassWord");
        Log.i("TAG", "mSendSSID:" + mSendSSID);
        Log.i("TAG", "mPassWord:" + mPassWord);
        Log.i("TAG", "mDeviceList:" + mDeviceList.toString());
        mDeviceManager.setUpDevice(mSendSSID, mPassWord, mCallBack);

    }

    private void addView(String msg) {
        LinearLayout child = new LinearLayout(this);
        child.setPadding(20, 20, 20, 20);
        child.setOrientation(LinearLayout.HORIZONTAL);
        mTv = new TextView(this);
        mTv.setText(msg);
        mIv = new ImageView(this);
        child.addView(mTv);
        child.addView(mIv);
        mParent.addView(child);
        mSteps++;
    }

}
