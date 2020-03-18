package com.aork.common.network.service;

import com.aork.common.network.NullResponseBody;
import com.aork.common.network.SchedulerTransformer;
import com.aork.common.network.UrlCollection;
import com.aork.common.network.api.FollowApi;
import com.aork.common.network.interceptor.AuthInterceptor;
import com.aork.common.network.interceptor.NapiInterceptor;
import com.aork.common.network.observer.NoBodyObserver;
import com.aork.common.network.observer.ObserverContainer;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Follow service.
 * */

public class FollowService {

    private FollowApi api;
    private CompositeDisposable compositeDisposable;

    public FollowService(OkHttpClient client,
                         GsonConverterFactory gsonConverterFactory,
                         RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                         CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(UrlCollection.UNSPLASH_URL)
                .client(
                        client.newBuilder()
                                .addInterceptor(new AuthInterceptor())
                                .addInterceptor(new NapiInterceptor())
                                .build()
                ).addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((FollowApi.class));
        compositeDisposable = disposable;
    }

    public void followUser(String username, NoBodyObserver observer) {
        api.follow(username)
                .compose(SchedulerTransformer.create())
                .onExceptionResumeNext(Observable.create(emitter -> emitter.onNext(new NullResponseBody())))
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancelFollowUser(String username, NoBodyObserver observer) {
        api.cancelFollow(username)
                .compose(SchedulerTransformer.create())
                .onExceptionResumeNext(Observable.create(emitter -> emitter.onNext(new NullResponseBody())))
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
