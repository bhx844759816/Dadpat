package com.benbaba.dadpat.host.adapter;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseAdapter;
import com.benbaba.dadpat.host.base.BaseViewHolder;
import com.benbaba.dadpat.host.bean.MusicBuffer;

import java.util.List;

/**
 * 歌曲缓冲得适配器
 * Created by Administrator on 2017/12/23.
 */
public class MusicBufferAdapter extends BaseAdapter<MusicBuffer> {

    public MusicBufferAdapter(Context context, List<MusicBuffer> data) {
        super(context, data, R.layout.adapter_music_buffer);
    }

    @Override
    public void convert(BaseViewHolder holder, int pos, MusicBuffer buffer) {
        CheckBox cb = holder.getView(R.id.id_music_buffer_cb);
        TextView size = holder.getView(R.id.id_music_buffer_size);
        cb.setChecked(buffer.isSelect());
        cb.setText(buffer.getSongName());
        size.setText(buffer.getBufferSize());
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> buffer.setSelect(isChecked));
    }


}
