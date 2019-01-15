package com.benbaba.dadpat.host.adapter;

import android.content.Context;
import android.view.View;


import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseAdapter;
import com.benbaba.dadpat.host.base.BaseViewHolder;
import com.benbaba.dadpat.host.bean.WifiBean;

import java.util.List;

/**
 * Created by Administrator on 2018/1/30.
 */
public class WifiListAdapter extends BaseAdapter<WifiBean> {
    private OnSendWifiListener mListener;

    public WifiListAdapter(Context context, List<WifiBean> data) {
        super(context, data, R.layout.wifi_list_item);
    }

    public void setOnSendWifiListener(OnSendWifiListener listener) {
        this.mListener = listener;
    }

    @Override
    public void convert(BaseViewHolder holder, int pos, final WifiBean wifiBean) {
        holder.setText(R.id.id_wifi_name, wifiBean.getSsid());
        holder.setOnClickListener(R.id.id_wifi_send, v -> mListener.sendWifi(wifiBean.getSsid()));
    }

    public interface OnSendWifiListener {
        void sendWifi(String ssid);
    }
}
