package com.aork.common.network.service;

import com.aork.base.unsplash.NotificationFeed;
import com.aork.common.network.SchedulerTransformer;
import com.aork.common.network.UrlCollection;
import com.aork.common.network.api.NotificationApi;
import com.aork.common.network.interceptor.NotificationInterceptor;
import com.aork.common.network.observer.BaseObserver;
import com.aork.common.network.observer.ObserverContainer;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Notification service.
 * */

public class NotificationService {

    private NotificationApi api;
    private CompositeDisposable compositeDisposable;

    public NotificationService(OkHttpClient client,
                               GsonConverterFactory gsonConverterFactory,
                               RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                               CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(UrlCollection.UNSPLASH_URL)
                .client(
                        client.newBuilder()
                                .addInterceptor(new NotificationInterceptor())
                                .build()
                ).addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((NotificationApi.class));
        compositeDisposable = disposable;
    }

    public void requestNotificationFeed(String enrich, BaseObserver<NotificationFeed> observer) {
        api.getNotification(RequestBody.create(MediaType.parse("text/plain"), enrich))
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}