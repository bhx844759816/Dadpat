package com.benbaba.dadpat.host.bean;

public class MusicBuffer {
    public static final String TABLE_NAME = "music";

    public static final String NAME_SONGID = "songId";

    public static final String NAME_SONGNAME = "songName";

    public static final String NAME_FILE_SAVE_DIR = "songSaveDir";

    public static final String NAME_SONGBGMPATH = "songBgmPath";

    public static final String NAME_SONGPERSONPATH = "songPersonPath";

    public static final String NAME_SONGRHYTHMTYPE = "songRhythmType";

    public static final String NAME_SONGJSONPATH = "songJsonPath";

//    public static final String NAME_SONGJSONPATH = "songJsonPath";
    private String songId;//歌曲ID

    private String songName;//歌曲得名称

    private String songSaveDir;//歌曲得名称

    private String songBgmPath;//背景音乐得路径

    private String songPersonPath;//背景音乐带人声得路径

    private String songRhythmType;//游戏的节奏练习的曲

    private String songJsonPath;//游戏json文件得路径

    private boolean isSelect;// 是否被选中

    private String bufferSize;// 文件大小

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongSaveDir() {
        return songSaveDir;
    }

    public void setSongSaveDir(String songSaveDir) {
        this.songSaveDir = songSaveDir;
    }

    public String getSongBgmPath() {
        return songBgmPath;
    }

    public void setSongBgmPath(String songBgmPath) {
        this.songBgmPath = songBgmPath;
    }

    public String getSongPersonPath() {
        return songPersonPath;
    }

    public void setSongPersonPath(String songPersonPath) {
        this.songPersonPath = songPersonPath;
    }

    public String getSongRhythmType() {
        return songRhythmType;
    }

    public void setSongRhythmType(String songRhythmType) {
        this.songRhythmType = songRhythmType;
    }

    public String getSongJsonPath() {
        return songJsonPath;
    }

    public void setSongJsonPath(String songJsonPath) {
        this.songJsonPath = songJsonPath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(String bufferSize) {
        this.bufferSize = bufferSize;
    }
}
