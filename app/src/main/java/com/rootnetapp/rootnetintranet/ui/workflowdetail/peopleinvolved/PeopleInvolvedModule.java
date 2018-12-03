package com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class PeopleInvolvedModule {

    @Provides
    PeopleInvolvedRepository providePeopleInvolvedRepository(ApiInterface service,
                                                             AppDatabase database) {
        return new PeopleInvolvedRepository(service, database);
    }

    @Provides
    PeopleInvolvedViewModelFactory providePeopleInvolvedViewModelFactory(
            PeopleInvolvedRepository peopleInvolvedRepository) {
        return new PeopleInvolvedViewModelFactory(peopleInvolvedRepository);
    }
}
