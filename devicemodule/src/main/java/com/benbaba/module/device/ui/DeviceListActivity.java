package com.benbaba.module.device.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.benbaba.module.device.R;
import com.benbaba.module.device.adapter.DividerItemDecoration;
import com.benbaba.module.device.adapter.ExpandListAdapter;
import com.benbaba.module.device.db.DeviceGroup;
import com.benbaba.module.device.db.DeviceInfo;
import com.benbaba.module.device.db.MyDataBase;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("CheckResult")
public class DeviceListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ExpandListAdapter mAdapter;
    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        mRecyclerView = findViewById(R.id.id_recyclerView);
        findViewById(R.id.id_add_toy_drum).setOnClickListener(this);
        findViewById(R.id.id_add_blue_tooth_drum).setOnClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new ExpandListAdapter(this);
        mAdapter.setOnScrollListener(new ExpandListAdapter.OnScrollListener() {
            @Override
            public void scrollToPos(int pos) {
                smoothMoveToPosition(mRecyclerView, pos);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mRecyclerView, mToPosition);
                }
            }
        });




        MyDataBase.init(this)
                .getDeviceGroupDao()
                .queryAll()
                .map(new Function<List<DeviceGroup>, Map<DeviceGroup, List<DeviceInfo>>>() {
                    @Override
                    public Map<DeviceGroup, List<DeviceInfo>> apply(List<DeviceGroup> deviceGroups) throws Exception {
                        Map<DeviceGroup, List<DeviceInfo>> map = new LinkedHashMap<>();
                        for (DeviceGroup deviceGroup : deviceGroups) {
                            List<DeviceInfo> list = MyDataBase.init(DeviceListActivity.this).getDeviceDao().queryByGID(deviceGroup.getGId());
                            map.put(deviceGroup, list);
                        }
                        return map;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<DeviceGroup, List<DeviceInfo>>>() {
                    @Override
                    public void accept(Map<DeviceGroup, List<DeviceInfo>> deviceGroupListMap) throws Exception {
                        mAdapter.notifyData(deviceGroupListMap);
                    }
                });

    }

    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.id_add_toy_drum || i == R.id.id_add_blue_tooth_drum) {
            Intent intent = new Intent(this, SearchDeviceActivity2.class);
            startActivity(intent);

        }
    }
}
