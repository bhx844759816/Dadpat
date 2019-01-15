package com.benbaba.module.device.utils;

/**
 * 通过Rssi(信号强度)计算距离
 */
public class RissUtils {
    /**
     * A - 发射端和接收端相隔1米时的信号强度
     */
    private static final double A_Value = 50;
    /**
     * n - 环境衰减因子
     */
    private static final double n_Value = 2.5;

    /**
     * 根据Rssi获得返回的距离,返回数据单位为m
     */
    public static double getDistance(int rssi) {
        int iRssi = Math.abs(rssi);
        double power = (iRssi - A_Value) / (10 * n_Value);
        return Math.pow(10, power);
    }

}
