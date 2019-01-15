package com.benbaba.dadpat.host.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.bean.PluginBean;
import com.benbaba.dadpat.host.http.downland.DownLandProgressListener;
import com.benbaba.dadpat.host.http.downland.DownlandManager;
import com.benbaba.dadpat.host.utils.FileUtils;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.Md5Utils;
import com.benbaba.dadpat.host.utils.ToastUtils;
import com.zhihu.matisse.compress.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 后台APP下载更新
 * 通知栏显示下载进度，下载完成弹出安装界面
 * Created by Administrator on 2018/9/23.
 */
@SuppressWarnings("CheckResult")
public class AppUpdateService2 extends Service {
    private static final String APP_DOWNLAND_NAME = "dadpat.apk";
    private static final int NOTIFICATION_ID = UUID.randomUUID().hashCode();
    private PluginBean mAppBean;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;
    private DownlandManager mDownLandManager;
    private int curPercent;

    /**
     * 下载进度显示
     */
    private DownLandProgressListener mListener = (bytesRead, contentLength, done) -> {
        int percent = (int) ((bytesRead * 1.0f / contentLength) * 100);
        if (curPercent != percent) {
            mBuilder.setProgress(100, curPercent = percent, false);
            mBuilder.setContentText(FileUtils.formatFileSize(bytesRead) + "/" + FileUtils.formatFileSize(contentLength));
            mManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        createNotification();
        mDownLandManager = new DownlandManager(mListener);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.i("onStartCommand：");
        mAppBean = intent.getParcelableExtra("AppBean");
        mDownLandManager.downland(mAppBean.getUrl())
                .map(inputStream -> {
                    //删除下载得文件
                    FileUtils.deleteFile(new File(Constants.APP_SAVE_DIR, APP_DOWNLAND_NAME));
                    boolean result = FileUtils.saveFile(Constants.APP_SAVE_DIR, APP_DOWNLAND_NAME, inputStream);
                    if (result) {
                        L.i("File MD5:" + Md5Utils.getFileMD5(new File(Constants.APP_SAVE_DIR, APP_DOWNLAND_NAME)));
                        L.i("Bean MD5:" + mAppBean.getApkMd5());
                        result = mAppBean.getApkMd5().equals(Md5Utils.getFileMD5(new File(Constants.APP_SAVE_DIR, APP_DOWNLAND_NAME)));
                    }
                    return result;
                })
                .subscribe(result -> {
                            if (result) {
                                //自动安装
                                installApk();
                                stopSelf();
                            } else {
                                downLandError();
                            }
                        },
                        throwable -> {
                            L.i("下载失败：" + throwable.getLocalizedMessage());
                            downLandError();
                        });
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 下载失败
     */
    private void downLandError() {
        Intent intent_retry = new Intent(this, AppUpdateService2.class);
        intent_retry.putExtra("AppBean", mAppBean);
        mBuilder.setContentText("下载失败,点击重新下载");
        PendingIntent mIntent = PendingIntent.getService(getApplicationContext(), 0,
                intent_retry, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(mIntent);
        mManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * 创建通知
     */
    private void createNotification() {
        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(this, "app_update")
                    .setContentTitle("App更新")
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.dadpat_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.dadpat_logo))
                    .setAutoCancel(true);
        }
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    /**
     * 安装APK
     */
    public void installApk() {
        startActivity(getInstallIntent());
    }

    /**
     * 获取安装得Intent
     *
     * @return
     */
    private Intent getInstallIntent() {
        File file = new File(Constants.APP_SAVE_DIR, APP_DOWNLAND_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), "com.benbaba.dadpat.host.fileprovider", file);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        return intent;
    }


    /**
     * 删除安装包
     */
    private void deleteApk() {
//        File file = new File(APP_DOWNLAND_PATH);
//        if (file.exists()) {
//            if (file.delete()) {
//                ToastUtils.showShortToast(getApplicationContext(), "删除安装包成功");
//            }
//        }
    }


}
