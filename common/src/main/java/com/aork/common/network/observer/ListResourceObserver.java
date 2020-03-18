package com.aork.common.network.observer;

import com.aork.base.resource.ListResource;
import com.aork.common.base.vm.pager.PagerViewModel;

import java.util.List;

public class ListResourceObserver<T> extends BaseObserver<List<T>> {

    private PagerViewModel<T> viewModel;
    private boolean refresh;

    public ListResourceObserver(PagerViewModel<T> viewModel, boolean refresh) {
        this.viewModel = viewModel;
        this.refresh = refresh;
    }

    @Override
    public void onSucceed(List<T> list) {
        if (refresh) {
            viewModel.writeListResource(resource -> ListResource.refreshSuccess(resource, list));
        } else if (list.size() == viewModel.getListPerPage()) {
            viewModel.writeListResource(resource -> ListResource.loadSuccess(resource, list));
        } else {
            viewModel.writeListResource(resource -> ListResource.allLoaded(resource, list));
        }
    }

    @Override
    public void onFailed() {
        viewModel.writeListResource(ListResource::error);
    }
}