package com.aork.search.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aork.base.resource.ListResource;
import com.aork.common.base.vm.pager.PhotosPagerViewModel;
import com.aork.base.unsplash.Photo;
import com.aork.search.repository.PhotoSearchPageViewRepository;

import javax.inject.Inject;

public class PhotoSearchPageViewModel extends PhotosPagerViewModel
        implements SearchPagerViewModel<Photo> {

    private PhotoSearchPageViewRepository repository;
    private String query;

    @Inject
    public PhotoSearchPageViewModel(PhotoSearchPageViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<Photo> defaultResource, String defaultQuery) {
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
        repository.getPhotos(this, getQuery(), true);
    }

    @Override
    public void load() {
        repository.getPhotos(this, getQuery(), false);
    }
}
