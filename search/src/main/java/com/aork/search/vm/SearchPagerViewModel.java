package com.aork.search.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aork.base.resource.ListResource;

public interface SearchPagerViewModel<T> {

    boolean init(@NonNull ListResource<T> resource, String defaultQuery);

    @Nullable
    String getQuery();

    void setQuery(@Nullable String query);
}
