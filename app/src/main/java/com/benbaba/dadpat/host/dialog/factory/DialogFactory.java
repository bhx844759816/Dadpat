package com.benbaba.dadpat.host.dialog.factory;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import com.benbaba.dadpat.host.callback.OnNetWorkRemindCallBack;
import com.benbaba.dadpat.host.dialog.AppUpdateDialogFragment;
import com.benbaba.dadpat.host.dialog.BlueToothSettingDialogFragment;
import com.benbaba.dadpat.host.dialog.BluetoothListDialogFragment;
import com.benbaba.dadpat.host.dialog.SongListDialogFragment;
import com.benbaba.dadpat.host.dialog.UserProtocolDialogFragment;
import com.benbaba.dadpat.host.dialog.FeedBackDialogFragment;
import com.benbaba.dadpat.host.dialog.LoadingDialogFragment;
import com.benbaba.dadpat.host.dialog.MessageDialogFragment;
import com.benbaba.dadpat.host.dialog.NetWorkDialogFragment;
import com.benbaba.dadpat.host.dialog.PerInfoDialogFragment;
import com.benbaba.dadpat.host.dialog.PromptDialogFragment;
import com.benbaba.dadpat.host.dialog.ReStartAPPDialogFragment;
import com.benbaba.dadpat.host.dialog.SettingDialogFragment;
import com.benbaba.dadpat.host.dialog.WifiSettingPromptDialogFragment;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.PermissionPageUtils;

public class DialogFactory {
    private static final String TAG_PER_INFO = "tag_per_info";
    private static final String TAG_MESSAGE = "tag_message";
    private static final String TAG_SETTING = "tag_setting";
    private static final String TAG_BUFFER = "tag_buffer";
    private static final String TAG_LOADING = "tag_loading";
    private static final String TAG_NETWORK = "tag_network";
    private static final String TAG_PROMPT = "tag_prompt";
    private static final String TAG_RESTART_APP = "tag_reStart_App";
    private static final String TAG_UPDATE_APP = "tag_update_App";
    private static final String TAG_FEEDBACK = "tag_feedback";
    private static final String TAG_WIFI_SETTING_PROMPT = "tag_wifi_setting_prompt";
    private static final String TAG_BLUE_TOOTH_LIST = "tag_blue_tooth_list";
    private static final String TAG_BLUE_TOOTH_SETTING = "tag_blue_tooth_setting";
    private static final String TAG_SONG_LIST = "tag_song_list";


    /**
     * 加载个人信息对话框
     *
     * @param activity
     */
    public static PerInfoDialogFragment showPerInfoDialog(FragmentActivity activity) {
        PerInfoDialogFragment fragment = (PerInfoDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_PER_INFO);
        if (fragment == null) {
            fragment = new PerInfoDialogFragment();
            L.i("PerInfoDialogFragment instance");
        }
        show(activity, fragment, TAG_PER_INFO);
        return fragment;


    }

    /**
     * 展示消息和公告的对话框
     *
     * @param activity
     */
    public static MessageDialogFragment showMessageDialog(FragmentActivity activity) {
        MessageDialogFragment fragment = (MessageDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_MESSAGE);
        if (fragment == null) {
            fragment = new MessageDialogFragment();
        }
        show(activity, fragment, TAG_MESSAGE);
        return fragment;
    }

    /**
     * 展示缓存对话框
     *
     * @param activity
     */
    public static void showUserProtocolDialog(FragmentActivity activity) {
        UserProtocolDialogFragment fragment = (UserProtocolDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_BUFFER);
        if (fragment == null) {
            fragment = new UserProtocolDialogFragment();
        }
        show(activity, fragment, TAG_BUFFER);
    }

    /**
     * 展示游戏设置的Dialog
     *
     * @param activity
     */
    public static void showSettingDialog(FragmentActivity activity) {
        SettingDialogFragment fragment = (SettingDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_SETTING);
        if (fragment == null) {
            fragment = new SettingDialogFragment();
        }
        show(activity, fragment, TAG_SETTING);
    }

    /**
     * 展示LoadingDialog
     *
     * @param activity
     * @return
     */
    public static LoadingDialogFragment showLoadingDialog(FragmentActivity activity) {
        LoadingDialogFragment fragment = (LoadingDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_LOADING);
        if (fragment == null) {
            fragment = new LoadingDialogFragment();
        }
        show(activity, fragment, TAG_LOADING);
        return fragment;
    }

    public static void dismissLoadingDialog(FragmentActivity activity) {
        dismiss(activity, TAG_LOADING);
    }

