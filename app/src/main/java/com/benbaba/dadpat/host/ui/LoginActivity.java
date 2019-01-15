package com.benbaba.dadpat.host.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.benbaba.dadpat.host.App;
import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseActivity;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.http.ApiException;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.NetUtils;
import com.benbaba.dadpat.host.utils.SPUtils;
import com.benbaba.dadpat.host.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import io.reactivex.android.schedulers.AndroidSchedulers;
//                ShareUtils.shareWechat("分享游戏","分享游戏连接","http://www.dadpat.com/share/link/1110",new PlatformActionListener(){
//
//                    @Override
//                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//
//                    }
//
//                    @Override
//                    public void onError(Platform platform, int i, Throwable throwable) {
//
//                    }
//
//                    @Override
//                    public void onCancel(Platform platform, int i) {
// }
//                });

/**
 * 登陆的Activity
 */
@SuppressWarnings("checkresult")
public class LoginActivity extends BaseActivity {

    /**
     * 第三方登陆授权回调
     */
    PlatformActionListener mListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
            L.i("doLoginThird onComplete:");
            Map<String, Object> params = new HashMap<>();
            if (platform.getName().equals(Wechat.NAME)) {
                params.put("token", platform.getDb().getToken());
                params.put("refresh_token", platform.getDb().get("refresh_token"));
                params.put("expiresIn", platform.getDb().getExpiresIn());
                params.put("nickname", platform.getDb().get("nickname"));
                params.put("openid", platform.getDb().get("openid"));
                params.put("unionid", platform.getDb().get("unionid"));
                L.i("openId:" + platform.getDb().get("openid"));
                doLoginThird("weixin", params.toString());
            } else if (platform.getName().equals(SinaWeibo.NAME)) {
                params.put("userID", platform.getDb().getUserId());
                params.put("token", platform.getDb().getToken());
                params.put("refresh_token", platform.getDb().get("refresh_token"));
                params.put("expiresIn", platform.getDb().getExpiresIn());
                params.put("expiresTime", platform.getDb().getExpiresTime());
                //TODO 等待审核通过
                doLoginThird("weibo", params.toString());
            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            runOnUiThread(() -> toast("访问第三方客户端失败"));
        }

        @Override
        public void onCancel(Platform platform, int i) {
            runOnUiThread(() -> toast("访问第三方客户端失败"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.id_login_wx, R.id.id_login_wb, R.id.id_login_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_login_wx:
                if (NetUtils.isNetworkConnected(this)) {
                    doAuthorize(Wechat.NAME);
                } else {
                    toast("网络连接失败，请检查网络连接");
                }
                break;
            case R.id.id_login_wb:
                if (NetUtils.isNetworkConnected(this)) {
                    doAuthorize(SinaWeibo.NAME);
                } else {
                    toast("网络连接失败，请检查网络连接");
                }
                break;
            case R.id.id_login_phone:
                startActivity(new Intent(this, PhoneLoginActivity.class));
                break;
        }
    }

    /**
     * 授权第三方登陆
     *
     * @param platform
     */
    private void doAuthorize(String platform) {
        Platform plt = ShareSDK.getPlatform(platform);
        plt.setPlatformActionListener(mListener);
        plt.SSOSetting(false); //SSO授权，传false默认是客户端授权，没有客户端授权或者不支持客户端授权会跳web授权
        if (!plt.isClientValid()) {
            if (platform.equals(SinaWeibo.NAME)) {
                ToastUtils.showShortToast(this, "无新浪客户端");
            } else if (platform.equals(Wechat.NAME)) {
                ToastUtils.showShortToast(this, "无微信客户端");
            }
        }
        if (plt.isAuthValid()) {
            plt.removeAccount(true);
        }
        plt.showUser(null);
//        plt.authorize();
    }

    /**
     * 第三方登陆
     *
     * @param accessType 表示是微信登陆(weixin) 或者微博登陆（weibo）
     */
    private void doLoginThird(String accessType, String data) {
        HttpManager.getInstance().loginThird(accessType, data)
                .doOnSubscribe(disposable -> DialogFactory.showLoadingDialog(LoginActivity.this))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> DialogFactory.dismissLoadingDialog(LoginActivity.this))
                .subscribe(thirdLoginBean -> {
                    App app = (App) getApplication();
                    app.setToken(thirdLoginBean.getToken());
                    SPUtils.put(LoginActivity.this.getApplicationContext(), Constants.SP_LOGIN_TYPE, accessType);
                    SPUtils.put(LoginActivity.this.getApplicationContext(), Constants.SP_LOGIN, true);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("User", thirdLoginBean.getUser());
                    startActivity(intent);
                    LoginActivity.this.finish();
                }, throwable -> {
                    if (throwable instanceof ApiException) {
                        ApiException exception = (ApiException) throwable;
                        toast(exception.getMessage());
                    }
                });
    }
}
