package com.aork.search.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aork.base.resource.ListResource;
import com.aork.base.unsplash.User;
import com.aork.common.base.vm.pager.UsersPagerViewModel;
import com.aork.search.repository.UserSearchPageViewRepository;

import javax.inject.Inject;

public class UserSearchPageViewModel extends UsersPagerViewModel
        implements SearchPagerViewModel<User> {

    private UserSearchPageViewRepository repository;
    private String query;

    @Inject
    public UserSearchPageViewModel(UserSearchPageViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<User> defaultResource, String defaultQuery) {
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
        repository.getUsers(this, getQuery(), true);
    }

    @Override
    public void load() {
        repository.getUsers(this, getQuery(), false);
    }
}
