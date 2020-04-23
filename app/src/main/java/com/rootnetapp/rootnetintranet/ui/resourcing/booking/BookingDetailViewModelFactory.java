package com.rootnetapp.rootnetintranet.ui.resourcing.booking;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class BookingDetailViewModelFactory implements ViewModelProvider.Factory {

    private BookingDetailRepository bookingDetailRepository;

    public BookingDetailViewModelFactory(BookingDetailRepository bookingDetailRepository) {
        this.bookingDetailRepository = bookingDetailRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BookingDetailViewModel.class)) {
            return (T) new BookingDetailViewModel(bookingDetailRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}