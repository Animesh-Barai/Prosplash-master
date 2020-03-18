package com.aork.search.repository;

import com.aork.base.resource.ListResource;
import com.aork.base.unsplash.SearchCollectionsResult;
import com.aork.common.network.observer.BaseObserver;
import com.aork.common.network.service.SearchService;
import com.aork.search.vm.CollectionSearchPageViewModel;

import javax.inject.Inject;

public class CollectionSearchPageViewRepository {

    private SearchService service;

    @Inject
    public CollectionSearchPageViewRepository(SearchService service) {
        this.service = service;
    }

    public void getCollections(CollectionSearchPageViewModel viewModel, String query, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.searchCollections(
                query,
                viewModel.getListRequestPage(),
                new BaseObserver<SearchCollectionsResult>() {
                    @Override
                    public void onSucceed(SearchCollectionsResult searchCollectionsResult) {
                        if (searchCollectionsResult.results == null) {
                            onFailed();
                            return;
                        }
                        if (refresh) {
                            viewModel.writeListResource(resource ->
                                    ListResource.refreshSuccess(resource, searchCollectionsResult.results));
                        } else if (searchCollectionsResult.results.size() == viewModel.getListPerPage()) {
                            viewModel.writeListResource(resource ->
                                    ListResource.loadSuccess(resource, searchCollectionsResult.results));
                        } else {
                            viewModel.writeListResource(resource ->
                                    ListResource.allLoaded(resource, searchCollectionsResult.results));
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
