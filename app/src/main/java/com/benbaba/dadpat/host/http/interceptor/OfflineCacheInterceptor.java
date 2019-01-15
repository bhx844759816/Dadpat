package com.benbaba.dadpat.host.http.interceptor;

import com.benbaba.dadpat.host.App;
import com.benbaba.dadpat.host.utils.NetUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class OfflineCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetUtils.isNetworkConnected(App.getContext())) {
            int offlineCacheTime = 60 * 60 * 24;//离线的时候的缓存的过期时间
            request = request.newBuilder()
                    .cacheControl(new CacheControl
                            .Builder()
                            .maxStale(offlineCacheTime, TimeUnit.SECONDS)
                            .onlyIfCached()
                            .build()
                    ) //两种方式结果是一样的，写法不同
//                    .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
                    .build();
        }
        return chain.proceed(request);
    }
}
