package com.aork.about.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aork.about.R;
import com.aork.about.ui.holder.TranslatorHolder;
import com.aork.common.base.activity.MysplashActivity;
import com.aork.about.model.AboutModel;
import com.aork.about.presenter.CreateAboutModelPresenter;
import com.aork.about.ui.holder.CategoryHolder;
import com.aork.about.ui.holder.HeaderHolder;
import com.aork.about.ui.holder.LibraryHolder;
import com.aork.common.base.adapter.BaseAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * About adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link AboutModel}.
 *
 * */

public class AboutAdapter extends BaseAdapter<AboutModel, AboutModel, AboutAdapter.ViewHolder> {

    private MysplashActivity a;

    /**
     * Basic CollectionHolder class for {@link AboutAdapter}.
     * */
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void onBindView(MysplashActivity a, AboutModel model);

        protected abstract void onRecycled();
    }

    public AboutAdapter(MysplashActivity a) {
        super(a, CreateAboutModelPresenter.createModelList(a));
        this.a = a;
    }

    @Override
    protected AboutModel getViewModel(AboutModel model) {
        return model;
    }

    @NotNull
    @Override
    public AboutAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case AboutModel.TYPE_HEADER:
                return new HeaderHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_about_header, parent, false));

            case AboutModel.TYPE_CATEGORY:
                return new CategoryHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_abuot_category, parent, false));

          /*  case AboutModel.TYPE_APP:
                return new AppHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_about_app, parent, false));
*/
            case AboutModel.TYPE_TRANSLATOR:
                return new TranslatorHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_about_translator, parent, false)
                );

            case AboutModel.TYPE_LIBRARY:
            default:
                return new LibraryHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_about_library, parent, false));
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, AboutModel model) {
        holder.onBindView(a, model);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, AboutModel model, @NonNull List<Object> payloads) {
        onBindViewHolder(holder, model);
    }

    @Override
    public void onViewRecycled(@NotNull ViewHolder holder) {
        holder.onRecycled();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }
}
