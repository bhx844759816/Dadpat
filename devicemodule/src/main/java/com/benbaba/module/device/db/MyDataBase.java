package com.benbaba.module.device.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;


/**
 * 数据库实例
 */
@Database(entities = {DeviceInfo.class, DeviceGroup.class}, version = 1, exportSchema = false)
public abstract class MyDataBase extends RoomDatabase {
    //获取设备表得Dao
    public abstract DeviceDao getDeviceDao();

    public abstract DeviceGroupDao getDeviceGroupDao();

    private final static Object sLock = new Object();
    private static MyDataBase sDataBase;

    public static MyDataBase init(Context context) {
        if (sDataBase == null) {
            synchronized (sLock) {
                if (sDataBase == null) {
                    sDataBase = Room.databaseBuilder(context.getApplicationContext(),
                            MyDataBase.class, "dadpat.db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    Log.i("TAG","MyDataBase onCreate");
                                    initData(db);
                                    super.onCreate(db);
                                }

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
//                                    initData(db);
                                    super.onOpen(db);
                                }
                            })
                            .build();
                }
            }
        }
        return sDataBase;
    }

    private static void initData(SupportSQLiteDatabase database) {
        Log.i("TAG","initData");
        database.execSQL("INSERT INTO device_group VALUES(1,'玩具鼓')");
        database.execSQL("INSERT INTO device_group VALUES(2,'蓝牙鼓')");
//
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000001','dadpat_01',1)");
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000002','dadpat_02',1)");
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000003','dadpat_03',1)");
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000004','dadpat_04',1)");
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000005','dadpat_05',1)");
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000006','dadpat_06',1)");
//
//
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000007','blueTooth_01',2)");
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000008','blueTooth_02',2)");
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000009','blueTooth_03',2)");
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000010','blueTooth_04',2)");
//        database.execSQL("INSERT INTO device_info('dId','device_name','gId') VALUES('00000011','blueTooth_05',2)");
//
//
//        Cursor cursor_group = database.query("SELECT * FROM device_group");
//        while (cursor_group.moveToNext()) {
//            int gID = cursor_group.getInt(cursor_group.getColumnIndex("gId"));
//            String name = cursor_group.getString(cursor_group.getColumnIndex("groupName"));
//            Log.i("TAG", "gID:" + gID + ",name:" + name);
//        }
//        cursor_group.close();
//        Cursor cursor_info = database.query("SELECT * FROM device_info");
//        while (cursor_info.moveToNext()) {
//            int dID = cursor_info.getInt(cursor_info.getColumnIndex("dId"));
//            String dName = cursor_info.getString(cursor_info.getColumnIndex("device_name"));
//            String gId = cursor_info.getString(cursor_info.getColumnIndex("gId"));
//            Log.i("TAG", "dID:" + dID + ",dName:" + dName + ",gid:" + gId);
//        }
//        cursor_info.close();
    }
}
