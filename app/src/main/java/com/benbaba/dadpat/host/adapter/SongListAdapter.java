package com.benbaba.dadpat.host.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseAdapter;
import com.benbaba.dadpat.host.base.BaseViewHolder;
import com.benbaba.dadpat.host.bean.SongBean;

import java.util.List;

public class SongListAdapter extends BaseAdapter<SongBean> {

    public SongListAdapter(Context context, List<SongBean> data) {
        super(context, data, R.layout.adapter_song_list);
    }

    @Override
    public void convert(BaseViewHolder holder, int pos, SongBean songBean) {
        TextView name = holder.getView(R.id.id_song_list_name_tv);
        TextView sort = holder.getView(R.id.id_song_list_sort_tv);
        ImageView player = holder.getView(R.id.id_song_list_play_iv);
        ConstraintLayout parent = holder.getView(R.id.id_song_list_parent);
        name.setText(songBean.getSongName());
        sort.setText(String.valueOf(pos + 1));
        if (songBean.isSelect()) {
            name.setTextColor(Color.parseColor("#ffffff"));
            sort.setTextColor(Color.parseColor("#ffffff"));
            parent.setBackgroundColor(Color.parseColor("#79d3f9"));
        } else {
            name.setTextColor(Color.parseColor("#666666"));
            sort.setTextColor(Color.parseColor("#666666"));
            parent.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        if (songBean.isPlaying()) {
            player.setVisibility(View.VISIBLE);
            sort.setVisibility(View.INVISIBLE);
        } else {
            player.setVisibility(View.INVISIBLE);
            sort.setVisibility(View.VISIBLE);
        }

//        parent.setOnClickListener(v -> {
//            for (SongBean data : mDatas) {
//                data.setSelect(false);
//            }
//            songBean.setSelect(true);
//            notifyDataSetChanged();
//        });
    }


}
