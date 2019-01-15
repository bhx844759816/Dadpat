package com.benbaba.module.device.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池得管理类
 */
public class ThreadPoolManager {
    private static int sPools = Runtime.getRuntime().availableProcessors();
    /**
     * 创建线程池
     */
    private static ExecutorService sThreadPools;

    static {
        sThreadPools = Executors.newFixedThreadPool(sPools);
    }

    /**
     * 调用线程池执行runnable
     *
     * @param runnable
     */
    public static void execu(Runnable runnable) {
        sThreadPools.execute(runnable);
    }
}
