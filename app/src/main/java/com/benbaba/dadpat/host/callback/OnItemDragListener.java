package com.benbaba.dadpat.host.callback;

import android.view.View;

public interface OnItemDragListener {
    /**
     * 开始拖拽
     */
    void startDrag();

    /**
     *
     */
    void deleteItem(View view, int position);


    void clearView();
}
