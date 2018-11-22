package com.rootnetapp.rootnetintranet.ui.workflowdetail.files;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FilesViewModelFactory implements ViewModelProvider.Factory {

    private FilesRepository filesRepository;

    public FilesViewModelFactory(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FilesViewModel.class)) {
            return (T) new FilesViewModel(filesRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}