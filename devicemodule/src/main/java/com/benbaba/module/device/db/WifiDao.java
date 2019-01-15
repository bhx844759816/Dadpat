package com.benbaba.module.device.db;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.benbaba.module.device.bean.WifiBean;

import java.util.List;


@Dao
public interface WifiDao {
    /**
     * 插入wifi信息
     *
     * @param wifiBean
     */
    @Insert
    void insert(WifiBean wifiBean);

    /**
     * 查询全部得wifiBean
     *
     * @return
     */
    @Query("SELECT * FROM wifi_info")
    List<WifiBean> queryAll();

    /**
     * 根据uid查询对应得wifi对象
     *
     * @param uid
     * @return
     */
    @Query("SELECT * FROM wifi_info WHERE uid=:uid")
    WifiBean getWifiBean(String uid);

}
