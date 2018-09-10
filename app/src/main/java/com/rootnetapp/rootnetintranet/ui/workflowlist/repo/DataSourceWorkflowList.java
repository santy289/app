package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;

public class DataSourceWorkflowList extends PageKeyedDataSource<Integer, WorkflowListItem> {

    private AppDatabase database;

    public DataSourceWorkflowList(AppDatabase database) {
        this.database = database;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, WorkflowListItem> callback) {
        // TODO call database and fecth initial items

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, WorkflowListItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, WorkflowListItem> callback) {
        // TODO fetch items After a page number
    }

}
