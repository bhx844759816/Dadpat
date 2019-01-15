package com.benbaba.module.device.adapter;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benbaba.module.device.R;
import com.benbaba.module.device.db.DeviceGroup;

public class ParentViewHolder extends BaseViewHolder {

    private View mView;

    private ItemClickListener mListener;

    public ParentViewHolder(@NonNull View itemView, ItemClickListener listener) {
        super(itemView);
        this.mView = itemView;
        this.mListener = listener;
    }

    public void bindView(@NonNull final DeviceGroup group) {
        RelativeLayout parent = mView.findViewById(R.id.id_parent_node);
        TextView tv = mView.findViewById(R.id.id_parent_node_content);
        final ImageView imageView = mView.findViewById(R.id.id_parent_node_arrow);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group.setExpand(!group.isExpand());
                if (group.isExpand()) {
                    rotationExpandIcon(imageView, 0, 90);
                } else {
                    rotationExpandIcon(imageView, 90, 0);
                }
                mListener.parentStateChange(group);

            }
        });
        tv.setText(group.getGroupName());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void rotationExpandIcon(final ImageView expand, float from, float to) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);//属性动画
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                expand.setRotation((Float) valueAnimator.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }
}
