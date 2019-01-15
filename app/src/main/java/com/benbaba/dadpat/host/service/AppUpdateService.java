package com.benbaba.dadpat.host.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.bean.PluginBean;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.utils.FileUtils;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.Md5Utils;
import com.benbaba.dadpat.host.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 后台APP下载更新
 * 通知栏显示下载进度，下载完成弹出安装界面
 * Created by Administrator on 2018/9/23.
 */
public class AppUpdateService extends Service {
    private static final String APP_DOWNLAND_PATH = Constants.APP_SAVE_DIR + File.separator + String.valueOf(System.currentTimeMillis() + "_dadpat.apk");
    private static final int NOTIFICATION_ID = UUID.randomUUID().hashCode();
    private PluginBean mAppBean;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Aria.download(this).register();
        createNotification();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Aria.download(this).unRegister();
        mManager.cancel(1);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAppBean = intent.getParcelableExtra("AppBean");
        //删除文件夹下所有文件
        FileUtils.deleteDirFiles(new File(Constants.APP_SAVE_DIR));
        Aria.download(this)
                .load(mAppBean.getUrl())
                .setFilePath(APP_DOWNLAND_PATH)
                .start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 创建通知
     */
    private void createNotification() {
        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(this, "app_update")
                    .setContentTitle("App更新")
                    .setDefaults(NotificationCompat.STREAM_DEFAULT)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.dadpat_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.dadpat_logo))
                    .setAutoCancel(true);
        }
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Download.onTaskRunning
    protected void onTaskRunning(DownloadTask task) {
        if (task.getKey().equals(mAppBean.getUrl())) {
            mBuilder.setContentText(String.valueOf(task.getConvertCurrentProgress() + "/" + task.getConvertFileSize()))
                    .setProgress(100, task.getPercent(), false);
            mManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    @Download.onTaskComplete
    protected void onTaskComplete(DownloadTask task) {
        if (task.getKey().equals(mAppBean.getUrl())) {
            File file = new File(task.getDownloadPath());
            L.i("APPBean MD5:" + mAppBean.getApkMd5());
            L.i("APPFile MD5:" + Md5Utils.getFileMD5(file));
            if (mAppBean.getApkMd5().equals(Md5Utils.getFileMD5(file))) {
                mBuilder.setContentText("下载完成开始安装").setProgress(100, 100, false);
                mManager.notify(NOTIFICATION_ID, mBuilder.build());
                //如果MD5相等得时候安装APK
                installApk(getApplicationContext(), task.getDownloadPath());
            } else {
                Aria.download(this).load(mAppBean.getUrl()).cancel(true);
                mBuilder.setContentText("下载失败");
                mManager.notify(NOTIFICATION_ID, mBuilder.build());
            }

        }
    }

    @Download.onTaskFail
    protected void onTaskFail(DownloadTask task) {
        if (task.getKey().equals(mAppBean.getUrl())) {
            Aria.download(this).load(mAppBean.getUrl()).cancel(true);
            mBuilder.setContentText("下载失败");
            mManager.notify(NOTIFICATION_ID, mBuilder.build());
            Aria.download(this).load(mAppBean.getUrl()).setFilePath(APP_DOWNLAND_PATH).start();
        }
    }

    /**
     * 安装APK
     *
     * @param context
     * @param apkPath
     */
    public void installApk(Context context, String apkPath) {
        if (context == null || TextUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, "com.benbaba.dadpat.host.fileprovider", file);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    /**
     * 删除安装包
     */
    private void deleteApk() {
        File file = new File(APP_DOWNLAND_PATH);
        if (file.exists()) {
            if (file.delete()) {
                ToastUtils.showShortToast(getApplicationContext(), "删除安装包成功");
            }
        }
    }

}
