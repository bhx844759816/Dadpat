package com.benbaba.dadpat.host.bean;

public class WifiBean {

    private String ssid;
    private int level; // 0 - 99 信号强度

    public WifiBean(String ssid, int level) {
        this.ssid = ssid;
        this.level = level;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
