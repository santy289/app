package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.profile.ProfileDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import io.reactivex.disposables.CompositeDisposable;

public class StatusRepository {

    private static final String TAG = "StatusRepository";

    private ApiInterface service;
    private ProfileDao profileDao;
    private WorkflowTypeDbDao workflowTypeDbDao;

    //todo add methods and implementations

    private final CompositeDisposable disposables = new CompositeDisposable();

    public StatusRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.profileDao = database.profileDao();
        this.workflowTypeDbDao = database.workflowTypeDbDao();
    }

    protected void clearDisposables() {
        disposables.clear();
    }
}
