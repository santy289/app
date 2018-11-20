package com.rootnetapp.rootnetintranet.ui.workflowdetail;


import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.profile.ProfileDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflowdetail.WorkflowApproveRejectResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowDetailRepository {
    private ApiInterface service;
    private ProfileDao profileDao;
    private WorkflowTypeDbDao workflowTypeDbDao;

    private MutableLiveData<Boolean> showLoading;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "WorkflowDetailRepository";

    public WorkflowDetailRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.profileDao = database.profileDao();
        this.workflowTypeDbDao = database.workflowTypeDbDao();
    }

    protected void clearDisposables() {
        disposables.clear();
    }

    public Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected LiveData<Boolean> getErrorShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }

}
