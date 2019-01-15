package com.benbaba.dadpat.host.utils;


import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 分享和第三方登陆
 */
public class ShareUtils {

    /**
     * 分享到QQ
     *
     * @param url
     * @param listener
     */
    public static void shareQQ(String title, String text, String url, PlatformActionListener listener) {
        shareUrl(title, text, url, QQ.NAME, listener);
    }

    public static void shareQQ(String title, String text, String url) {
        shareUrl(title, text, url, QQ.NAME, null);
    }

    /**
     * 分享到QQ空间
     *
     * @param text     分享的标题
     * @param title    分享的文本
     * @param url      分享的链接
     * @param listener 分享的回调
     */
    public static void shareQZone(String title, String text, String url, PlatformActionListener listener) {
        shareUrl(title, text, url, QZone.NAME, listener);
    }

    public static void shareQZone(String title, String text, String url) {
        shareUrl(title, text, url, SinaWeibo.NAME, null);
    }

    /**
     * 分享到微信
     *
     * @param text     分享的标题
     * @param title    分享的文本
     * @param url      分享的链接
     * @param listener 分享的回调
     */
    public static void shareWechat(String title, String text, String url, PlatformActionListener listener) {
        shareUrl(title, text, url, Wechat.NAME, listener);
    }

    public static void shareWechat(String title, String text, String url) {
        shareUrl(title, text, url, Wechat.NAME, null);
    }

    /**
     * 分享到微信朋友圈
     *
     * @param text     分享的标题
     * @param title    分享的文本
     * @param url      分享的链接
     * @param listener 分享的回调
     */
    public static void shareWechatMoments(String title, String text, String url, PlatformActionListener listener) {
        shareUrl(title, text, url, WechatMoments.NAME, listener);
    }

    public static void shareWechatMoments(String title, String text, String url) {
        shareUrl(title, text, url, Wechat.NAME, null);
    }

    /**
     * 分享到新浪微博
     *
     * @param title    分享的标题
     * @param text     分享的文本
     * @param url      分享的链接
     * @param listener 分享的回调
     */
    public static void shareSinaWeb(String title, String text, String url, PlatformActionListener listener) {
        shareUrl(title, text, url, SinaWeibo.NAME, listener);
    }

    public static void shareSinaWeb(String title, String text, String url) {
        shareUrl(title, text, url, SinaWeibo.NAME, null);
    }

    /**
     * 分享链接
     *
     * @param title    分享的标题
     * @param text     分享的文本
     * @param url      分享的链接
     * @param platform 分享的平台
     * @param listener 分享的回调
     */
    private static void shareUrl(String title, String text, String url, String platform, PlatformActionListener listener) {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle(title);
        sp.setTitleUrl(url);
        sp.setText(text);
        sp.setUrl(url);
        Platform plt = ShareSDK.getPlatform(platform);
        plt.setPlatformActionListener(listener); // 设置分享事件回调
        plt.share(sp);
    }

}
