package com.benbaba.dadpat.host.utils;

import android.content.Context;
import android.content.res.Resources;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;
import com.benbaba.dadpat.host.bean.PluginBean;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.RePluginConfig;
import com.qihoo360.replugin.model.PluginInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 基于Aria下载框架的插件管理类
 * 框架地址<code>https://github.com/AriaLyy/Aria</code>
 */
@SuppressWarnings("checkresult")
public class PluginManager {

    private List<PluginBean> mDownPluginList;
    private static PluginManager mInstance;
    private OnPluginManagerCallBack mCallBack;
    private boolean isAllowNetWorkDownLand;

    private PluginManager() {
        mDownPluginList = new ArrayList<>();
        Aria.download(this).register();
    }

    public static PluginManager getInstance() {
        if (mInstance == null) {
            synchronized (PluginManager.class) {
                if (mInstance == null)
                    mInstance = new PluginManager();
            }
        }
        return mInstance;
    }

    /**
     * 开始下载
     */
    public void startDownLand(PluginBean bean) {
        if (!mDownPluginList.contains(bean)) {
            mDownPluginList.add(bean);
        }
        if (bean.isNeedUpdate()) {
            Aria.download(this)
                    .load(bean.getUrl())
                    .setFilePath(bean.getSavePath()).removeRecord();
        }
        Aria.download(this)
                .load(bean.getUrl())
                .setFilePath(bean.getSavePath())
                .start();
    }

    public void setOnPluginManagerCallBack(OnPluginManagerCallBack callBack) {
        mCallBack = callBack;
    }

    public boolean isTaskRunning(PluginBean bean) {
        return Aria.download(this).load(bean.getUrl()).taskExists();
    }

    /**
     * DownloadEntity 用于标识下载任务得对象
     *
     * @param bean
     * @return
     */
    public String getFileSize(PluginBean bean) {
        L.i("getFileSize:" + Aria.download(this).load(bean.getUrl()).setFilePath(bean.getSavePath()).getFileSize());
        return Aria.download(this).load(bean.getUrl()).setFilePath(bean.getSavePath()).getConvertFileSize();
    }

    public long getNeedDownSize() {
        List<DownloadEntity> list = Aria.download(this).getAllNotCompletTask();
        long size = 0;
        for (DownloadEntity downloadEntity : list) {
            size += (downloadEntity.getFileSize() - downloadEntity.getCurrentProgress());
        }
        return size;
    }

    /**
     * 获取当前下载的进度
     */
    public float getCurrentDownProgress(String url) {
        DownloadEntity entity = Aria.download(this).getDownloadEntity(url);
        if (entity != null) {
            return entity.getCurrentProgress() * 1.0f / entity.getFileSize();
        } else {
            return 0;
        }
    }

    /**
     * 停止下载
     */
    public void stopDownLand(PluginBean bean) {
        Aria.download(this)
                .load(bean.getUrl())
                .stop();
    }

    /**
     * 停止全部任务
     */
    public void stopAllDownLand() {
        Aria.download(this).stopAllTask();
    }

    /**
     * 是否有下载任务
     * @return
     */
    public boolean isHaveTaskRunning() {
        for (PluginBean bean : mDownPluginList) {
            if (isTaskRunning(bean)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 开始全部下载
     */
    public void startAllDownLand() {
        for (PluginBean bean : mDownPluginList) {
            Aria.download(this).load(bean.getUrl()).start();
        }
    }

    /**
     * 取消下载
     */
    public void cancelDownLand(String url) {
        Aria.download(this)
                .load(url)
                .cancel(true);
    }

    /**
     * 将下载得查分包和本地得Apk合并
     */
    public void megerApkFile() {

    }

    /**
     * 安装插件Apk
     * 子线程安装插件
     */
    private void installPlugin(PluginBean bean) {
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            String apkMd5 = Md5Utils.getFileMD5(new File(bean.getSavePath()));
            L.i("file md5:" + apkMd5);
            L.i("bean md5:" + bean.getApkMd5());
            if (!bean.getApkMd5().equals(apkMd5)) {
                e.onNext(false);
                e.onComplete();
                return;
            }
            PluginInfo info = RePlugin.install(bean.getSavePath());
            if (info != null) {
                Aria.download(this).load(bean.getUrl()).removeRecord();
                e.onNext(true);
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result) {
                        mCallBack.installFinish(bean);
                    } else {
                        mCallBack.installError(bean);
                    }
                }, throwable -> mCallBack.installError(bean));
    }

    /**
     * 卸载插件Apk
     */
    public boolean unInstallPlugin(PluginBean bean, Context context) {
        if (RePlugin.isPluginInstalled(bean.getPluginName())) {
            //删除下载记录
            Aria.download(this).load(bean.getUrl()).removeRecord();
            Aria.download(this).load(bean.getUrl()).cancel(true);
            //删除保存在此插件File目录下得所有文件
            File file = context.getExternalFilesDir("benbaba" + File.separator + bean.getPluginName());
            if (file != null && file.exists()) {
                L.i("删除文件:" + bean.getPluginName());
                FileUtils.deleteFile(file);
            }
            return RePlugin.uninstall(bean.getPluginName());
        }
        return false;
    }

    /**
     * 释放资源
     */
    public void release() {
        Aria.download(this).unRegister();
        mDownPluginList.clear();
        mCallBack = null;
    }

//    /**
//     * 任务正在运行得时候
//     *
//     * @param task
//     */
//    @Download.onTaskPre
//    protected void taskPreStart(DownloadTask task) {
//        for (PluginBean bean2 : mDownPluginList) {
//            if (bean2.getUrl().equals(task.getKey())) {
//                bean2.getWaveDrawable().setLevel(task.getPercent() * 100);
//            }
//        }
//    }

    /**
     * 任务正在运行得时候
     *
     * @param task
     */
    @Download.onTaskRunning
    protected void taskRunning(DownloadTask task) {
        for (PluginBean bean2 : mDownPluginList) {
            if (bean2.getUrl().equals(task.getKey())) {
                bean2.getWaveDrawable().setLevel(task.getPercent() * 100);
            }
        }
    }

    /**
     * 任务完成
     *
     * @param task
     */
    @Download.onTaskComplete
    protected void taskComplete(DownloadTask task) {
        L.i("taskComplete:" + task.getTaskName());
        for (PluginBean bean : mDownPluginList) {
            if (bean.getUrl().equals(task.getKey())) {
                installPlugin(bean);
            }
        }
    }

    @Download.onWait
    protected void taskWait(DownloadTask task) {
        L.i("taskWait");
    }

    @Download.onTaskFail
    protected void taskError(DownloadTask task) {
        L.i("taskError:" + task.getTaskName());
        for (PluginBean bean : mDownPluginList) {
            if (bean.getUrl().equals(task.getKey())) {
                mCallBack.downLandError(bean);
            }
        }
    }

    /**
     * 插件得回调
     */
    public interface OnPluginManagerCallBack {
        void installFinish(PluginBean bean);//安装完成

        void installError(PluginBean bean);//下载失败

        void downLandError(PluginBean bean);
    }
}
