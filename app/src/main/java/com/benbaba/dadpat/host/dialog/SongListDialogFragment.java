package com.benbaba.dadpat.host.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.adapter.SongListAdapter;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.bean.SongBean;
import com.benbaba.dadpat.host.utils.DeviceUdpUtils;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.NetUtils;
import com.benbaba.dadpat.host.utils.SocketManager;
import com.benbaba.dadpat.host.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 歌单列表得Dialog
 */
public class SongListDialogFragment extends BaseDialogFragment {


    @BindView(R.id.id_dialog_song_list_rv)
    RecyclerView mSongListRv;
    @BindView(R.id.id_dialog_song_list_play)
    ImageView mPlayImg; //播放按钮
    private SongListAdapter mAdapter;
    private List<SongBean> mSongList;
    private boolean isStarting;
    private List<Pair<String, String>> mData = Arrays.asList(
            new Pair<>("大长今", "1"),
            new Pair<>("铃儿响叮当", "2"),
            new Pair<>("伦敦桥", "3"),
            new Pair<>("玛丽有只小羔羊", "4"),
            new Pair<>("我是一个粉刷匠", "5"),
            new Pair<>("洋娃娃", "6")
    );
    private SocketManager mSocketManager;
    private SocketManager.OnSocketReceiveCallBack mSocketCallBack = bean -> {

    };

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_song_list;
    }

    @Override
    public void initView(View view) {
        getDialog().setCanceledOnTouchOutside(false);
        mSocketManager = new SocketManager(mSocketCallBack);
        mSocketManager.startReceiveUdpMsg();
        initSongList();
        mSongListRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new SongListAdapter(getContext(), mSongList);
        mSongListRv.setAdapter(mAdapter);
    }

    /**
     * 初始化歌单得列表
     */
    private void initSongList() {
        mSongList = new ArrayList<>();
        for (Pair<String, String> data : mData) {
            SongBean songBean = new SongBean();
            songBean.setSongName(data.first);
            songBean.setSongId(data.second);
            mSongList.add(songBean);
        }
        mSongList.get(0).setSelect(true);
    }

    @OnClick({R.id.id_dialog_song_list_last, R.id.id_dialog_song_list_play, R.id.id_dialog_song_list_next, R.id.id_dialog_song_list_dismiss})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_dialog_song_list_last: {
                int currentSelect = getCurrentSelect();
                if (currentSelect == 0) {
                    ToastUtils.showShortToast(mContext, "没有上一首啦");
                } else {
                    resetData();
                    isStarting = false;
                    String msg = DeviceUdpUtils.getPauseSongJson(mSongList.get(currentSelect).getSongId());
                    mSocketManager.sendReceiveUdpMsg(msg);
                    mPlayImg.setBackgroundResource(R.drawable.song_list_icon_play_start);
                    currentSelect--;
                    mSongList.get(currentSelect).setSelect(true);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            }
            case R.id.id_dialog_song_list_play:
                SongBean songBean = mSongList.get(getCurrentSelect());
                isStarting = !isStarting;
                if (isStarting) {
                    songBean.setPlaying(true);
                    mPlayImg.setBackgroundResource(R.drawable.song_list_icon_play_pause);
                    if (checkWifi())
                        mSocketManager.sendReceiveUdpMsg(DeviceUdpUtils.getPlaySongJson(songBean.getSongId()));
                } else {
                    songBean.setPlaying(false);
                    mPlayImg.setBackgroundResource(R.drawable.song_list_icon_play_start);
                    if (checkWifi())
                        mSocketManager.sendReceiveUdpMsg(DeviceUdpUtils.getPauseSongJson(songBean.getSongId()));
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.id_dialog_song_list_next: {
                int currentSelect = getCurrentSelect();
                if (currentSelect == mSongList.size() - 1) {
                    ToastUtils.showShortToast(mContext, "没有下一首啦");
                } else {
                    resetData();
                    isStarting = false;
                    if (checkWifi())
                        mSocketManager.sendReceiveUdpMsg(DeviceUdpUtils.getPauseSongJson(mSongList.get(currentSelect).getSongId()));
                    mPlayImg.setBackgroundResource(R.drawable.song_list_icon_play_start);
                    currentSelect++;
                    mSongList.get(currentSelect).setSelect(true);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            }
            case R.id.id_dialog_song_list_dismiss:
                dismissAllowingStateLoss();
                break;

        }
    }

    @OnCheckedChanged(R.id.id_dialog_song_List_switch)
    public void onRadioChanged(boolean changed) {
        if (checkWifi()) {
            mSocketManager.sendReceiveUdpMsg(DeviceUdpUtils.getBgMusicOpenClosJson(changed));
        }
    }

    /**
     * 获取当前选中得对象
     *
     * @return
     */
    private int getCurrentSelect() {
        for (int i = 0; i < mSongList.size(); i++) {
            SongBean songBean = mSongList.get(i);
            if (songBean.isSelect()) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 重置列表状态
     */
    private void resetData() {
        for (SongBean songBean : mSongList) {
            songBean.setPlaying(false);
            songBean.setSelect(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocketManager != null) {
            mSocketManager.release();
            mSocketManager = null;
        }
    }

    public boolean checkWifi() {
        if (NetUtils.isWifiConnected(mContext)) {
            return true;
        } else {
            ToastUtils.showShortToast(mContext, "手机未连接wifi");
            return false;
        }

    }
}
