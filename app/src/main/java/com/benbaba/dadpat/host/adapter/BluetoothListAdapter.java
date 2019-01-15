package com.benbaba.dadpat.host.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseAdapter;
import com.benbaba.dadpat.host.base.BaseViewHolder;
import com.benbaba.dadpat.host.bean.BleAdvertisedData;

import java.util.List;

public class BluetoothListAdapter extends BaseAdapter<BluetoothDevice> {

    private String onLineBlueToothName; //已连接得蓝牙得名称
    private String onLineBlueToothAddress;//已连接得蓝牙得地址
    private OnBlueToothListClickListener mListener;

    public BluetoothListAdapter(Context context, List<BluetoothDevice> data) {
        super(context, data, R.layout.adapter_bluetooth_list);
    }

    @Override
    public void convert(BaseViewHolder holder, int pos, BluetoothDevice device) {
        TextView name = holder.getView(R.id.id_adapter_blue_tooth_name_tv);
        name.setText(device.getName());
        TextView address = holder.getView(R.id.id_adapter_blue_tooth_address_tv);
        address.setText(device.getAddress());
        TextView onLine = holder.getView(R.id.id_adapter_blue_tooth_connection_tv);
        LinearLayout parent = holder.getView(R.id.id_adapter_blue_tooth_parent);
        if (TextUtils.isEmpty(onLineBlueToothName) &&
                device.getName().equals(onLineBlueToothName) &&
                TextUtils.isEmpty(onLineBlueToothAddress) &&
                device.getAddress().equals(onLineBlueToothAddress)) {
            parent.setBackgroundColor(Color.parseColor("#79d3f9"));
            onLine.setText("已连接");
            name.setTextColor(Color.parseColor("#ffffff"));
            address.setTextColor(Color.parseColor("#ffffff"));
            onLine.setTextColor(Color.parseColor("#ffffff"));
        } else {
            parent.setBackgroundColor(Color.parseColor("#ffffff"));

            onLine.setText("未连接");
            name.setTextColor(Color.parseColor("#808080"));
            address.setTextColor(Color.parseColor("#99808080"));
            onLine.setTextColor(Color.parseColor("#99808080"));
        }
        onLine.setOnClickListener(v -> mListener.connectBlueTooth(device));
    }

    public void setListener(OnBlueToothListClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置鼓现在连接得蓝牙得信息
     *
     * @param onLineBlueToothName 连接蓝牙得名称
      * @param onLineBlueToothAddress 连接蓝牙得地址
     */
    public void setonLineBlueToothName(String onLineBlueToothName, String onLineBlueToothAddress) {
        this.onLineBlueToothName = onLineBlueToothName;
        this.onLineBlueToothAddress = onLineBlueToothAddress;
        notifyDataSetChanged();
    }

    public interface OnBlueToothListClickListener {
        void connectBlueTooth(BluetoothDevice device);
    }
}
