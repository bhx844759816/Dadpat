package com.benbaba.dadpat.host.adapter;

import android.content.Context;


import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseAdapter;
import com.benbaba.dadpat.host.base.BaseViewHolder;
import com.benbaba.dadpat.host.bean.NoticeBean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/23.
 */
public class MessageAdapter extends BaseAdapter<NoticeBean> {

    public MessageAdapter(Context context, List<NoticeBean> data) {
        super(context, data, R.layout.adapter_message);
    }

    @Override
    public void convert(BaseViewHolder holder, int pos, NoticeBean noticeBean) {
        holder.setText(R.id.id_notice_title, noticeBean.getNotcieTitle());
        holder.setText(R.id.id_notice_content, noticeBean.getNoticeContent());
        holder.setText(R.id.id_notice_time, noticeBean.getNoticeTime());
    }
}
