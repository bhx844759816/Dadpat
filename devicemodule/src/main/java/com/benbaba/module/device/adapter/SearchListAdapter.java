package com.benbaba.module.device.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.benbaba.module.device.R;
import com.benbaba.module.device.db.DeviceInfo;

import java.util.List;

/**
 * 搜索列表得适配器
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder> {
    private List<DeviceInfo> mDeviceInfos;
    private LayoutInflater mInflater;

    public SearchListAdapter(Context context, List<DeviceInfo> list) {
        this.mDeviceInfos = list;
        this.mInflater = LayoutInflater.from(context);
    }

    public void notifyData(List<DeviceInfo> list) {
        this.mDeviceInfos = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.adapter_search_list, viewGroup, false);
        return new SearchListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListViewHolder viewHolder, int i) {
        final DeviceInfo info = mDeviceInfos.get(i);
        viewHolder.cb.setChecked(info.isCheck());
        viewHolder.macName.setText(info.getDId());
        viewHolder.name.setText(info.getName());
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                info.setCheck(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDeviceInfos.size();
    }

    class SearchListViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView macName;
        CheckBox cb;

        SearchListViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.id_search_list_cb);
            name = itemView.findViewById(R.id.id_search_list_name);
            macName = itemView.findViewById(R.id.id_search_list_mac);

        }
    }
}
