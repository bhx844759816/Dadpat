package com.benbaba.dadpat.host.http;


import com.benbaba.dadpat.host.App;
import com.benbaba.dadpat.host.bean.NoticeBean;
import com.benbaba.dadpat.host.bean.PluginBean;
import com.benbaba.dadpat.host.bean.ThirdLoginBean;
import com.benbaba.dadpat.host.bean.TokenBean;
import com.benbaba.dadpat.host.bean.User;
import com.benbaba.dadpat.host.http.entry.HttpResponseFunc;
import com.benbaba.dadpat.host.http.entry.HttpResult;
import com.benbaba.dadpat.host.http.entry.RxHelper;
import com.benbaba.dadpat.host.http.interceptor.NetCacheInterceptor;
import com.benbaba.dadpat.host.http.interceptor.LoggingInterceptor;
import com.benbaba.dadpat.host.http.interceptor.OfflineCacheInterceptor;
import com.benbaba.dadpat.host.http.interceptor.TokenInterceptor;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/12/18.
 */
public class HttpManager {
    public static final String BASE_URL = "https://www.goofypapa.com";
    private static HttpManager mInstance;
    private OkHttpClient sOkHttpClient;
    private ApiService mService;
    private InputStream mTrustrCertificate;

    public static HttpManager getInstance() {
        if (mInstance == null) {
            synchronized (HttpManager.class) {
                if (mInstance == null)
                    mInstance = new HttpManager();
            }
        }
        return mInstance;
    }


    private HttpManager() {

    }

    /**
     * 初始化Retrofit
     */
    public void init() {
        Retrofit mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mService = mRetrofit.create(ApiService.class);
    }

    public void setTrustCertificate(InputStream inputStream) {
        mTrustrCertificate = inputStream;
    }


    // 配置OkHttpClient
    private OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            synchronized (HttpManager.class) {
                if (sOkHttpClient == null) {
                    File file = new File(App.getContext().getCacheDir(), "Cache");
                    //缓存大小10M
                    int cacheSize = 100 * 1024 * 1024;
                    Cache cache = new Cache(file, cacheSize);
                    SSLSocketFactory sslFactory = SSLSocketFactoryUtils2.getSSLSocketFactory(mTrustrCertificate);
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.addInterceptor(new LoggingInterceptor())
                            .addInterceptor(new OfflineCacheInterceptor())
                            .addNetworkInterceptor(new NetCacheInterceptor())
                            .addNetworkInterceptor(new TokenInterceptor())
                            .cache(cache)
                            .retryOnConnectionFailure(true)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .connectTimeout(5, TimeUnit.SECONDS);
                    if (sslFactory != null) {
                        builder.sslSocketFactory(sslFactory, SSLSocketFactoryUtils.createTrustAllManager())
                                .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier());
                    }
                    sOkHttpClient = builder.build();
                }
            }
        }
        return sOkHttpClient;
    }


    /**
     * 注册
     *
     * @param params
     * @return
     */
    public Observable<User> register(Map<String, String> params) {
        return mService.register(params).compose(RxHelper.handleResult());
    }

    /**
     * 验证短信
     *
     * @param params
     * @return
     */
    public Observable<HttpResult> verifySms(Map<String, String> params) {
        return mService.verifySms(params)
                .onErrorReturn(new HttpResponseFunc<>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通过手机号和密码登陆并获取token
     *
     * @param params
     * @return
     */
    public Observable<TokenBean> doLogin(Map<String, String> params) {
        return mService.doLogin(params).compose(RxHelper.handleResult());
    }

    /**
     * 退出登陆
     *
     * @return
     */
    public Observable<HttpResult> doLogout() {
        return mService.doLogout().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 修改用户密码
     *
     * @param params
     * @return
     */
    public Observable<HttpResult<String>> modifyPsd(Map<String, String> params) {
        return mService.modifyPsd(params)
                .onErrorReturn(new HttpResponseFunc<>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<HttpResult<String>> getEarthTest(Map<String, String> params) {
        return mService.getEarthTest(params)
                .onErrorReturn(new HttpResponseFunc<>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 第三方登陆
     *
     * @return
     */
    public Observable<ThirdLoginBean> loginThird(String accessType, String data) {
        return mService
                .loginThird(accessType, "APP_USER", data)
                .compose(RxHelper.handleResult());
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public Observable<User> getUser() {
        return mService.getUser().compose(RxHelper.handleResult());
    }

    /**
     * 更新用户头像
     *
     * @param file
     * @return
     */
    public Observable<HttpResult<String>> updateUserPhoto(File file) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        return mService.updateUserPhoto(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 上传用户信息
     *
     * @param userName
     * @param userGender
     * @param userBirthday
     * @return
     */
    public Observable<HttpResult> updateUserInfo(String userId, String userName, String userGender, String userBirthday) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("userName", userName);
        params.put("userSex", userGender);
        params.put("userBirthday", userBirthday);
        return mService.updateUserInfo(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取插件列表
     *
     * @return
     */
    public Observable<List<PluginBean>> getPluginList() {
        return mService.getPluginList().compose(RxHelper.handleResult());
    }

    /**
     * 获取主程序得版本信息
     *
     * @return
     */
    public Observable<List<PluginBean>> getHostApp() {
        return mService.getHostApp().compose(RxHelper.handleResult());
    }

    /**
     * 获取公告列表
     *
     * @return
     */
    public Observable<List<NoticeBean>> getNoticeList() {
        return mService.getNoticeList().compose(RxHelper.handleResult());
    }

    /**
     * 提交反馈意见
     *
     * @param params
     * @return
     */
    public Observable<HttpResult<String>> postFeedBack(Map<String, String> params) {
        return mService.postFeedBack(params)
                .onErrorReturn(new HttpResponseFunc<>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
