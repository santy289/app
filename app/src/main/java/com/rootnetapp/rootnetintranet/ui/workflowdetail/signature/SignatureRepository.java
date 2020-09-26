package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

public class SignatureRepository {

    private ApiInterface service;
    private AppDatabase database;

    protected SignatureRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
    }


}
