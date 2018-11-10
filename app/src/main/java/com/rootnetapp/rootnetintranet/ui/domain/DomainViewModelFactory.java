package com.rootnetapp.rootnetintranet.ui.domain;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

public class DomainViewModelFactory implements ViewModelProvider.Factory{

    private DomainRepository domainRepository;

    public DomainViewModelFactory(DomainRepository domainRepository){
        this.domainRepository = domainRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)  {
        if (modelClass.isAssignableFrom(DomainViewModel.class)) {
            return (T) new DomainViewModel(domainRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
