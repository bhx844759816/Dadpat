package com.benbaba.module.device.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benbaba.module.device.R;
import com.benbaba.module.device.db.DeviceInfo;

public class ChildViewHolder extends BaseViewHolder {

    private View mView;

    public ChildViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void bindView(@NonNull DeviceInfo info) {
        TextView tv = mView.findViewById(R.id.id_child_node_name);
        RelativeLayout parent = mView.findViewById(R.id.id_child_node);
        if (!TextUtils.isEmpty(info.getName())) {
            tv.setText(info.getName());
        }
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
