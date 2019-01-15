package com.benbaba.dadpat.host.utils;

import com.google.gson.JsonObject;

/**
 * Created by Administrator on 2018/1/30.
 */
public class DeviceUdpUtils {

    private static JsonObject getHeader(String action) {
        JsonObject header = new JsonObject();
        header.addProperty("app_id", "11111111");
        header.addProperty("action", action);
        header.addProperty("device_id", "11111111");
        return header;
    }

    /**
     * 获取设置wifi得json对象
     *
     * @param ssid
     * @param psd
     * @return
     */
    public static String getWifiSettingJson(String ssid, String psd) {
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        body.addProperty("ssid", ssid);
        body.addProperty("password", psd);
        body.addProperty("type", "wpa");
        object.add("header", getHeader("network"));
        object.add("body", body);
        return object.toString();
    }
    public static String getBgMusicOpenClosJson(boolean isOpen){
//        bg_music
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        if(isOpen){
            body.addProperty("type", "1");
        }else {
            body.addProperty("type", "0");
        }
        object.add("header", getHeader("bg_music"));
        object.add("body", body);
        return object.toString();
    }
    //"{\"header\": { \"app_id\": \"365888\", \"action\": \"bluetooth\"," +
//        " \"dev_id\": \"FF0BDEFF\" },\"body\": {\"address\":\"ff:00:aa:ff:ff:ff\"}}";

    /**
     * 获取蓝牙信息得消息
     *
     * @return
     */
    public static String getBlueToothJson() {
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        body.addProperty("type", "0");
        object.add("header", getHeader("getBluetooth"));
        object.add("body", body);
        return object.toString();
    }

    /**
     * 获取发送鼓蓝牙信息得消息
     *
     * @param address
     * @return
     */
    public static String getSendBlueToothAddressJson(String address) {
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        body.addProperty("address", address);
        object.add("header", getHeader("getBluetooth"));
        object.add("body", body);
        return object.toString();
    }
    public static String getPlaySongJson(String songId) {
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        body.addProperty("music_id", songId);
        body.addProperty("speed", "1.0f");
        object.add("header", getHeader("rhythmPrompting"));
        object.add("body", body);
        return object.toString();
    }
    public static String getPauseSongJson(String songId) {
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        body.addProperty("music_id", songId);
        object.add("header", getHeader("rhythmPrompting_pause"));
        object.add("body", body);
        return object.toString();
    }

    public static String getResumeSongJson(String songId) {
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        body.addProperty("music_id", songId);
        object.add("header", getHeader("rhythmPrompting_resume"));
        object.add("body", body);
        return object.toString();
    }
//    /**
//     * 获取初始化乐谱的json
//     *
//     * @param songId 歌曲ID
//     * @return
//     */
//    public static String getInitMusicJson(String songId, String syllabic_flag) {
//        JsonObject object = new JsonObject();
//        JsonObject body = new JsonObject();
//        body.addProperty("music_id", songId);
//        body.addProperty("syllabic_flag", syllabic_flag);
//        object.add("header", getHeader("music_setting"));
//        object.add("body", body);
//        return object.toString();
//    }
//
//    /**
//     * "{\"header\":{\"app_id\":\"365888\",\"action\":\"syllabic_cues\",\"dev_id\":
//     * \"FF0BDEFF, FF0BDEFA, FF0BDEFD\" },\"body\": { \"musical_type \": \"0\"}}"
//     *
//     * @param musical_type 0 为鼓 1为手谍
//     * @return
//     */
//    public static String getMusiceTeachJson(String musical_type) {
//        JsonObject object = new JsonObject();
//        JsonObject body = new JsonObject();
//        body.addProperty("musical_type", musical_type);
//        object.add("header", getHeader("syllabic_cues"));
//        object.add("body", body);
//        return object.toString();
//    }
//
//    /**
//     * "{\"header\":{\"app_id\":\"365888\",\"action\":\"rhythmPrompting\",\"dev_id\": \"FF0BDEFF, FF0BDEFA, FF0BDEFD\" },
//     * \"body\": { \" musical_type \": \"0\",\"type\":\"0\"}}"
//     *
//     * @return
//     */
//    public static String getMusicStartJson() {
//        JsonObject object = new JsonObject();
//        JsonObject body = new JsonObject();
//        body.addProperty("musical_type", "0");
//        body.addProperty("type", "1");
//        object.add("header", getHeader("rhythmPrompting"));
//        object.add("body", body);
//        return object.toString();
//    }
//
//    /**
//     * "{\"header\":{\"app_id\":\"365888\",\"action\":\"ryth_game\",\"dev_id\":
//     * \"FF0BDEFF, FF0BDEFA, FF0BDEFD\" },\"body\": { \"musical_type\": \"0\"}}"
//     *
//     * @return
//     */
//    public static String getRhythmGameJson() {
//        JsonObject object = new JsonObject();
//        JsonObject body = new JsonObject();
//        body.addProperty("musical_type", "0");
//        body.addProperty("type", "1");
//        object.add("header", getHeader("ryth_game"));
//        object.add("body", body);
//        return object.toString();
//    }
//
//    public static String connectDrumJson() {
//        JsonObject object = new JsonObject();
//        JsonObject body = new JsonObject();
//        body.addProperty("musical_type", "0");
//        body.addProperty("type", "1");
//        object.add("header", getHeader("connect"));
//        object.add("body", body);
//        return object.toString();
//    }
}
