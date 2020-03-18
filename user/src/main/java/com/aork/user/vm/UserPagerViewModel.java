package com.aork.user.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aork.base.resource.ListResource;

public interface UserPagerViewModel<T> {

    boolean init(@NonNull ListResource<T> resource, String defaultUsername);

    @Nullable
    String getUsername();

    void setUsername(@Nullable String username);
}
