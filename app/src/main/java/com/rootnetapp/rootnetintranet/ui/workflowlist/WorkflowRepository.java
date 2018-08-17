package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.arch.lifecycle.LiveData;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDao;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowRepository {

    private AppDatabase database;
    private ApiInterface service;
    private WorkflowDao workflowDao;
    private LiveData<List<Workflow>> allWorkflows;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public WorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
        workflowDao = this.database.workflowDao();
        allWorkflows = workflowDao.getWorkflows();
    }

    public void clearDisposables() {
        disposables.clear();
    }

    public LiveData<List<Workflow>> getAllWorkflows() {
        return allWorkflows;
    }

    public void insertWorkflow(Workflow workflow) {
        Disposable disposable = Completable.fromCallable(() -> {
            database.workflowDao().insertWorkflow(workflow);
            return true;
        }).subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe();
        disposables.add(disposable);
    }

    public Observable<List<Workflow>> getWorkflowsFromInternal() {
        return Observable.fromCallable(()-> database.workflowDao().getAllWorkflows())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowsResponse> getWorkflowsFromService(String auth, int page) {
        return service.getWorkflows(auth, 50, true, page, true)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Workflow>> setWorkflowsLocalUpdate(List<Workflow> workflows){
        return Observable.fromCallable(() -> {
            database.workflowDao().insertAll(workflows);
            return workflows;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

}