package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class CommentsModule {
    @Provides
    CommentsRepository provideCommentsRepository(ApiInterface service) {
        return new CommentsRepository(service);
    }

    @Provides
    CommentsViewModelFactory provideCommentsViewModelFactory(CommentsRepository commentsRepository) {
        return new CommentsViewModelFactory(commentsRepository);
    }
}
