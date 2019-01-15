package com.benbaba.dadpat.host.http.downland;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 下载得管理类
 * Created by Administrator on 2018/2/10.
 */
public class DownlandManager {
    private static final String BASE_URL = "http://www.dadpat.com";
    private static final int TIMEOUT = 10;//请求数据最大延迟时间
    private static DownlandManager INSTANCE;
    private DownLandProgressListener mListener;
    private DownLandService service;

    public DownlandManager(DownLandProgressListener listener) {
        this.mListener = listener;
        retrofit();
    }

    /**
     * 设置进度监听
     *
     * @param listener
     */
    public void setDownLandProgressListener(DownLandProgressListener listener) {
        this.mListener = listener;
    }

    /**
     * 实例化DownLandService
     */
    private void retrofit() {
        ProgressInterceptor interceptor = new ProgressInterceptor(mListener);
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(DownLandService.class);
    }

    /**
     * 下载文件
     *
     * @param url 地址
     */
    public Flowable<InputStream> downland(String url) {
        return service.download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(ResponseBody::byteStream)
                .observeOn(Schedulers.computation());
    }

    /**
     * 下载得service
     */
    public interface DownLandService {
        @Streaming
        @GET
        Flowable<ResponseBody> download(@Url String url);
    }
}
