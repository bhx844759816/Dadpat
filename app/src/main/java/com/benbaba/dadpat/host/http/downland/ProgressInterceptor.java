package com.benbaba.dadpat.host.http.downland;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 下载进度拦截器
 * Created by Administrator on 2018/2/10.
 */
public class ProgressInterceptor implements Interceptor {
    private DownLandProgressListener listener;

    public ProgressInterceptor(DownLandProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), listener))
                .build();
    }
}
