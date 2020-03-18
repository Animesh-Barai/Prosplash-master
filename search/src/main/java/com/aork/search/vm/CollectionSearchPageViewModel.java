package com.aork.search.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aork.base.resource.ListResource;
import com.aork.base.unsplash.Collection;
import com.aork.common.base.vm.pager.CollectionsPagerViewModel;
import com.aork.search.repository.CollectionSearchPageViewRepository;

import javax.inject.Inject;

public class CollectionSearchPageViewModel extends CollectionsPagerViewModel
        implements SearchPagerViewModel<Collection> {
    
    private CollectionSearchPageViewRepository repository;
    private String query;

    @Inject
    public CollectionSearchPageViewModel(CollectionSearchPageViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<Collection> defaultResource, String defaultQuery) {
        if (super.init(defaultResource)) {
            setQuery(defaultQuery);
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    @Nullable
    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public void setQuery(@Nullable String query) {
        this.query = query;
    }

    @Override
    public void refresh() {
        repository.getCollections(this, getQuery(), true);
    }

    @Override
    public void load() {
        repository.getCollections(this, getQuery(), false);
    }
}
