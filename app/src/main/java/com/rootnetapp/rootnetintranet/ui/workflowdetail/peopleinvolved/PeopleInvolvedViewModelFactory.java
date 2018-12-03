package com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PeopleInvolvedViewModelFactory implements ViewModelProvider.Factory {

    private PeopleInvolvedRepository peopleInvolvedRepository;

    public PeopleInvolvedViewModelFactory(PeopleInvolvedRepository peopleInvolvedRepository) {
        this.peopleInvolvedRepository = peopleInvolvedRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PeopleInvolvedViewModel.class)) {
            return (T) new PeopleInvolvedViewModel(peopleInvolvedRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}