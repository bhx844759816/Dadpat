package com.benbaba.module.device.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

@Dao
public interface DeviceGroupDao {
    /**
     * 查询全部分组对应得设备列表
     *
     * @return
     */
    @Query("SELECT * FROM device_group")
    Flowable<List<DeviceGroup>> queryAll();

    @Insert
    void insert(DeviceGroup... groups);

}
