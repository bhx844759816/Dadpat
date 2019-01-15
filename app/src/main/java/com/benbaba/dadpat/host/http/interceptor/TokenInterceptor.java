package com.benbaba.dadpat.host.http.interceptor;

import android.text.TextUtils;


import com.benbaba.dadpat.host.App;
import com.benbaba.dadpat.host.utils.L;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 拦截请求设置请求头
 * Created by Administrator on 2017/12/18.
 */
public class TokenInterceptor implements Interceptor {
    private static final String TOKEN_KEY = "Authorization";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = App.token;
        //如果token不为null得时候添加到header
        L.i("token:" + token);
        if (!TextUtils.isEmpty(token)) {
            Request request = originalRequest
                    .newBuilder()
                    .addHeader(TOKEN_KEY, token)
                    .build();
            return chain.proceed(request);
        }
        return chain.proceed(originalRequest);
    }

}
