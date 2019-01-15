package com.benbaba.dadpat.host.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.benbaba.dadpat.host.utils.DensityUtil;
import com.benbaba.dadpat.host.utils.L;

/**
 * 取消滚动得Manager
 */
public class NoScrollLayoutManager extends GridLayoutManager {
    private Context context;

    public NoScrollLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        this.context = context;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }


}
