package com.benbaba.dadpat.host.http.subscriber;

import android.support.v4.app.FragmentActivity;

import com.arialyy.aria.util.NetUtils;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.ToastUtils;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/3/6.
 */
public abstract class BaseSubscriber<T> implements Observer<T> {
    protected WeakReference<FragmentActivity> mWeakReference;

    public BaseSubscriber(FragmentActivity activity) {
        mWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void onSubscribe(Disposable d) {
        FragmentActivity activity = mWeakReference.get();
        if (activity == null || !NetUtils.isConnected(activity)) {
            ToastUtils.showShortToast(activity, "当前网络不可用，请检查网络情况");
            onComplete();
        }
    }

    @Override
    public void onError(Throwable e) {
        L.i("onError:" + e.getLocalizedMessage());
    }

    @Override
    public void onNext(T t) {
        requestSuccess(t);
    }

    @Override
    public void onComplete() {
    }

    public abstract void requestSuccess(T t);
}
