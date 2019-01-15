package com.benbaba.dadpat.host.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.benbaba.dadpat.host.App;
import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseActivity;
import com.benbaba.dadpat.host.bean.TokenBean;
import com.benbaba.dadpat.host.bean.User;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.http.ApiException;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.http.entry.HttpResult;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.PhoneUtils;
import com.benbaba.dadpat.host.utils.SPUtils;
import com.benbaba.dadpat.host.utils.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

@SuppressLint("CheckResult")
public class RegisterActivity extends BaseActivity {
    @BindView(R.id.id_register_phone)
    EditText mRegisterPhone;
    @BindView(R.id.id_register_phone_code)
    EditText mRegisterPhoneCode;
    @BindView(R.id.id_register_send_phone_code)
    TextView mRegisterSendPhoneCode;
    @BindView(R.id.id_register_psd)
    EditText mRegisterPsd;
    private int mCount;//计数

    /**
     * 短信Sdk的回调事件
     */
    private EventHandler mEventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            runOnUiThread(() -> {
                DialogFactory.dismissLoadingDialog(RegisterActivity.this);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    toast("发送验证码成功");
                } else {
                    toast("发送验证码失败");
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        //注册监听
        SMSSDK.registerEventHandler(mEventHandler);
    }

    @OnClick({R.id.id_register_send_phone_code, R.id.id_register_btn, R.id.id_register_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_register_back:
                RegisterActivity.this.finish();
                break;
            case R.id.id_register_send_phone_code: {
                String phone = mRegisterPhone.getText().toString().trim();
                if (!PhoneUtils.isMobile(phone)) {
                    ToastUtils.showShortToast(RegisterActivity.this, "请输入合法手机号");
                    return;
                }
                mCount = 60;
                DialogFactory.showLoadingDialog(RegisterActivity.this);
                sendPhoneCode(phone);
                break;
            }
            case R.id.id_register_btn: {
                String phone = mRegisterPhone.getText().toString().trim();
                String psd = mRegisterPsd.getText().toString().trim();
                String code = mRegisterPhoneCode.getText().toString().trim();
                if (!PhoneUtils.isMobile(phone)) {
                    ToastUtils.showShortToast(RegisterActivity.this, "请输入合法手机号");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    toast("验证码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(psd)) {
                    toast("密码不能为空");
                    return;
                }
                register(phone, code, psd);
                break;
            }

        }
    }

    /**
     * 发送手机验证码
     */
    private void sendPhoneCode(String phone) {
        Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .take(mCount + 1) //
                .doOnSubscribe(disposable -> {
                    SMSSDK.getVerificationCode("86", phone);
                    mRegisterSendPhoneCode.setTextColor(getResources().getColor(R.color.white_42));
                    mRegisterSendPhoneCode.setEnabled(false);//在发送数据的时候设置为不能点击
                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(aLong -> {
                    mCount--;
                    mRegisterSendPhoneCode.setText(String.valueOf(mCount));
                }, throwable -> {
                }, () -> {
                    mRegisterSendPhoneCode.setEnabled(true);
                    mRegisterSendPhoneCode.setTextColor(getResources().getColor(R.color.white));
                    mRegisterSendPhoneCode.setText(getResources().getString(R.string.send_phone_code_retry));//数据发送完后设置为原来的文字
                });
    }

    /**
     * 注册
     */
    private void register(String phone, String code, String psd) {
        Map<String, String> params = new HashMap<>();
        params.put("userMobile", phone);
        params.put("smsCode", code);
        //验证短信接口
        HttpManager.getInstance().verifySms(params)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .doOnSubscribe(disposable -> DialogFactory.showLoadingDialog(RegisterActivity.this))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> DialogFactory.dismissLoadingDialog(RegisterActivity.this))
                .flatMap((Function<HttpResult, ObservableSource<User>>) httpResult -> {
                    if (httpResult.isSuccess()) {
                        params.clear();
                        params.put("authCode", phone);
                        params.put("authType", "mobile");
                        params.put("authCredential", psd);
                        params.put("userType", "DEFAULT_USER");
                        //注册
                        return HttpManager.getInstance().register(params);
                    } else {
                        return Observable.error(new ApiException(ApiException.CODE_SMS_CODE_ERROR));
                    }
                })
                //jwt登陆获取token
                .flatMap((Function<User, ObservableSource<TokenBean>>) user -> {
                    L.i("register:" + user.toString());
                    Map<String, String> _params = new HashMap<>();
                    _params.put("userMobile", phone);
                    _params.put("userPwd", psd);
                    return HttpManager.getInstance().doLogin(_params);
                })
                .subscribe(tokenBean -> {
                    App app = (App) getApplication();
                    app.setToken(tokenBean.getToken());
                    SPUtils.put(RegisterActivity.this.getApplicationContext(), Constants.SP_LOGIN_TYPE, "phone");
                    SPUtils.put(RegisterActivity.this.getApplicationContext(), Constants.SP_LOGIN, true);
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("User", tokenBean.getUser());
                    startActivity(intent);
                    ToastUtils.showShortToast(RegisterActivity.this, "注册成功");
                    RegisterActivity.this.finish();
                }, throwable -> {
                    L.i("error:" + throwable.getLocalizedMessage());
                    if (throwable instanceof ApiException) {
                        ApiException exception = (ApiException) throwable;
                        ToastUtils.showShortToast(RegisterActivity.this, exception.getMessage());
                    }
                });
    }
}
