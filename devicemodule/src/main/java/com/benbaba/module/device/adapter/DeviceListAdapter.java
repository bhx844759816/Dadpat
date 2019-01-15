package com.benbaba.module.device.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.benbaba.module.device.R;
import com.benbaba.module.device.db.DeviceInfo;
import com.benbaba.module.device.utils.CommonUtils;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private Context mContext;
    private List<DeviceInfo> mList;
    private LayoutInflater mInflater;
    private OnModifyDrumNameListener mListener;

    public DeviceListAdapter(Context context, List<DeviceInfo> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    public void setOnModifyDrumNameListener(OnModifyDrumNameListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.adapter_device_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final DeviceInfo info = mList.get(i);
        if (info.getGId() == 1) {
            viewHolder.typeNameTv.setText("玩具鼓");
        } else if (info.getGId() == 2) {
            viewHolder.typeNameTv.setText("蓝牙鼓");
        }
        viewHolder.timeTv.setText(CommonUtils.getFormatTime(info.getSetUpTime()));
        if (!TextUtils.isEmpty(info.getName()))
            viewHolder.deviceNameTv.setText(String.valueOf("设备名称： " + info.getName()));
        viewHolder.deviceIdTv.setText(String.valueOf("唯一ID：" + info.getDId()));
        if (info.isOnLine()) {
            viewHolder.deviceOnLineTv.setText("在线");
        } else {
            viewHolder.deviceOnLineTv.setText("离线");
        }
        // 设备名称点击
        viewHolder.deviceNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.modifyDrumName(info);
            }
        });

    }

    public interface OnModifyDrumNameListener {
        void modifyDrumName(DeviceInfo info);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeNameTv;
        TextView timeTv;
        TextView deviceNameTv;
        TextView deviceIdTv;
        TextView deviceOnLineTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeNameTv = itemView.findViewById(R.id.id_device_type_name);
            timeTv = itemView.findViewById(R.id.id_device_time);
            deviceNameTv = itemView.findViewById(R.id.id_device_name);
            deviceIdTv = itemView.findViewById(R.id.id_device_id);
            deviceOnLineTv = itemView.findViewById(R.id.id_device_onLine);
        }
    }
}
