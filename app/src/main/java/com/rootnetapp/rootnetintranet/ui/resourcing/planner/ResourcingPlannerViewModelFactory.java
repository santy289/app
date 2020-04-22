package com.rootnetapp.rootnetintranet.ui.resourcing.planner;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ResourcingPlannerViewModelFactory implements ViewModelProvider.Factory {

    private ResourcingPlannerRepository resourcingPlannerRepository;

    public ResourcingPlannerViewModelFactory(ResourcingPlannerRepository resourcingPlannerRepository) {
        this.resourcingPlannerRepository = resourcingPlannerRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ResourcingPlannerViewModel.class)) {
            return (T) new ResourcingPlannerViewModel(resourcingPlannerRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}