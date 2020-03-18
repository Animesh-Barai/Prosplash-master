package com.aork.common.network.api;

import com.aork.base.unsplash.SearchCollectionsResult;
import com.aork.base.unsplash.SearchUsersResult;
import com.aork.common.network.UrlCollection;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Search node api.
 * */

public interface SearchNodeApi {

    @GET(UrlCollection.UNSPLASH_NODE_API_URL + "search/users")
    Observable<SearchUsersResult> searchUsers(@Query("query") String query,
                                              @Query("page") int page,
                                              @Query("per_page") int per_page);

    @GET(UrlCollection.UNSPLASH_NODE_API_URL + "search/collections")
    Observable<SearchCollectionsResult> searchCollections(@Query("query") String query,
                                                          @Query("page") int page,
                                                          @Query("per_page") int per_page);
}
