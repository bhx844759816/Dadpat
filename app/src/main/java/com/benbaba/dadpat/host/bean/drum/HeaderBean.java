package com.benbaba.dadpat.host.bean.drum;

/**
 * 鼓返回得消息得头
 */
public class HeaderBean {

    private String app_id;
    private String action;
    private String dev_id;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDev_id() {
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }
}
