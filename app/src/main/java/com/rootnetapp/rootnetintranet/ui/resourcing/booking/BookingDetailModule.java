package com.rootnetapp.rootnetintranet.ui.resourcing.booking;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class BookingDetailModule {
    @Provides
    BookingDetailRepository provideBookingDetailRepository(ApiInterface service, AppDatabase database) {
        return new BookingDetailRepository(service);
    }

    @Provides
    BookingDetailViewModelFactory provideBookingDetailViewModelFactory(BookingDetailRepository bookingDetailRepository) {
        return new BookingDetailViewModelFactory(bookingDetailRepository);
    }
}