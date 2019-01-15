package com.benbaba.module.device.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.benbaba.dadpat.niosocketlib.ReceiveType;
import com.benbaba.module.device.R;
import com.benbaba.module.device.adapter.DeviceListAdapter;
import com.benbaba.module.device.adapter.DividerItemDecoration;
import com.benbaba.module.device.bean.DrumBean;
import com.benbaba.module.device.db.DeviceInfo;
import com.benbaba.module.device.db.MyDataBase;
import com.benbaba.module.device.utils.DeviceUdpUtils;
import com.benbaba.module.device.utils.OnSocketCallBack;
import com.benbaba.module.device.utils.SocketWork;
import com.benbaba.module.device.view.ModifyNameDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("CheckResult")
public class DeviceListActivity2 extends AppCompatActivity implements View.OnClickListener, ModifyNameDialog.OnModifyDialogListener {

    private RecyclerView mRecyclerView;
    private DeviceListAdapter mAdapter;
    private List<DeviceInfo> mList;
    private DeviceInfo mDeviceInfo;

    private OnSocketCallBack mCallBack = new OnSocketCallBack() {
        @Override
        public void receiveMsg(DrumBean bean, ReceiveType type) {
            if (bean.getHeader().getAction().equals("get_voltage")) {
                String devId = bean.getHeader().getDev_id();
                setDeviceOnLine(devId);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 设置设备在线
     *
     * @param devId
     */
    private void setDeviceOnLine(String devId) {
        for (DeviceInfo info : mList) {
            if (info.getDId().equals(devId)) {
                info.setOnLine(true);
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        mRecyclerView = findViewById(R.id.id_recyclerView);
        SocketWork.getInstance().setOnSocketCallBack(mCallBack);
        findViewById(R.id.id_add_toy_drum).setOnClickListener(this);
        findViewById(R.id.id_add_blue_tooth_drum).setOnClickListener(this);
        mList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new DeviceListAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnModifyDrumNameListener(new DeviceListAdapter.OnModifyDrumNameListener() {
            @Override
            public void modifyDrumName(DeviceInfo info) {
                mDeviceInfo = info;
                ModifyNameDialog.showDialog(DeviceListActivity2.this);
            }
        });
        MyDataBase.init(this).getDeviceDao()
                .queryAll()
                .doOnNext(new Consumer<List<DeviceInfo>>() {
                    @Override
                    public void accept(List<DeviceInfo> list) throws Exception {
                        //循环发送消息获取玩具鼓是否在线
                        for (DeviceInfo info : list) {
                            String msg = DeviceUdpUtils.getDeviceElectric(info.getDId());
                            Log.i("TAG", "doOnNext send msg:" + msg);
                            SocketWork.getInstance().sendUdpMsg(msg, "255.255.255.255");
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DeviceInfo>>() {
                    @Override
                    public void accept(List<DeviceInfo> list) throws Exception {
                        mList.clear();
                        mList.addAll(list);
                        mAdapter.notifyDataSetChanged();
                    }
                });

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.id_add_toy_drum || i == R.id.id_add_blue_tooth_drum) {
            Intent intent = new Intent(this, SearchDeviceActivity2.class);
            startActivity(intent);

        }
    }

    @Override
    public void sendDeviceName(final String name) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                String msg = DeviceUdpUtils.getDrumBroadCastJson(name, mDeviceInfo.getDId());
                Log.i("TAG", "msg:" + msg);
                mDeviceInfo.setName(name);
                SocketWork.getInstance().sendUdpMsg(msg, "255.255.255.255");
//                MyDataBase.init(DeviceListActivity2.this).getDeviceDao().update(mDeviceInfo);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();

    }
}
