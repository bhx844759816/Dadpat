package com.benbaba.module.device.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benbaba.module.device.R;

import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.MyHolder> {
    private List<ScanResult> mResults;
    private LayoutInflater mInflater;
    private OnAdapterClickListener mListener;

    public WifiListAdapter(Context context, List<ScanResult> list) {
        mInflater = LayoutInflater.from(context);
        this.mResults = list;
    }

    public void notifyData(List<ScanResult> list) {
        this.mResults = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.adapter_wifi_item, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) {
        myHolder.name.setText(mResults.get(i).SSID.replace("/", ""));
        int level = getLevel(mResults.get(i).level);
        Log.i("TAG", "SSID:" + myHolder.name.getText().toString() + ",level:" + level);
        switch (level) {
            case 4:
                myHolder.level.setBackgroundResource(R.drawable.wifi_level_4);
                break;
            case 3:
                myHolder.level.setBackgroundResource(R.drawable.wifi_level_3);
                break;
            case 2:
                myHolder.level.setBackgroundResource(R.drawable.wifi_level_2);
                break;
            case 1:
                myHolder.level.setBackgroundResource(R.drawable.wifi_level_1);
                break;
        }
        myHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickItem(i);
                }
            }
        });
    }

    /**
     * 设置适配器点击事件
     *
     * @param listener
     */
    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mListener = listener;
    }

    /**
     * 获取wifi得信号强度
     *
     * @param rssi
     * @return
     */
    private int getLevel(int rssi) {
        return WifiManager.calculateSignalLevel(rssi, 4);
    }

    @Override
    public int getItemCount() {
        if (mResults == null) {
            return 0;
        } else {
            return mResults.size();
        }
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView level;
        RelativeLayout parent;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.id_wifi_item_name);
            level = itemView.findViewById(R.id.id_wifi_item_level);
            parent = itemView.findViewById(R.id.id_wifi_item_parent);
        }
    }

    public interface OnAdapterClickListener {
        void clickItem(int pos);
    }
}
