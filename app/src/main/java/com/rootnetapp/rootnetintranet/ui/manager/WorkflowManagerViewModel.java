package com.rootnetapp.rootnetintranet.ui.manager;

import android.util.ArrayMap;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.models.responses.workflowoverview.WorkflowOverviewResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

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
    private MutableLiveData<List<WorkflowDb>> mMyPendingWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mMyOpenWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mMyClosedWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mOutOfTimeWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mUpdatedWorkflowsListLiveData;
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
    private String mStartDate, mEndDate;
    private int mCurrentPage;
    private int mWebCount, mWebCompleted;
    private WorkflowOverviewResponse workflowOverviewResponse;

    public WorkflowManagerViewModel(WorkflowManagerRepository repository) {
        this.mRepository = repository;
        mCurrentPage = 1;
    }

    public void init(String token, String startDate, String endDate) {
        mToken = token;
        updateDashboard(startDate, endDate);
    }

    //region Dashboard Update

    /**
     * Resets every info regarding to the dashboard and perform a request to the server to obtain
     * the new values.
     *
     * @param startDate start date filter.
     * @param endDate   end date filter.
     */
    protected void updateDashboard(String startDate, String endDate) {
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

    /**
     * Verifies whether all of the requested services are completed before dismissing the loading
     * view.
     */
    private void updateCompleted() {
        mWebCompleted++;

        if (mWebCompleted >= mWebCount) {
            showLoading.setValue(false);

            mWebCount = mWebCompleted = 0;
        }
    }
    //endregion

    //region All Workflows

    /**
     * Creates a Map of the common filters. This map is used to be sent as a request parameter to
     * the server.
     *
     * @return common filters.
     */
    private Map<String, Object> getCommonFilters() {
        Map<String, Object> options = new ArrayMap<>();

        options.put("start", getStartDate());
        options.put("end", getEndDate());

        return options;
    }

    /**
     * Resets the current page to its initial value. Called when the dashboard filters change.
     */
    protected void resetCurrentPage() {
        this.mCurrentPage = 1;
    }

    /**
     * Increments the current page by one. Called when the user requests more workflows.
     */
    protected void incrementCurrentPage() {
        mCurrentPage++;
    }

    /**
     * Performs a request to the server to obtain a list of workflows using the common filters.
     */
    protected void getWorkflows() {
        showLoading.setValue(true);

        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, mCurrentPage, getCommonFilters())
                .subscribe(this::onWorkflowsSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI regarding the workflows. Callback of the {@link
     * #getWorkflows()} request when it's successful.
     *
     * @param workflowResponseDb server response
     */
    private void onWorkflowsSuccess(WorkflowResponseDb workflowResponseDb) {
        updateCompleted();

        List<WorkflowDb> workflowList = workflowResponseDb.getList();

        mHideMoreButtonLiveData.setValue(workflowResponseDb.getPager().isIsLastPage());
        mHideWorkflowListLiveData.setValue(workflowList.isEmpty());
        mWorkflowListLiveData.setValue(workflowList);
    }
    //endregion

    //region My Pending Workflows

    /**
     * Performs a request to the server to obtain the user's pending workflows.
     */
    protected void getMyPendingWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getMyWorkflows().getPending()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("profile_related", 1);
        options.put("pending", true);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, options)
                .subscribe(this::onMyPendingSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's
     * pending workflows. Callback of the {@link #getMyPendingWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onMyPendingSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mMyPendingWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region My Open Workflows

    /**
     * Performs a request to the server to obtain the user's open workflows.
     */
    protected void getMyOpenWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getMyWorkflows().getOpen()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("profile_related", 1);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, true, options)
                .subscribe(this::onMyOpenSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's open
     * workflows. Callback of the {@link #getMyOpenWorkflows()} request when it's successful.
     *
     * @param workflowResponseDb server response
     */
    private void onMyOpenSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mMyOpenWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region My Pending Workflows

    /**
     * Performs a request to the server to obtain the user's closed workflows.
     */
    protected void getMyClosedWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getMyWorkflows().getClosed()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("profile_related", 1);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, false, options)
                .subscribe(this::onMyClosedSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's
     * closed workflows. Callback of the {@link #getMyClosedWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onMyClosedSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mMyClosedWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Out of Time Workflows

    /**
     * Performs a request to the server to obtain the user's out of time workflows.
     */
    protected void getOutOfTimeWorkflows() {
        if (workflowOverviewResponse.getOverview().getMyWorkflows().getOutOfTime()
                .getCount() == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("profile_related", 1);
        options.put("out_of_time", true);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, options)
                .subscribe(this::onOutOfTimeSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's out
     * of time workflows. Callback of the {@link #getOutOfTimeWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onOutOfTimeSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mOutOfTimeWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Updated Workflows

    /**
     * Performs a request to the server to obtain the user's updated workflows.
     */
    protected void getUpdatedWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getMyWorkflows().getUpdated()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("profile_related", 1);
        options.put("latest", true);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, options)
                .subscribe(this::onUpdatedSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's
     * updated workflows. Callback of the {@link #getUpdatedWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onUpdatedSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mUpdatedWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Workflows Count

    /**
     * Performs a request to the server to obtain the count of each workflow section.
     */
    protected void getOverviewWorkflowsCount() {
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getOverviewWorkflowsCount(mToken, getStartDate(), getEndDate())
                .subscribe(this::onOverviewSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI regarding the count of each section. Callback of
     * the {@link #getOverviewWorkflowsCount()} request when it's successful.
     *
     * @param overviewResponse server response
     */
    private void onOverviewSuccess(WorkflowOverviewResponse overviewResponse) {
        updateCompleted();

        workflowOverviewResponse = overviewResponse;

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

    protected String getFormattedStartDate() {
        return Utils.getFormattedDate(getStartDate(), Utils.SERVER_DATE_FORMAT, "yyyy-MM-dd");
    }

    private void setStartDate(String startDate) {
        this.mStartDate = startDate;
    }

    protected String getEndDate() {
        return mEndDate;
    }

    protected String getFormattedEndDate() {
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

    protected LiveData<List<WorkflowDb>> getObservableMyPendingWorkflows() {
        if (mMyPendingWorkflowsListLiveData == null) {
            mMyPendingWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mMyPendingWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableMyOpenWorkflows() {
        if (mMyOpenWorkflowsListLiveData == null) {
            mMyOpenWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mMyOpenWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableMyClosedWorkflows() {
        if (mMyClosedWorkflowsListLiveData == null) {
            mMyClosedWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mMyClosedWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableOutOfTimeWorkflows() {
        if (mOutOfTimeWorkflowsListLiveData == null) {
            mOutOfTimeWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mOutOfTimeWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableUpdatedWorkflows() {
        if (mUpdatedWorkflowsListLiveData == null) {
            mUpdatedWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mUpdatedWorkflowsListLiveData;
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
