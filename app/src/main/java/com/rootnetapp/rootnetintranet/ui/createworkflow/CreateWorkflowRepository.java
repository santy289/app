package com.rootnetapp.rootnetintranet.ui.createworkflow;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

/**
 * Created by root on 22/03/18.
 */

public class CreateWorkflowRepository {

    ApiInterface service;
    AppDatabase database;

    public CreateWorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
    }



}
