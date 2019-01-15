package com.benbaba.dadpat.host.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.benbaba.dadpat.host.AppManager;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseActivity;
import com.benbaba.dadpat.host.bean.User;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.http.ApiException;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.http.entry.HttpResult;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.PhoneUtils;
import com.benbaba.dadpat.host.utils.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 忘记密码
 */
@SuppressLint("CheckResult")
public class ForgetPsdActivity extends BaseActivity {
    private static final int SEND_PHONE_CODE_SUCCESS = 0x01;
    private static final int SEND_PHONE_CODE_ERROR = 0x02;
    private static final int VERIFY_PHONE_CODE_SUCCESS = 0x03;
    private static final int VERIFY_PHONE_CODE_ERROR = 0x04;
    private static final int MODIFY_PSD_SUCCESS = 0x05;
    private static final int MODIFY_PSD_ERROR = 0x06;
    @BindView(R.id.id_forget_psd_phone)
    EditText mPhone;
    @BindView(R.id.id_forget_psd_code)
    EditText mCode;
    @BindView(R.id.id_forget_psd_send_code)
    TextView mSendCode;
    @BindView(R.id.id_forget_psd_new_psd)
    EditText mNewPsd;
    private int mCount;//计数

    private EventHandler mEventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            runOnUiThread(() -> {
                DialogFactory.dismissLoadingDialog(ForgetPsdActivity.this);
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
        setContentView(R.layout.activity_forget_psd);
        ButterKnife.bind(this);
        //注册监听
        SMSSDK.registerEventHandler(mEventHandler);
    }

    @OnClick({R.id.id_forget_psd_send_code, R.id.id_forget_psd_confirm_btn, R.id.id_forget_psd_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_forget_psd_back:
                ForgetPsdActivity.this.finish();
                break;
            case R.id.id_forget_psd_send_code: {
                String phone = mPhone.getText().toString().trim();
                if (!PhoneUtils.isMobile(phone)) {
                    toast("请输入合法手机号");
                    return;
                }
                mCount = 60;
                DialogFactory.showLoadingDialog(ForgetPsdActivity.this);
                sendPhoneCode(phone);
                break;
            }
            case R.id.id_forget_psd_confirm_btn:
                String phone = mPhone.getText().toString().trim();
                String psd = mNewPsd.getText().toString().trim();
                String code = mCode.getText().toString().trim();
                if (!PhoneUtils.isMobile(phone)) {
                    toast("请输入合法手机号");
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
                modifyPsd(phone, psd, code);
                break;
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
                    mSendCode.setTextColor(getResources().getColor(R.color.white_42));
                    mSendCode.setEnabled(false);//在发送数据的时候设置为不能点击
                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(aLong -> {
                            mCount--;
                            mSendCode.setText(String.valueOf(mCount));
                        },
                        throwable -> L.i("throwable:" + throwable.getLocalizedMessage()),
                        () -> {
                            mSendCode.setEnabled(true);
                            mSendCode.setTextColor(getResources().getColor(R.color.white));
                            mSendCode.setText(getResources().getString(R.string.send_phone_code_retry));//数据发送完后设置为原来的文字
                        });
    }

    /**
     * 修改密码
     */
    private void modifyPsd(String phone, String psd, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("userMobile", phone);
        params.put("userPwd", psd);
        params.put("smsCode", code);
        HttpManager.getInstance().modifyPsd(params)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .doOnSubscribe(disposable -> DialogFactory.showLoadingDialog(ForgetPsdActivity.this))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> DialogFactory.dismissLoadingDialog(ForgetPsdActivity.this))
                .subscribe(httpResult -> {
                    if (httpResult.isSuccess()) {
                        toast("修改密码成功");
                        ForgetPsdActivity.this.finish();
                    } else {
                        toast("修改密码失败:" + httpResult.getMsg());
                    }
                }, throwable -> {
                    if (throwable instanceof ApiException) {
                        ApiException exception = (ApiException) throwable;
                        toast(exception.getMessage());
                    }
                });

//        HttpManager.getInstance().modifyUserPsd(params)
//                .compose(bindUntilEvent(ActivityEvent.DESTROY))
//                .subscribe(httpResult -> {
//                    if (httpResult.isSuccess()) {
//                        L.i("修改密码成功");
//                        ToastUtils.showShortToast(ForgetPsdActivity.this, "修改密码成功");
//                        ForgetPsdActivity.this.finish();
//                    } else {
//                        L.i("修改密码失败" + httpResult.getMsg());
//                        ToastUtils.showShortToast(ForgetPsdActivity.this, "修改密码失败");
//                    }
//                });
    }
}
