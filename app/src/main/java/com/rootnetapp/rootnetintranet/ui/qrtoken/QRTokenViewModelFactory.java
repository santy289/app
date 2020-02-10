package com.rootnetapp.rootnetintranet.ui.qrtoken;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class QRTokenViewModelFactory implements ViewModelProvider.Factory {

    private QRTokenRepository qrTokenRepository;

    public QRTokenViewModelFactory(QRTokenRepository qrTokenRepository) {
        this.qrTokenRepository = qrTokenRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QRTokenViewModel.class)) {
            return (T) new QRTokenViewModel(qrTokenRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}