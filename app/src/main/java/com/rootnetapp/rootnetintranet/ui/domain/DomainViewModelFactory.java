package com.rootnetapp.rootnetintranet.ui.domain;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

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
