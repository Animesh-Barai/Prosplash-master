package com.aork.common.presenter.pager;

import androidx.recyclerview.widget.RecyclerView;

import com.aork.base.i.PagerManageView;
import com.aork.base.i.PagerView;
import com.aork.base.unsplash.Photo;
import com.aork.common.base.vm.pager.PagerViewModel;

import java.util.ArrayList;
import java.util.List;

public class PagerLoadablePresenter {

    public static List<Photo> loadMore(PagerViewModel<Photo> viewModel, int currentCount,
                                       PagerView pagerView, RecyclerView recyclerView,
                                       PagerManageView pagerManageView, int pagerIndex) {
        if (pagerManageView != null && pagerManageView.canLoadMore(pagerIndex)) {
            pagerManageView.onLoad(pagerIndex);
        }
        if (!recyclerView.canScrollVertically(1)
                && pagerManageView != null && pagerManageView.isLoading(pagerIndex)) {
            pagerView.setSwipeLoading(true);
        }

        if (currentCount >= viewModel.getListSize()) {
            return new ArrayList<>();
        }

        return subList(viewModel, currentCount, viewModel.getListSize());
    }

    private static List<Photo> subList(PagerViewModel<Photo> viewModel, int from, int to) {
        List<Photo> result = new ArrayList<>();
        viewModel.readDataList(list -> result.addAll(list.subList(from, to)));
        return result;
    }
}
