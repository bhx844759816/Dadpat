package com.benbaba.dadpat.host.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.adapter.MessageAdapter;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.bean.NoticeBean;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * 消息和公告的Dialog
 */
@SuppressWarnings("checkresult")
public class MessageDialogFragment extends BaseDialogFragment {
    public static final int STATE_MESSAGE = 0x01;
    public static final int STATE_NOTICE = 0x02;//
    @BindView(R.id.id_message_list)
    RecyclerView mMessageList;
    private List<NoticeBean> mData;
    private MessageAdapter mAdapter;
    private int mState;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_message;
    }

    @Override
    public void initView(View view) {
        mData = new ArrayList<>();
        mAdapter = new MessageAdapter(mContext, mData);
        mMessageList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mMessageList.setAdapter(mAdapter);
//        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            switch (checkedId) {
//                case R.id.id_notice:
//                    mNotice.setChecked(true);
//                    mMessage.setChecked(false);
//                    break;
//                case R.id.id_message:
//                    mNotice.setChecked(false);
//                    mMessage.setChecked(true);
//                    break;
//            }
//        });
    }


    public void setState(int state) {
        mState = state;
    }

    @Override
    public void initData() {
        getNoticeList();
//        switch (mState) {
//            case STATE_MESSAGE:
//                mMessage.setChecked(true);
//                mNotice.setChecked(false);
//                break;
//            case STATE_NOTICE:
//                mMessage.setChecked(false);
//                mNotice.setChecked(true);
//                getNoticeList();
//                break;
//        }
    }

    /**
     * 获取公告列表
     */
    private void getNoticeList() {
        HttpManager.getInstance().getNoticeList()
                .subscribe(noticeBeans -> {
                    mData.clear();
                    mData.addAll(noticeBeans);
                    mAdapter.notifyDataSetChanged();
                }, throwable -> L.i("throwable:" + throwable.getLocalizedMessage()));
    }
}