package com.benbaba.module.device.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


import java.io.Serializable;


@Entity(tableName = "device_info")
public class DeviceInfo implements Serializable {
    @PrimaryKey
    @NonNull
    private String dId; //设备得ID,设备得唯一ID,MAC地址

    @ColumnInfo(name = "device_name")
    private String name; //设备得名称

    @ColumnInfo(name = "ssid")
    private String bindWifiName;//设备连接得Wifi得名称

    @ColumnInfo(name = "setup_time")
    private Long setUpTime;// 配置时间

    @ForeignKey(entity = DeviceGroup.class, parentColumns = "gId", childColumns = "gid")
    private int gId; //设备分组ID

    @Ignore
    private int level; //wifi得信号强度

    @Ignore
    private String bSSID; //设备wifi得MAC地址

    @Ignore
    private int rssi; //设备得Rssi

    @Ignore
    private boolean isCheck;

    @Ignore
    private boolean isOnLine;// 是否在线

    @NonNull
    public String getDId() {
        return dId;
    }

    public void setDId(@NonNull String dId) {
        this.dId = dId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBindWifiName() {
        return bindWifiName;
    }

    public void setBindWifiName(String bindWifiName) {
        this.bindWifiName = bindWifiName;
    }

    public int getGId() {
        return gId;
    }

    public void setGId(int gId) {
        this.gId = gId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getbSSID() {
        return bSSID;
    }

    public void setbSSID(String bSSID) {
        this.bSSID = bSSID;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }


    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DeviceInfo) {
            DeviceInfo info = (DeviceInfo) obj;
            if (info.getDId().equals(this.dId)) {
                return true;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return dId.hashCode();
    }

    public Long getSetUpTime() {
        return setUpTime;
    }

    public void setSetUpTime(Long setUpTime) {
        this.setUpTime = setUpTime;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "dId='" + dId + '\'' +
                ", name='" + name + '\'' +
                ", bindWifiName='" + bindWifiName + '\'' +
                ", setUpTime=" + setUpTime +
                ", gId=" + gId +
                ", level=" + level +
                ", bSSID='" + bSSID + '\'' +
                ", rssi=" + rssi +
                ", isCheck=" + isCheck +
                '}';
    }
}
