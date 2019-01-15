package com.benbaba.dadpat.host.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.bean.PluginBean;
import com.benbaba.dadpat.host.callback.OnItemDragListener;
import com.benbaba.dadpat.host.ui.TestActivity;
import com.benbaba.dadpat.host.utils.L;

import java.util.Collections;
import java.util.List;

public class DragItemCallBack extends ItemTouchHelper.Callback {
    private RecyclerView.Adapter mAdapter;
    private List<PluginBean> mList;
    private ImageView mTagView;
    private boolean isUp;
    private Rect mMoveRect;
    private OnItemDragListener mDragListener;
    private boolean isDragDelete;

    public DragItemCallBack(RecyclerView.Adapter adapter, List<PluginBean> list, ImageView tagView) {
        mAdapter = adapter;
        this.mList = list;
        this.mTagView = tagView;
        mMoveRect = new Rect();
    }


    /**
     * 设置拖拽回调
     *
     * @param listener
     */
    public void setOnItemDragListener(OnItemDragListener listener) {
        mDragListener = listener;
    }

    /**
     * 重置
     */
    public void resetData() {
        isUp = false;
        isDragDelete = false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = 0;//0则不响应事件
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();//得到item原来的position
        int toPosition = target.getAdapterPosition();//得到目标position
        PluginBean fromBean = mList.get(fromPosition);
        PluginBean toBean = mList.get(toPosition);
//        if (!fromBean.isInstall() || !toBean.isInstall()) {
//            return false;
//        }
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * 设置是否支持长按拖拽
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
//        L.i("canDropOver");
//        int pos = target.getLayoutPosition();
//        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        int count = recyclerView.getAdapter().getItemCount();
//        if (manager instanceof GridLayoutManager) {
//            int spanCount = ((GridLayoutManager) manager).getSpanCount();
//            int num = count % spanCount;
//            if (num == 0) {
//                return pos < (count - spanCount);
//            } else {
//                return pos < (count - num);
//            }
//        }
        return true;
    }

    /**
     * 自定义拖动与滑动交互
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        mMoveRect.left = (int) viewHolder.itemView.getX() + recyclerView.getLeft();
        mMoveRect.right = mMoveRect.left + viewHolder.itemView.getWidth();
        mMoveRect.top = (int) (viewHolder.itemView.getY() + recyclerView.getTop());
        mMoveRect.bottom = mMoveRect.top + viewHolder.itemView.getHeight();
        if (mMoveRect.contains((int) mTagView.getX(), (int) mTagView.getY())) {
            L.i("移动到垃圾桶");
            if (isUp) {//在删除处放手，则删除item
                viewHolder.itemView.setVisibility(View.INVISIBLE);//先设置不可见，如果不设置的话，会看到viewHolder返回到原位置时才消失，因为remove会在viewHolder动画执行完成后才将viewHolder删除
                if (mDragListener != null && !isDragDelete) {
                    isDragDelete = true;
                    mDragListener.deleteItem(viewHolder.itemView, viewHolder.getAdapterPosition());
                }
                return;
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        resetData();
        if (mDragListener != null) {
            mDragListener.clearView();
        }
    }

    /**
     * 当长按选中item的时候（拖拽开始的时候）调用
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && mDragListener != null) {
            mDragListener.startDrag();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 设置手指离开后ViewHolder的动画时间，在用户手指离开后调用
     *
     * @param recyclerView
     * @param animationType
     * @param animateDx
     * @param animateDy
     * @return
     */
    @Override
    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        isUp = true;
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }
}
