package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.InformationRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CommentsViewModelFactory implements ViewModelProvider.Factory {

    private CommentsRepository commentsRepository;

    public CommentsViewModelFactory(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CommentsViewModel.class)) {
            return (T) new CommentsViewModel(commentsRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}