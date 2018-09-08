package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowTypeAndWorkflows;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

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
    private WorkflowDbDao workflowDbDao;
    private LiveData<PagedList<WorkflowListItem>> allWorkflows;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public WorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
        workflowDbDao = this.database.workflowDbDao();
        DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao.getWorkflows();

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPrefetchDistance(15)
                        .setInitialLoadSizeHint(30)
                        .setPageSize(20).build();

        LivePagedListBuilder<Integer, WorkflowListItem> builder = new LivePagedListBuilder<>(factory, pagedListConfig);
        allWorkflows = builder.build();
    }

    public void clearDisposables() {
        disposables.clear();
    }

    public LiveData<PagedList<WorkflowListItem>> getAllWorkflows() {
        return allWorkflows;
    }

    public void insertWorkflow(WorkflowDb workflow) {
        Disposable disposable = Completable.fromCallable(() -> {
            database.workflowDbDao().insertWorkflow(workflow);
            return true;
        }).subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe();
        disposables.add(disposable);
    }

    public void test() {
//        Disposable disposable = Completable.fromCallable(() -> {
//            List<WorkflowTypeAndWorkflows> result = database.workflowDbDao().loadWorkflowTypeAndWorkflows(16);
//            return true;
//        }).subscribeOn(Schedulers.newThread())
//          .observeOn(AndroidSchedulers.mainThread())
//          .subscribe();
//        disposables.add(disposable);
    }

    public Observable<List<WorkflowDb>> getWorkflowsFromInternal() {
        return Observable.fromCallable(()-> database.workflowDbDao().getAllWorkflows())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowResponseDb> getWorkflowsFromService(String auth, int page) {
        return service.getWorkflowsDb(auth, 50, true, page, true, false)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<WorkflowDb>> setWorkflowsLocalUpdate(List<WorkflowDb> workflows){
        return Observable.fromCallable(() -> {
            database.workflowDbDao().insertWorkflows(workflows);
            return workflows;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

}