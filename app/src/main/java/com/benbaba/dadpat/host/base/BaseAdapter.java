package com.benbaba.dadpat.host.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/12/13.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private LayoutInflater mInflater;
    protected List<T> mDatas;
    protected int mLayoutId;
    protected Context mContext;

    public BaseAdapter(Context context, List<T> data, int layoutId) {
        this.mContext = context;
        this.mDatas = data;
        this.mLayoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BaseViewHolder.get(mContext, parent, mLayoutId);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
//        holder.updatePosition(position);
        convert(holder, position, mDatas.get(position));
    }

    public abstract void convert(BaseViewHolder holder, int pos, T t);

    @Override
    public int getItemCount() {
        if (mDatas == null)
            return 0;
        return mDatas.size();
    }
}
