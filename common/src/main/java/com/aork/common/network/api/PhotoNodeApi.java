package com.aork.common.network.api;

import com.aork.base.unsplash.Photo;
import com.aork.common.network.UrlCollection;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Photo node api.
 * */

public interface PhotoNodeApi {

    @GET(UrlCollection.UNSPLASH_NODE_API_URL + "photos/{id}/info")
    Observable<Photo> getAPhoto(@Path("id") String id);
}
