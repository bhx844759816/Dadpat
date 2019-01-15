package com.benbaba.dadpat.host.dialog;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.adapter.BluetoothListAdapter;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.bean.drum.BlueToothBody;
import com.benbaba.dadpat.host.bean.drum.DefaultBody;
import com.benbaba.dadpat.host.bean.drum.DrumBean;
import com.benbaba.dadpat.host.callback.OnBlueToothSettingCallBack;
import com.benbaba.dadpat.host.ui.MainActivity;
import com.benbaba.dadpat.host.utils.DeviceUdpUtils;
import com.benbaba.dadpat.host.utils.NetUtils;
import com.benbaba.dadpat.host.utils.SocketManager;
import com.benbaba.dadpat.host.utils.ToastUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothListDialogFragment extends BaseDialogFragment {

    @BindView(R.id.id_dialog_bluetooth_rv)
    RecyclerView mBluetoothRv;
    private MainActivity mMainActivity;
    private BluetoothListAdapter mAdapter;
    private List<BluetoothDevice> mDeviceList;
    private SocketManager mSocketManager;
    private SocketManager.OnSocketReceiveCallBack mSocketCallBack = bean -> {
        String action = bean.getHeader().getAction();
        if ("bluetooth".equals(action)) {
            //发送蓝牙消息接收成功
            String resultCode = ((DefaultBody) bean.getBody()).getResult_code();
            if (resultCode.equals("0")) {
                ToastUtils.showShortToast(mMainActivity, "发送蓝牙消息成功");
            }
        } else if ("getBluetooth".equals(action)) {
            //获取蓝牙信息成功
            BlueToothBody body = (BlueToothBody) bean.getBody();
            if (body.getResult_code().equals("0")) {
                ToastUtils.showShortToast(mMainActivity, "获取蓝牙消息成功");
                mAdapter.setonLineBlueToothName(body.getBluetoothName(), body.getAddress());
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mMainActivity = (MainActivity) context;
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_bluetooth_list;
    }

    @Override
    public void initView(View view) {
        mSocketManager = new SocketManager(mSocketCallBack);
        mSocketManager.startReceiveUdpMsg();
        //获取当前连接得蓝牙
        mSocketManager.sendReceiveUdpMsg(DeviceUdpUtils.getBlueToothJson());
        getDialog().setCanceledOnTouchOutside(false);
        mDeviceList = new ArrayList<>();
        mDeviceList.addAll(mMainActivity.getBlueToothDeviceList());
        mAdapter = new BluetoothListAdapter(mMainActivity, mDeviceList);
        //发送蓝牙地址到鼓
        mAdapter.setListener(device -> {
            if (checkWifi()) {
                ToastUtils.showShortToast(mContext, "发送蓝牙信息到玩具鼓");
                mSocketManager.sendReceiveUdpMsg(DeviceUdpUtils.getSendBlueToothAddressJson(device.getAddress()));
            }
        });
        mBluetoothRv.setLayoutManager(new LinearLayoutManager(mMainActivity, LinearLayoutManager.VERTICAL, false));
        mBluetoothRv.addItemDecoration(new DividerItemDecoration(mMainActivity, LinearLayout.VERTICAL));
        mBluetoothRv.setAdapter(mAdapter);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initData() {
        Observable.interval(2000, TimeUnit.MILLISECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    mDeviceList.clear();
                    mDeviceList.addAll(mMainActivity.getBlueToothDeviceList());
                    mAdapter.notifyDataSetChanged();
                });

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mMainActivity.stopScanBlueToothDevice();
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocketManager != null) {
            mSocketManager.release();
            mSocketManager = null;
        }
    }

    @OnClick(R.id.id_dialog_bluetooth_cancel)
    public void onViewClicked() {
        dismiss();
    }

    public boolean checkWifi() {
        if (NetUtils.isWifiConnected(mContext)) {
            return true;
        } else {
            ToastUtils.showShortToast(mContext, "手机未连接wifi");
            return false;
        }

    }

}
