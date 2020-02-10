package com.rootnetapp.rootnetintranet.ui.qrtoken;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class QRTokenViewModelFactory implements ViewModelProvider.Factory {

    public QRTokenViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QRTokenViewModel.class)) {
            return (T) new QRTokenViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}