package com.aork.me.vm;

import com.aork.base.resource.ListResource;
import com.aork.common.base.vm.pager.PhotosPagerViewModel;
import com.aork.base.unsplash.Photo;
import com.aork.common.utils.manager.AuthManager;
import com.aork.me.repository.MePhotosViewRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.annotation.Nullable;

public class MePhotosViewModel extends PhotosPagerViewModel
        implements MePagerViewModel<Photo> {

    private MePhotosViewRepository repository;
    private String username;

    @Inject
    public MePhotosViewModel(MePhotosViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NotNull ListResource<Photo> defaultResource) {
        if (super.init(defaultResource)) {
            setUsername(AuthManager.getInstance().getUsername());
            refresh();
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getRepository().cancel();
    }

    @Override
    public void refresh() {
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserPhotos(this, true);
    }

    @Override
    public void load() {
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserPhotos(this, false);
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

    MePhotosViewRepository getRepository() {
        return repository;
    }
}
