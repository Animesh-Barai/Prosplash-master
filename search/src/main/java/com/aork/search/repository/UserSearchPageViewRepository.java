package com.aork.search.repository;

import com.aork.base.resource.ListResource;
import com.aork.base.unsplash.SearchUsersResult;
import com.aork.common.network.observer.BaseObserver;
import com.aork.common.network.service.SearchService;
import com.aork.search.vm.UserSearchPageViewModel;

import javax.inject.Inject;

public class UserSearchPageViewRepository {

    private SearchService service;

    @Inject
    public UserSearchPageViewRepository(SearchService service) {
        this.service = service;
    }

    public void getUsers(UserSearchPageViewModel viewModel, String query, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.searchUsers(
                query,
                viewModel.getListRequestPage(),
                new BaseObserver<SearchUsersResult>() {
                    @Override
                    public void onSucceed(SearchUsersResult searchUsersResult) {
                        if (searchUsersResult.results == null) {
                            onFailed();
                            return;
                        }
                        if (refresh) {
                            viewModel.writeListResource(resource ->
                                    ListResource.refreshSuccess(resource, searchUsersResult.results));
                        } else if (searchUsersResult.results.size() == viewModel.getListPerPage()) {
                            viewModel.writeListResource(resource ->
                                    ListResource.loadSuccess(resource, searchUsersResult.results));
                        } else {
                            viewModel.writeListResource(resource ->
                                    ListResource.allLoaded(resource, searchUsersResult.results));
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
