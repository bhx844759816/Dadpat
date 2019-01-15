package com.benbaba.module.device.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "device_group")
public class DeviceGroup {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int gId;//分组id

    private String groupName;// 分组名称
    /**
     * 是否展开
     */
    @Ignore
    private boolean isExpand;

    @NonNull
    public int getGId() {
        return gId;
    }

    public void setGId(@NonNull int gId) {
        this.gId = gId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    @Override
    public String toString() {
        return "DeviceGroup{" +
                "gId=" + gId +
                ", groupName='" + groupName + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return groupName.hashCode() + gId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DeviceGroup) {
            DeviceGroup deviceGroup = (DeviceGroup) obj;
            if (deviceGroup.gId == this.gId) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
