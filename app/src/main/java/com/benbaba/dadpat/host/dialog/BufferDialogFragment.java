package com.benbaba.dadpat.host.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.adapter.MusicBufferAdapter;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.bean.MusicBuffer;
import com.benbaba.dadpat.host.callback.OnBufferDialogCallBack;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.utils.FileUtils;
import com.benbaba.dadpat.host.utils.L;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.component.provider.PluginProviderClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 缓存歌曲的Dialog
 */
@SuppressWarnings("checkresult")
public class BufferDialogFragment extends BaseDialogFragment {

    @BindView(R.id.id_buffer_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.id_buffer_selectAll)
    TextView mSelectAllText;
    private List<MusicBuffer> mMusicBufferList;
    private MusicBufferAdapter mAdapter;
    private OnBufferDialogCallBack mCallBack;
    private boolean isSelectAll;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity instanceof OnBufferDialogCallBack) {
            mCallBack = (OnBufferDialogCallBack) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCallBack != null) {
            mCallBack = null;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_music_buffer;
    }

    @Override
    public void initData() {
        mMusicBufferList = new ArrayList<>();
        getMusicBuffer();
        setRecyclerViewAdapter();
    }

    @Override
    public void initView(View view) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mCallBack != null)
            mCallBack.onBufferDialogDismiss();

    }

    /**
     * 设置RecyclerView的适配器
     */
    private void setRecyclerViewAdapter() {
        if (mAdapter == null) {
            mAdapter = new MusicBufferAdapter(mContext, mMusicBufferList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取音乐的缓存文件
     */
    private void getMusicBuffer() {
        try {
            Uri uri = Uri.parse("content://com.benbaba.dadpat.plugin.game.instruments/music");
            Cursor cursor = PluginProviderClient.query(RePlugin.fetchContext("Plugin_Instrument"), uri,
                    null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String songId = cursor.getString(cursor.getColumnIndex(MusicBuffer.NAME_SONGID));
                    String songName = cursor.getString(cursor.getColumnIndex(MusicBuffer.NAME_SONGNAME));
                    String songSaveDir = cursor.getString(cursor.getColumnIndex(MusicBuffer.NAME_FILE_SAVE_DIR));
                    String bgmPath = cursor.getString(cursor.getColumnIndex(MusicBuffer.NAME_SONGBGMPATH));
                    String songRhythmType = cursor.getString(cursor.getColumnIndex(MusicBuffer.NAME_SONGRHYTHMTYPE));
                    String jsonPath = cursor.getString(cursor.getColumnIndex(MusicBuffer.NAME_SONGJSONPATH));
                    String personPath = cursor.getString(cursor.getColumnIndex(MusicBuffer.NAME_SONGPERSONPATH));
                    MusicBuffer buffer = new MusicBuffer();
                    buffer.setSongId(songId);
                    buffer.setSongName(songName);
                    buffer.setSongBgmPath(bgmPath);
                    buffer.setSongSaveDir(songSaveDir);
                    buffer.setSongPersonPath(personPath);
                    buffer.setSongRhythmType(songRhythmType);
                    buffer.setSongJsonPath(jsonPath);
                    L.i("saveDir:" + songSaveDir);
                    String bufferSize = FileUtils.formatFileSize(FileUtils.getFileSize(bgmPath) +
                            FileUtils.getFileSize(personPath) + FileUtils.getFileSize(songRhythmType) +
                            FileUtils.getFileSize(jsonPath));
                    buffer.setBufferSize(bufferSize);
                    mMusicBufferList.add(buffer);
                }
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @OnClick({R.id.id_buffer_delete, R.id.id_music_buffer_finish, R.id.id_buffer_selectAll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_buffer_delete://删除
                deleteSelectMusicBuffer();
                break;
            case R.id.id_music_buffer_finish:
                dismiss();
                break;
            case R.id.id_buffer_selectAll:
                isSelectAll = !isSelectAll;
                updateMusicBufferState();
                if (isSelectAll) {
                    mSelectAllText.setText("取消全选");
                } else {
                    mSelectAllText.setText("全选");
                }
                break;
        }
    }

    private void updateMusicBufferState() {
        for (MusicBuffer buffer : mMusicBufferList) {
            buffer.setSelect(isSelectAll);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 删除选中的缓存曲目
     */
    private void deleteSelectMusicBuffer() {
        DialogFactory.showLoadingDialog(getActivity());
        Observable.create(e -> {
            Uri uri = Uri.parse("content://com.benbaba.dadpat.plugin.game.instruments/music");
            for (MusicBuffer buffer : mMusicBufferList) {
                if (buffer.isSelect()) {
                    File file = new File(buffer.getSongSaveDir());
                    // 删除文件
                    FileUtils.deleteFile(file);
                    //删除数据库记录
                    PluginProviderClient.delete(RePlugin.fetchContext("Plugin_Instrument"),
                            uri, MusicBuffer.NAME_SONGID + "=?", new String[]{buffer.getSongId()});
                }
            }
            e.onNext(new Object());
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            DialogFactory.dismissLoadingDialog(getActivity());
            isSelectAll = false;
            mSelectAllText.setText("全选");
            mMusicBufferList.clear();
            getMusicBuffer();
            setRecyclerViewAdapter();
        });

    }
}
