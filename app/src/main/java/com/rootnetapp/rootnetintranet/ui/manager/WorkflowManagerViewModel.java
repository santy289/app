package com.rootnetapp.rootnetintranet.ui.manager;

import android.util.ArrayMap;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.models.responses.workflowoverview.WorkflowOverviewResponse;
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
    private MutableLiveData<Integer> mMyPendingCountLiveData;
    private MutableLiveData<Integer> mOutOfTimeCountLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private WorkflowManagerRepository mRepository;
    private String mToken;
    private List<WorkflowDb> mOutOfTimeWorkflows;
    private String mStartDate, mEndDate;

    public WorkflowManagerViewModel(WorkflowManagerRepository repository) {
        this.mRepository = repository;
    }

    public void init(String token, String startDate, String endDate) {
        mToken = token;
        setStartDate(startDate);
        setEndDate(endDate);
        fetchOutOfTimeWorkflows();
        getOverviewWorkflowsCount();
    }

    protected void getWorkflows(int page) {
        showLoading.setValue(true);
        Disposable disposable = mRepository.getPendingWorkflows(mToken, page)
                .subscribe(this::onWorkflowsSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    //region Out of time
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

    private void onOutOfTimeSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        setOutOfTimeWorkflows(list);
        mOutOfTimeCountLiveData.setValue(list.size());
    }
    //endregion

    //region Workflows Count
    protected void getOverviewWorkflowsCount() {
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getOverviewWorkflowsCount(mToken, getStartDate(), getEndDate())
                .subscribe(this::onOverviewSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onOverviewSuccess(WorkflowOverviewResponse overviewResponse) {
        showLoading.setValue(false);

        mOutOfTimeCountLiveData.setValue(
                overviewResponse.getOverview().getMyWorkflows().getOutOfTime().getCount());
        mMyPendingCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getMyWorkflows().getPending().getCount()));
    }
    //endregion

    //region Filters
    protected String getStartDate() {
        return mStartDate;
    }

    protected void setStartDate(String startDate) {
        startDate = Utils.getFormattedDate(startDate, Utils.SERVER_DATE_FORMAT, "yyyy-MM-dd");
        this.mStartDate = startDate;
    }

    protected String getEndDate() {
        return mEndDate;
    }

    protected void setEndDate(String endDate) {
        endDate = Utils.getFormattedDate(endDate, Utils.SERVER_DATE_FORMAT, "yyyy-MM-dd");
        this.mEndDate = endDate;
    }
    //endregion

    private void onWorkflowsSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);
        mWorkflowsLiveData.setValue(workflowResponseDb);
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

    protected LiveData<Integer> getObservableMyPendingCount() {
        if (mMyPendingCountLiveData == null) {
            mMyPendingCountLiveData = new MutableLiveData<>();
        }
        return mMyPendingCountLiveData;
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
