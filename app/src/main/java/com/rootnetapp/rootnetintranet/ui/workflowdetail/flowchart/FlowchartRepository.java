package com.rootnetapp.rootnetintranet.ui.workflowdetail.flowchart;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

public class FlowchartRepository {

    private AppDatabase database;
    private ApiInterface apiInterface;

    public FlowchartRepository(AppDatabase database, ApiInterface apiInterface) {
        this.database = database;
        this.apiInterface = apiInterface;
    }
}
