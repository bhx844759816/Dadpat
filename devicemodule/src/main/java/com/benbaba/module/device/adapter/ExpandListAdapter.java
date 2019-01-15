package com.benbaba.module.device.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benbaba.module.device.R;
import com.benbaba.module.device.db.DeviceGroup;
import com.benbaba.module.device.db.DeviceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpandListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<ViewType> mViewTypeList;
    private LayoutInflater mInflater;
    Map<DeviceGroup, List<DeviceInfo>> mDatas;
    private OnScrollListener mScrollListener;

    public ExpandListAdapter(Context context ) {
        mInflater = LayoutInflater.from(context);
        mViewTypeList = new ArrayList<>();
    }

    public void notifyData(Map<DeviceGroup, List<DeviceInfo>> datas) {
        mDatas = datas;
        init(datas);
        notifyDataSetChanged();
    }

    private void init(Map<DeviceGroup, List<DeviceInfo>> datas) {
        mViewTypeList.clear();
        for (Map.Entry<DeviceGroup, List<DeviceInfo>> entry : datas.entrySet()) {
            ViewType parent = new ViewType();
            parent.setGroup(entry.getKey());
            parent.setViewType(ViewType.VIEW_TYPE_GROUPITEM);
            mViewTypeList.add(parent);
            if (entry.getKey().isExpand()) {
                for (DeviceInfo info : entry.getValue()) {
                    ViewType child = new ViewType();
                    child.setViewType(ViewType.VIEW_TYPE_SUBITEM);
                    child.setGroup(entry.getKey());
                    child.setInfo(info);
                    mViewTypeList.add(child);
                }
            }
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case ViewType.VIEW_TYPE_GROUPITEM:
                view = mInflater.inflate(R.layout.adapter_parent_view, viewGroup, false);
                view.setTag(ViewType.VIEW_TYPE_GROUPITEM);
                return new ParentViewHolder(view, mListener);
            case ViewType.VIEW_TYPE_SUBITEM:
                view = mInflater.inflate(R.layout.adapter_child_view, viewGroup, false);
                view.setTag(ViewType.VIEW_TYPE_SUBITEM);
                return new ChildViewHolder(view);
            default:
                view = mInflater.inflate(R.layout.adapter_parent_view, viewGroup, false);
                view.setTag(ViewType.VIEW_TYPE_GROUPITEM);
                return new ParentViewHolder(view, mListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        ViewType type = mViewTypeList.get(position);

        switch (getItemViewType(position)) {
            case ViewType.VIEW_TYPE_GROUPITEM:
                ParentViewHolder parentViewHolder = (ParentViewHolder) holder;
                parentViewHolder.bindView(type.getGroup());
                break;
            case ViewType.VIEW_TYPE_SUBITEM:
                ChildViewHolder childViewHolder = (ChildViewHolder) holder;
                childViewHolder.bindView(type.getInfo());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mViewTypeList.get(position).getViewType();
    }

    private ItemClickListener mListener = new ItemClickListener() {
        @Override
        public void parentStateChange(DeviceGroup group) {
            notifyData(mDatas);
            if (group.isExpand()) {
                for (int i = 0; i < mViewTypeList.size(); i++) {
                    ViewType type = mViewTypeList.get(i);
                    if(group.equals(type.getGroup())){
                        mScrollListener.scrollToPos(i);
                        break;
                    }
                }
            }
        }
    };
    public void setOnScrollListener(OnScrollListener listener){
        this.mScrollListener = listener;
    }
    @Override
    public int getItemCount() {
        return mViewTypeList.size();
    }

    static class ViewType {
        static final int VIEW_TYPE_GROUPITEM = 0;
        static final int VIEW_TYPE_SUBITEM = 1;
        private int viewType;
        private DeviceGroup group;
        private DeviceInfo info;

        int getViewType() {
            return viewType;
        }

        void setViewType(int viewType) {
            this.viewType = viewType;
        }

        public DeviceGroup getGroup() {
            return group;
        }

        public void setGroup(DeviceGroup group) {
            this.group = group;
        }

        public DeviceInfo getInfo() {
            return info;
        }

        public void setInfo(DeviceInfo info) {
            this.info = info;
        }
    }

    public interface OnScrollListener{
        void scrollToPos(int pos);
    }
}
