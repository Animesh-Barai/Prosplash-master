package com.aork.me.vm;

import com.aork.base.resource.ListResource;
import com.aork.base.unsplash.Collection;
import com.aork.common.base.vm.pager.CollectionsPagerViewModel;
import com.aork.common.utils.manager.AuthManager;
import com.aork.me.repository.MeCollectionsViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Me collections view model.
 * */
public class MeCollectionsViewModel extends CollectionsPagerViewModel
        implements MePagerViewModel<Collection> {

    private MeCollectionsViewRepository repository;
    private String username;

    @Inject
    public MeCollectionsViewModel(MeCollectionsViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<Collection> resource) {
        if (super.init(resource)) {
            setUsername(AuthManager.getInstance().getUsername());
            refresh();
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    @Override
    public void refresh() {
        setUsername(AuthManager.getInstance().getUsername());
        repository.getUserCollections(this, true);
    }

    @Override
    public void load() {
        setUsername(AuthManager.getInstance().getUsername());
        repository.getUserCollections(this, false);
    }

    @Nullable
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(@Nullable String username) {
        this.username = username;
    }
}
