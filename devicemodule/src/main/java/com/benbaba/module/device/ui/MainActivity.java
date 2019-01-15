package com.benbaba.module.device.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.benbaba.module.device.R;
import com.benbaba.module.device.wifi.DeviceManager;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private DeviceManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mManager = new DeviceManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mManager.startSearchWifi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mManager.stopSearchWifi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DeviceManager.GPS_SETTING_REQUEST_CODE && resultCode == RESULT_OK) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
