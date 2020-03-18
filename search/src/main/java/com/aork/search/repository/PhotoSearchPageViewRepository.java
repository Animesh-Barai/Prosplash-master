package com.aork.search.repository;

import com.aork.base.resource.ListResource;
import com.aork.base.unsplash.SearchPhotosResult;
import com.aork.common.network.observer.BaseObserver;
import com.aork.common.network.service.SearchService;
import com.aork.search.vm.PhotoSearchPageViewModel;

import javax.inject.Inject;

public class PhotoSearchPageViewRepository {

    private SearchService service;

    @Inject
    public PhotoSearchPageViewRepository(SearchService service) {
        this.service = service;
    }

    public void getPhotos(PhotoSearchPageViewModel viewModel, String query, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.searchPhotos(
                query,
                viewModel.getListRequestPage(),
                new BaseObserver<SearchPhotosResult>() {
                    @Override
                    public void onSucceed(SearchPhotosResult searchPhotosResult) {
                        if (searchPhotosResult.results == null) {
                            onFailed();
                            return;
                        }
                        if (refresh) {
                            viewModel.writeListResource(resource ->
                                    ListResource.refreshSuccess(resource, searchPhotosResult.results));
                        } else if (searchPhotosResult.results.size() == viewModel.getListPerPage()) {
                            viewModel.writeListResource(resource ->
                                    ListResource.loadSuccess(resource, searchPhotosResult.results));
                        } else {
                            viewModel.writeListResource(resource ->
                                    ListResource.allLoaded(resource, searchPhotosResult.results));
                        }
                    }

                    @Override
                    public void onFailed() {
                        viewModel.writeListResource(ListResource::error);
                    }
                }
        );
    }

    public void cancel() {
        service.cancel();
    }
}
