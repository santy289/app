package com.rootnetapp.rootnetintranet.ui.workflowdetail.files;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class FilesModule {
    @Provides
    FilesRepository provideFilesRepository(ApiInterface service) {
        return new FilesRepository(service);
    }

    @Provides
    FilesViewModelFactory provideFilesViewModelFactory(FilesRepository filesRepository) {
        return new FilesViewModelFactory(filesRepository);
    }
}
