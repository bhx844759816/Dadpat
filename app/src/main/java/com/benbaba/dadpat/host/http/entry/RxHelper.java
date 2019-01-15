package com.benbaba.dadpat.host.http.entry;

import com.benbaba.dadpat.host.http.ApiException;
import com.benbaba.dadpat.host.utils.L;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/2/10.
 */
public class RxHelper {


    public static <T> ObservableTransformer<HttpResult<T>, T> handleResult() {
        return observable -> observable.onErrorReturn(new HttpResponseFunc<>())
                .flatMap(result -> {
                    L.i("request Result:" + result.toString());
                    if (result.isSuccess()) {
                        return Observable.just(result.getData());
                    } else {
                        return Observable.error(new ApiException(result.getCode()));
                    }
                }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
