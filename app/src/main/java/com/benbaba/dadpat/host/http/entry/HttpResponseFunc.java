package com.benbaba.dadpat.host.http.entry;

import com.benbaba.dadpat.host.http.ApiException;

import io.reactivex.functions.Function;

public class HttpResponseFunc<T> implements Function<Throwable, HttpResult<T>> {

    @Override
    public HttpResult<T> apply(Throwable throwable) throws Exception {
        HttpResult result = new HttpResult<>();
        result.setCode(ApiException.CODE_NETWORK_ERROR);
        result.setSuccess(false);
        result.setMsg("请检查网络，网络连接错误");
        return result;
    }
}