    /**
     * 展示网络提醒得Dialog
     *
     * @param activity
     * @return
     */
    public static NetWorkDialogFragment showNetWorkRemindDialog(FragmentActivity activity) {
        NetWorkDialogFragment fragment = (NetWorkDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_NETWORK);
        if (fragment == null) {
            fragment = new NetWorkDialogFragment();
        }
        show(activity, fragment, TAG_NETWORK);
        return fragment;
    }

    /**
     * 取消网络提醒得Dialog
     *
     * @param activity
     */
    public static void dismissNetWorkRemindDialog(FragmentActivity activity) {
        dismiss(activity, TAG_NETWORK);
    }

    /**
     * 展示确认对话框
     *
     * @param activity
     * @return
     */
    public static PromptDialogFragment showPromptDialog(FragmentActivity activity) {
        PromptDialogFragment fragment = (PromptDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_PROMPT);
        if (fragment == null) {
            fragment = new PromptDialogFragment();
        }
        show(activity, fragment, TAG_PROMPT);
        return fragment;
    }

    /**
     * 展示蓝牙列表得Dialog
     *
     * @param activity
     */
    public static void showBlueToothListDialog(FragmentActivity activity) {
        BluetoothListDialogFragment fragment = (BluetoothListDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_BLUE_TOOTH_LIST);
        if (fragment == null) {
            fragment = new BluetoothListDialogFragment();
        }
        show(activity, fragment, TAG_BLUE_TOOTH_LIST);
    }

    /**
     * 展示设置蓝牙得提示对话框
     *
     * @param activity
     */
    public static void showBlueToothSettingDialog(FragmentActivity activity) {
        BlueToothSettingDialogFragment fragment = (BlueToothSettingDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_BLUE_TOOTH_SETTING);
        if (fragment == null) {
            fragment = new BlueToothSettingDialogFragment();
        }
        show(activity, fragment, TAG_BLUE_TOOTH_SETTING);
    }

    /**
     * 展示歌单列表得Dialog
     *
     * @param activity
     */
    public static void showSongListDialog(FragmentActivity activity) {
        SongListDialogFragment fragment = (SongListDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_SONG_LIST);
        if (fragment == null) {
            fragment = new SongListDialogFragment();
        }
        show(activity, fragment, TAG_SONG_LIST);
    }

    /**
     * 展示重新启动App得Dialog
     *
     * @param activity
     */
    public static void showReStartAPPDialog(FragmentActivity activity) {
        ReStartAPPDialogFragment fragment = (ReStartAPPDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_RESTART_APP);
        if (fragment == null) {
            fragment = new ReStartAPPDialogFragment();
        }
        show(activity, fragment, TAG_RESTART_APP);
    }

    /**
     * 展示更新得Dialog
     *
     * @param activity
     */
    public static AppUpdateDialogFragment showAppUpdateDialog(FragmentActivity activity) {
        AppUpdateDialogFragment fragment = (AppUpdateDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_UPDATE_APP);
        if (fragment == null) {
            fragment = new AppUpdateDialogFragment();
        }
        show(activity, fragment, TAG_UPDATE_APP);
        return fragment;
    }

    /**
     * 展示反馈意见得对话框
     *
     * @param activity
     * @return
     */
    public static FeedBackDialogFragment showFeedBackDialog(FragmentActivity activity) {
        FeedBackDialogFragment fragment = (FeedBackDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_FEEDBACK);
        if (fragment == null) {
            fragment = new FeedBackDialogFragment();
        }
        show(activity, fragment, TAG_FEEDBACK);
        return fragment;
    }

    /**
     * 展示配置设备wifi得帮助对话框
     *
     * @param activity
     */
    public static void showWifiSettingPromptDialog(FragmentActivity activity) {
        WifiSettingPromptDialogFragment fragment = (WifiSettingPromptDialogFragment)
                activity.getSupportFragmentManager().findFragmentByTag(TAG_WIFI_SETTING_PROMPT);
        if (fragment == null) {
            fragment = new WifiSettingPromptDialogFragment();
        }
        show(activity, fragment, TAG_WIFI_SETTING_PROMPT);
    }

    /**
     * 跳转到权限设置界面
     *
     * @param context
     */
    public static void showPermissionRefuseDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("跳转到权限设置界面")
                .setCancelable(false)
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    PermissionPageUtils.getInstance(context).jumpPermissionPage();
                    dialog.dismiss();
                }).show();
    }

    /**
     * 允许丢失状态得提交
     *
     * @param activity
     * @param fragment
     * @param tag
     */
    private static void show(FragmentActivity activity, DialogFragment fragment, String tag) {
        if (!fragment.isAdded()) {
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(fragment, tag);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 允许丢失状态得移除对话框
     *
     * @param activity
     * @param tag
     */
    private static void dismiss(FragmentActivity activity, String tag) {
        DialogFragment fragment = (DialogFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null && fragment.isAdded()) {
            fragment.dismissAllowingStateLoss();
        }
    }
}


