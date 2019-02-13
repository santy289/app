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
    private MutableLiveData<List<WorkflowDb>> mWorkflowListLiveData;
    private MutableLiveData<Integer> mMyPendingCountLiveData;
    private MutableLiveData<Integer> mMyOpenCountLiveData;
    private MutableLiveData<Integer> mMyClosedCountLiveData;
    private MutableLiveData<Integer> mOutOfTimeCountLiveData;
    private MutableLiveData<Integer> mUpdatedCountLiveData;
    private MutableLiveData<Integer> mPendingCountLiveData;
    private MutableLiveData<Integer> mOpenCountLiveData;
    private MutableLiveData<Integer> mClosedCountLiveData;
    private MutableLiveData<Boolean> mHideMoreButtonLiveData;
    private MutableLiveData<Boolean> mHideWorkflowListLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private WorkflowManagerRepository mRepository;
    private String mToken;
    private List<WorkflowDb> mOutOfTimeWorkflows;
    private String mStartDate, mEndDate;
    private int mCurrentPage;
    private int mWebCount, mWebCompleted;

    public WorkflowManagerViewModel(WorkflowManagerRepository repository) {
        this.mRepository = repository;
    }

    public void init(String token, String startDate, String endDate) {
        mToken = token;
        updateDashboard(startDate, endDate);
        fetchOutOfTimeWorkflows();
    }

    //region Dashboard Update
    protected void updateDashboard(String startDate, String endDate){
        mWebCount = mWebCompleted = 0;
        showLoading.setValue(true);

        setStartDate(startDate);
        setEndDate(endDate);

        resetCurrentPage();

        getWorkflows();
        mWebCount++;

        getOverviewWorkflowsCount();
        mWebCount++;
    }

    private void updateCompleted(){
        mWebCompleted++;

        if (mWebCompleted >= mWebCount) {
            showLoading.setValue(false);

            mWebCount = mWebCompleted = 0;
        }
    }
    //endregion

    //region Workflows
    protected void resetCurrentPage() {
        this.mCurrentPage = 0;
    }

    protected void incrementCurrentPage(){
        mCurrentPage++;
    }

    protected void getWorkflows() {
        showLoading.setValue(true);

        Disposable disposable = mRepository.getPendingWorkflows(mToken, mCurrentPage)
                .subscribe(this::onWorkflowsSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onWorkflowsSuccess(WorkflowResponseDb workflowResponseDb) {
        updateCompleted();

        List<WorkflowDb> workflowList = workflowResponseDb.getList();

        mHideMoreButtonLiveData.setValue(workflowResponseDb.getPager().isIsLastPage());
        mHideWorkflowListLiveData.setValue(workflowList.isEmpty());
        mWorkflowListLiveData.setValue(workflowList);
    }
    //endregion

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
        updateCompleted();

        mMyPendingCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getMyWorkflows().getPending().getCount()));
        mMyOpenCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getMyWorkflows().getOpen().getCount()));
        mMyClosedCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getMyWorkflows().getClosed().getCount()));
        mOutOfTimeCountLiveData.setValue(
                overviewResponse.getOverview().getMyWorkflows().getOutOfTime().getCount());
        mUpdatedCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getMyWorkflows().getUpdated().getCount()));

        mPendingCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getCompanyWorkflows().getPending().getCount()));
        mOpenCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getCompanyWorkflows().getOpen().getCount()));
        mClosedCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getCompanyWorkflows().getClosed().getCount()));
    }
    //endregion

    //region Filters
    protected String getStartDate() {
        return mStartDate;
    }

    protected String getFormattedStartDate(){
        return Utils.getFormattedDate(getStartDate(), Utils.SERVER_DATE_FORMAT, "yyyy-MM-dd");
    }

    private void setStartDate(String startDate) {
        this.mStartDate = startDate;
    }

    protected String getEndDate() {
        return mEndDate;
    }

    protected String getFormattedEndDate(){
        return Utils.getFormattedDate(getEndDate(), Utils.SERVER_DATE_FORMAT, "yyyy-MM-dd");
    }

    private void setEndDate(String endDate) {
        this.mEndDate = endDate;
    }
    //endregion

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<List<WorkflowDb>> getObservableWorkflows() {
        if (mWorkflowListLiveData == null) {
            mWorkflowListLiveData = new MutableLiveData<>();
        }
        return mWorkflowListLiveData;
    }

    protected LiveData<Integer> getObservableMyPendingCount() {
        if (mMyPendingCountLiveData == null) {
            mMyPendingCountLiveData = new MutableLiveData<>();
        }
        return mMyPendingCountLiveData;
    }

    protected LiveData<Integer> getObservableMyOpenCount() {
        if (mMyOpenCountLiveData == null) {
            mMyOpenCountLiveData = new MutableLiveData<>();
        }
        return mMyOpenCountLiveData;
    }

    protected LiveData<Integer> getObservableMyClosedCount() {
        if (mMyClosedCountLiveData == null) {
            mMyClosedCountLiveData = new MutableLiveData<>();
        }
        return mMyClosedCountLiveData;
    }

    protected LiveData<Integer> getObservableOutOfTimeCount() {
        if (mOutOfTimeCountLiveData == null) {
            mOutOfTimeCountLiveData = new MutableLiveData<>();
        }
        return mOutOfTimeCountLiveData;
    }

    protected LiveData<Integer> getObservableUpdatedCount() {
        if (mUpdatedCountLiveData == null) {
            mUpdatedCountLiveData = new MutableLiveData<>();
        }
        return mUpdatedCountLiveData;
    }

    protected LiveData<Integer> getObservablePendingCount() {
        if (mPendingCountLiveData == null) {
            mPendingCountLiveData = new MutableLiveData<>();
        }
        return mPendingCountLiveData;
    }

    protected LiveData<Integer> getObservableOpenCount() {
        if (mOpenCountLiveData == null) {
            mOpenCountLiveData = new MutableLiveData<>();
        }
        return mOpenCountLiveData;
    }

    protected LiveData<Integer> getObservableClosedCount() {
        if (mClosedCountLiveData == null) {
            mClosedCountLiveData = new MutableLiveData<>();
        }
        return mClosedCountLiveData;
    }

    protected LiveData<Boolean> getObservableHideMoreButton() {
        if (mHideMoreButtonLiveData == null) {
            mHideMoreButtonLiveData = new MutableLiveData<>();
        }
        return mHideMoreButtonLiveData;
    }

    protected LiveData<Boolean> getObservableHideWorkflowList() {
        if (mHideWorkflowListLiveData == null) {
            mHideWorkflowListLiveData = new MutableLiveData<>();
        }
        return mHideWorkflowListLiveData;
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
