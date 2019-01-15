package com.benbaba.dadpat.host.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;


import com.benbaba.dadpat.host.App;
import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseActivity;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.http.ApiException;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.utils.PhoneUtils;
import com.benbaba.dadpat.host.utils.SPUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 手机登录得activity
 */
@SuppressLint("CheckResult")
public class PhoneLoginActivity extends BaseActivity {

    @BindView(R.id.id_login_phone)
    EditText mLoginPhone;
    @BindView(R.id.id_login_psd)
    EditText mLoginPsd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.id_phone_register, R.id.id_forget_psd, R.id.id_login_btn, R.id.id_phone_login_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_phone_login_back:
                PhoneLoginActivity.this.finish();
                break;
            case R.id.id_phone_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.id_forget_psd:// 忘记密码
                String phone = mLoginPhone.getText().toString().trim();
                Intent intent = new Intent(PhoneLoginActivity.this, ForgetPsdActivity.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
                break;
            case R.id.id_login_btn:
                String mobile = mLoginPhone.getText().toString();
                String psd = mLoginPsd.getText().toString();
                if (!PhoneUtils.isMobile(mobile)) {
                    toast("手机号不合法");
                    return;
                }
                if (TextUtils.isEmpty(psd)) {
                    toast("密码不能为空");
                    return;
                }
                login(mobile, psd);
                break;
        }
    }

    /**
     * 登录
     *
     * @param userMobile
     * @param passWord
     */
    private void login(String userMobile, String passWord) {
        Map<String, String> params = new HashMap<>();
        params.put("userMobile", userMobile);
        params.put("userPwd", passWord);
        HttpManager.getInstance().doLogin(params)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .doOnSubscribe(disposable -> DialogFactory.showLoadingDialog(PhoneLoginActivity.this))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> DialogFactory.dismissLoadingDialog(PhoneLoginActivity.this))
                .subscribe(tokenBean -> {
                            App app = (App) getApplication();
                            app.setToken(tokenBean.getToken());
                            SPUtils.put(PhoneLoginActivity.this.getApplicationContext(), Constants.SP_LOGIN, true);
                            SPUtils.put(PhoneLoginActivity.this.getApplicationContext(), Constants.SP_LOGIN_TYPE, "phone");
                            Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
                            intent.putExtra("User", tokenBean.getUser());
                            startActivity(intent);
                            PhoneLoginActivity.this.finish();
                        },
                        throwable -> {
                            if (throwable instanceof ApiException) {
                                ApiException exception = (ApiException) throwable;
                                toast(exception.getMessage());
                            }
                        });
    }
}
