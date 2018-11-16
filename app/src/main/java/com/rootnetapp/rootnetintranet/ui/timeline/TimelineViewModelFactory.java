package com.rootnetapp.rootnetintranet.ui.timeline;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by root on 10/04/18.
 */

public class TimelineViewModelFactory implements ViewModelProvider.Factory{

    private TimelineRepository repository;

    public TimelineViewModelFactory(TimelineRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TimelineViewModel.class)) {
            return (T) new TimelineViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
