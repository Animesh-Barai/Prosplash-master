package com.aork.base.i;

public interface PagerManageView {

    void onRefresh(int index);
    void onLoad(int index);

    boolean canLoadMore(int index);
    boolean isLoading(int index);
}
