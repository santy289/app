package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;

public class DataSourceWorkflowListFactory extends DataSource.Factory<Integer, WorkflowListItem> {

    private MutableLiveData<DataSourceWorkflowList> sourceLiveData = new MutableLiveData<>();
    private AppDatabase database;

    public DataSourceWorkflowListFactory(AppDatabase database) {
        this.database = database;
    }

    @Override
    public DataSource<Integer, WorkflowListItem> create() {
        DataSourceWorkflowList source = new DataSourceWorkflowList(database);
        sourceLiveData.postValue(source);
        return source;
    }

}
