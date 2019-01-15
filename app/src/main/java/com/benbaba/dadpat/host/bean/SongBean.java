package com.benbaba.dadpat.host.bean;

public class SongBean {
    private String songName;//歌曲名称
    private String songId;// 歌曲ID
    private boolean isPlaying;//是否正在播放



    private boolean isSelect;//是否选中

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
