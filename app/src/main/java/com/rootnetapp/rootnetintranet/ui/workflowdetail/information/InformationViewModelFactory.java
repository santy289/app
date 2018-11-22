package com.rootnetapp.rootnetintranet.ui.workflowdetail.information;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class InformationViewModelFactory implements ViewModelProvider.Factory {

    private InformationRepository informationRepository;

    public InformationViewModelFactory(InformationRepository informationRepository) {
        this.informationRepository = informationRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(InformationViewModel.class)) {
            return (T) new InformationViewModel(informationRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}