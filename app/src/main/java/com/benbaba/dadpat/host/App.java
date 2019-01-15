package com.benbaba.dadpat.host;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.arialyy.aria.core.Aria;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.utils.DensityUtil;
import com.benbaba.dadpat.host.utils.SPUtils;
import com.benbaba.dadpat.host.utils.Utils_CrashHandler;
import com.clj.fastble.BleManager;
import com.mob.MobSDK;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.RePluginApplication;
import com.qihoo360.replugin.RePluginCallbacks;
import com.qihoo360.replugin.RePluginConfig;
import com.qihoo360.replugin.RePluginEventCallbacks;
import com.zhihu.matisse.internal.ui.AlbumPreviewActivity;
import com.zhihu.matisse.internal.ui.SelectedPreviewActivity;
import com.zhihu.matisse.ui.ImageCropActivity;
import com.zhihu.matisse.ui.MatisseActivity;

import me.jessyan.autosize.AutoSizeConfig;


/**
 * APP得入口类
 * Created by Administrator on 2018/2/5.
 */
public class App extends RePluginApplication implements Application.ActivityLifecycleCallbacks {
    public static String token;
    private static Context mBaseContext;

    @Override
    public void onCreate() {
        super.onCreate();
        BleManager.getInstance().init(this);
        mBaseContext = getApplicationContext();
        createNotificationChannel();
        Logger.addLogAdapter(new AndroidLogAdapter());
        //注册Activity得监听
        registerActivityLifecycleCallbacks(this);
        //配置Aria下载框架
        Aria.get(getApplicationContext()).getDownloadConfig()
                .setMaxTaskNum(3)
                .setThreadNum(3);
        //配置SSL证书
        HttpManager.getInstance().setTrustCertificate(getResources().openRawResource(R.raw.dadpat));
        HttpManager.getInstance().init();
        //初始化MobSdk
        MobSDK.init(this);
        //crash崩溃日志输出到内存中
        Utils_CrashHandler.getInstance().init(this);
        token = (String) SPUtils.get(getApplicationContext(), Constants.SP_TOKEN, "");
        // 取消第三方图片选择器得适配
        AutoSizeConfig.getInstance().getExternalAdaptManager()
                .addCancelAdaptOfActivity(MatisseActivity.class)
                .addCancelAdaptOfActivity(AlbumPreviewActivity.class)
                .addCancelAdaptOfActivity(SelectedPreviewActivity.class)
                .addCancelAdaptOfActivity(ImageCropActivity.class);

        RePlugin.registerHookingClass("com.qihoo360.replugin.sample.demo1.fragment.DemoFragment",
                RePlugin.createComponentName("", "com.qihoo360.replugin.sample.demo1.fragment.DemoFragment"), null);
    }

    public static Context getContext() {
        return mBaseContext;
    }

    /**
     * 创建通知得渠道
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "app_update";
            String channelName = "APP更新";
            int importance = NotificationManager.IMPORTANCE_MIN; //静默
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);

            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 设置Token
     *
     * @param token
     */
    public void setToken(String token) {
        App.token = token;
        SPUtils.put(getApplicationContext(), Constants.SP_TOKEN, token);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        RePlugin.enableDebugger(base, BuildConfig.DEBUG);
        MultiDex.install(this);
    }

    @Override
    protected RePluginConfig createConfig() {
        RePluginConfig c = new RePluginConfig();
        //当安装成功的时候删除源文件
        c.setMoveFileWhenInstalling(true);
        // 允许“插件使用宿主类”。默认为“关闭”
        c.setUseHostClassIfNotFound(true);
        // FIXME RePlugin默认会对安装的外置插件进行签名校验，这里先关掉，避免调试时出现签名错误
//        c.setVerifySign(!BuildConfig.DEBUG);
        // 针对“安装失败”等情况来做进一步的事件处理
        c.setEventCallbacks(new HostEventCallbacks(this));
        // FIXME 若宿主为Release，则此处应加上您认为"合法"的插件的签名，例如，可以写上"宿主"自己的。
//        RePlugin.addCertSignature("9d86eaad935d24ebed33c6fb07446194");
        return c;
    }

    @Override
    protected RePluginCallbacks createCallbacks() {
        return new HostCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        AppManager.getAppManager().removeActivity(activity);
    }

    /**
     * 宿主针对RePlugin的自定义行为
     */
    private class HostCallbacks extends RePluginCallbacks {

        private static final String TAG = "HostCallbacks";

        private HostCallbacks(Context context) {
            super(context);
        }

        @Override
        public boolean onPluginNotExistsForActivity(Context context, String plugin, Intent intent, int process) {
            // FIXME 当插件"没有安装"时触发此逻辑，可打开您的"下载对话框"并开始下载。
            // FIXME 其中"intent"需传递到"对话框"内，这样可在下载完成后，打开这个插件的Activity
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onPluginNotExistsForActivity: Start download... p=" + plugin + "; i=" + intent);
            }
            return super.onPluginNotExistsForActivity(context, plugin, intent, process);
        }
    }

    /**
     * 插件启动的回掉
     */
    private class HostEventCallbacks extends RePluginEventCallbacks {

        private static final String TAG = "HostEventCallbacks";

        public HostEventCallbacks(Context context) {
            super(context);
        }

        @Override
        public void onInstallPluginFailed(String path, RePluginEventCallbacks.InstallResult code) {
            // FIXME 当插件安装失败时触发此逻辑。您可以在此处做“打点统计”，也可以针对安装失败情况做“特殊处理”
            // 大部分可以通过RePlugin.install的返回值来判断是否成功
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onInstallPluginFailed: Failed! path=" + path + "; r=" + code);
            }
            super.onInstallPluginFailed(path, code);
        }

        @Override
        public void onPrepareStartPitActivity(Context context, Intent intent, Intent pittedIntent) {
            Log.i("TAG", "onPrepareStartPitActivity");
            super.onPrepareStartPitActivity(context, intent, pittedIntent);
        }

        @Override
        public void onStartActivityCompleted(String plugin, String activity, boolean result) {
            super.onStartActivityCompleted(plugin, activity, result);
        }
    }
}
