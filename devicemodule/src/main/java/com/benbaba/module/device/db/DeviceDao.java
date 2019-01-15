package com.benbaba.module.device.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM device_info")
    Flowable<List<DeviceInfo>> queryAll();

    @Query("SELECT * FROM device_info WHERE gId=:gID")
    List<DeviceInfo> queryByGID(int gID);

    @Insert
    void insert(DeviceInfo... infos);

    @Query("SELECT * FROM device_info WHERE dId=:dID")
    DeviceInfo queryByDId(String dID);

    @Update
    void update(DeviceInfo... infos);
}
