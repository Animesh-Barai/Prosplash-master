package com.aork.common.network.service;

import com.aork.base.unsplash.Total;
import com.aork.common.network.SchedulerTransformer;
import com.aork.common.network.UrlCollection;
import com.aork.common.network.api.StatusApi;
import com.aork.common.network.interceptor.AuthInterceptor;
import com.aork.common.network.observer.BaseObserver;
import com.aork.common.network.observer.ObserverContainer;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Status service.
 * */

public class StatusService {

    private StatusApi api;
    private CompositeDisposable compositeDisposable;

    public StatusService(OkHttpClient client,
                         GsonConverterFactory gsonConverterFactory,
                         RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                         CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(UrlCollection.UNSPLASH_API_BASE_URL)
                .client(
                        client.newBuilder()
                                .addInterceptor(new AuthInterceptor())
                                .build()
                ).addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((StatusApi.class));
        compositeDisposable = disposable;
    }

    public void requestTotal(BaseObserver<Total> observer) {
        api.getTotal()
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
