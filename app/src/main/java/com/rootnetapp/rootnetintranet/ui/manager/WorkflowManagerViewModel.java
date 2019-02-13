package com.rootnetapp.rootnetintranet.ui.manager;

import android.util.ArrayMap;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by root on 27/04/18.
 */

public class WorkflowManagerViewModel extends ViewModel {

    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<WorkflowResponseDb> mWorkflowsLiveData;
    private MutableLiveData<Integer> mOutOfTimeCountLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private WorkflowManagerRepository mRepository;
    private String mToken;
    private List<WorkflowDb> mOutOfTimeWorkflows;

    public WorkflowManagerViewModel(WorkflowManagerRepository repository) {
        this.mRepository = repository;
    }

    public void init(String token) {
        mToken = token;
        fetchOutOfTimeWorkflows();
    }

    protected void getWorkflows(int page) {
        showLoading.setValue(true);
        Disposable disposable = mRepository.getPendingWorkflows(mToken, page)
                .subscribe(this::onWorkflowsSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    protected List<WorkflowDb> getOutOfTimeWorkflows() {
        if (mOutOfTimeWorkflows == null) mOutOfTimeWorkflows = new ArrayList<>();

        return mOutOfTimeWorkflows;
    }

    protected void setOutOfTimeWorkflows(List<WorkflowDb> outOfTimeWorkflows) {
        this.mOutOfTimeWorkflows = outOfTimeWorkflows;
    }

    private void fetchOutOfTimeWorkflows() {
        Map<String, Object> options = new ArrayMap<>();

        options.put("out_of_time", true);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, options)
                .subscribe(this::onOutOfTimeSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onWorkflowsSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);
        mWorkflowsLiveData.setValue(workflowResponseDb);
    }

    private void onOutOfTimeSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        setOutOfTimeWorkflows(list);
        mOutOfTimeCountLiveData.setValue(list.size());
    }

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<WorkflowResponseDb> getObservableWorkflows() {
        if (mWorkflowsLiveData == null) {
            mWorkflowsLiveData = new MutableLiveData<>();
        }
        return mWorkflowsLiveData;
    }

    protected LiveData<Integer> getObservableOutOfTimeCount() {
        if (mOutOfTimeCountLiveData == null) {
            mOutOfTimeCountLiveData = new MutableLiveData<>();
        }
        return mOutOfTimeCountLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<Boolean> getObservableShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }
}
