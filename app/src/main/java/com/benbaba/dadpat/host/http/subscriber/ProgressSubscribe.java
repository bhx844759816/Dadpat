package com.benbaba.dadpat.host.http.subscriber;

import android.support.v4.app.FragmentActivity;

import com.benbaba.dadpat.host.dialog.factory.DialogFactory;

import io.reactivex.disposables.Disposable;

/**
 * 进度对话框的订阅
 * Created by Administrator on 2018/2/20.
 */
public abstract class ProgressSubscribe<T> extends BaseSubscriber<T> {


    public ProgressSubscribe(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        FragmentActivity activity = mWeakReference.get();
        if (activity != null)
            DialogFactory.showLoadingDialog(activity);
    }

    @Override
    public void onNext(T t) {
        super.onNext(t);
        FragmentActivity activity = mWeakReference.get();
        if (activity != null)
            DialogFactory.dismissLoadingDialog(activity);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        FragmentActivity activity = mWeakReference.get();
        if (activity != null)
            DialogFactory.dismissLoadingDialog(activity);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        FragmentActivity activity = mWeakReference.get();
        if (activity != null)
            DialogFactory.dismissLoadingDialog(activity);
    }
}
