package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;

public class DataSourceWorkflowList extends PageKeyedDataSource<Integer, WorkflowListItem> {


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, WorkflowListItem> callback) {

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, WorkflowListItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, WorkflowListItem> callback) {

    }
}
