package com.benbaba.module.device.utils;

import com.google.gson.JsonObject;

/**
 * Created by Administrator on 2018/1/30.
 */
public class DeviceUdpUtils {

    private static JsonObject getHeader(String action) {
        JsonObject header = new JsonObject();
        header.addProperty("app_id", "11111111");
        header.addProperty("action", action);
        header.addProperty("dev_id", "11111111");
        return header;
    }

    /**
     * 获取头部
     *
     * @param action
     * @param dev_id
     * @return
     */
    private static JsonObject getHeader(String action, String dev_id) {
        JsonObject header = new JsonObject();
        header.addProperty("app_id", "11111111");
        header.addProperty("action", action);
        header.addProperty("dev_id", dev_id);
//        mHeader.addProperty("action", action);
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

    /**
     * 播报玩具鼓得ID
     *
     * @param name
     * @param dev_id
     * @return
     */
    public static String getDrumBroadCastJson(String name, String dev_id) {
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        //ssssssssssss
        body.addProperty("userName", name);
        object.add("header", getHeader("user_setting", dev_id));
        object.add("body", body);
        return object.toString();
    }

    /**
     * 获取设置wifi得json对象
     *
     * @return
     */
    public static String getHeartPackage() {
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        object.add("header", getHeader("heart"));
        object.add("body", body);
        return object.toString();
    }

    /**
     * 获取设备电量
     *
     * @return
     */
    public static String getDeviceElectric(String devId) {
        JsonObject object = new JsonObject();
        JsonObject body = new JsonObject();
        body.addProperty("get_voltage", "0");
        object.add("header", getHeader("get_voltage", devId));
        object.add("body", body);
        return object.toString();
    }

}
