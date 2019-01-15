package com.benbaba.dadpat.host.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.benbaba.dadpat.host.R;

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int topSpace;

    public GridSpaceItemDecoration(Context context) {
        this.topSpace = DensityUtil.dip2px(context, 30);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = topSpace;
    }


}
