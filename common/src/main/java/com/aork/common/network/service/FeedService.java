package com.aork.common.network.service;

import com.aork.base.pager.ListPager;
import com.aork.base.unsplash.Photo;
import com.aork.common.network.SchedulerTransformer;
import com.aork.common.network.UrlCollection;
import com.aork.common.network.api.FeedApi;
import com.aork.common.network.interceptor.FeedInterceptor;
import com.aork.common.network.interceptor.NapiInterceptor;
import com.aork.common.network.observer.BaseObserver;
import com.aork.common.network.observer.ObserverContainer;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Feed service.
 * */

public class FeedService {

    private FeedApi api;
    private CompositeDisposable compositeDisposable;

    public FeedService(OkHttpClient client,
                       GsonConverterFactory gsonConverterFactory,
                       RxJava2CallAdapterFactory rxJava2CallAdapterFactory,
                       CompositeDisposable disposable) {
        api = new Retrofit.Builder()
                .baseUrl(UrlCollection.UNSPLASH_URL)
                .client(
                        client.newBuilder()
                                .addInterceptor(new FeedInterceptor())
                                .addInterceptor(new NapiInterceptor())
                                .build()
                ).addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build()
                .create((FeedApi.class));
        compositeDisposable = disposable;
    }

    public void requestFollowingFeed(@ListPager.PageRule int page, @ListPager.PerPageRule int per_page,
                                     BaseObserver<List<Photo>> observer) {
        api.getFollowingFeed(page, per_page)
                .compose(SchedulerTransformer.create())
                .subscribe(new ObserverContainer<>(compositeDisposable, observer));
    }

    public void cancel() {
        compositeDisposable.clear();
    }
}
