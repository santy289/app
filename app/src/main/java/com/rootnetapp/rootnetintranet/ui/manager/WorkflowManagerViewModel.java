package com.rootnetapp.rootnetintranet.ui.manager;

import android.util.ArrayMap;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowoverview.WorkflowOverviewResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 27/04/18.
 */

public class WorkflowManagerViewModel extends ViewModel {

    private static final int TAG_WORKFLOW_TYPE = 77;
    private static final String TAG = "ManagerViewModel";

    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<List<WorkflowDb>> mWorkflowListLiveData;
    private MutableLiveData<List<WorkflowDb>> mUserPendingWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mUserOpenWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mUserClosedWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mUserOutOfTimeWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mUserUpdatedWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mCompanyPendingWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mCompanyOpenWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mCompanyClosedWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mCompanyOutOfTimeWorkflowsListLiveData;
    private MutableLiveData<List<WorkflowDb>> mCompanyUpdatedWorkflowsListLiveData;
    private MutableLiveData<Integer> mUserPendingCountLiveData;
    private MutableLiveData<Integer> mUserOpenCountLiveData;
    private MutableLiveData<Integer> mUserClosedCountLiveData;
    private MutableLiveData<Integer> mUserOutOfTimeCountLiveData;
    private MutableLiveData<Integer> mUserUpdatedCountLiveData;
    private MutableLiveData<Integer> mCompanyPendingCountLiveData;
    private MutableLiveData<Integer> mCompanyOpenCountLiveData;
    private MutableLiveData<Integer> mCompanyClosedCountLiveData;
    private MutableLiveData<Integer> mCompanyOutOfTimeCountLiveData;
    private MutableLiveData<Integer> mCompanyUpdatedCountLiveData;
    private MutableLiveData<Integer> mCompanyPeopleInvolvedCountLiveData;
    private MutableLiveData<Boolean> mHideMoreButtonLiveData;
    private MutableLiveData<Boolean> mHideWorkflowListLiveData;
    private MutableLiveData<SingleChoiceFormItem> mAddWorkflowTypeItemLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private WorkflowManagerRepository mRepository;
    private String mToken;
    private String mStartDate, mEndDate;
    private Integer mWorkflowTypeId;
    private int mCurrentPage;
    private int mWebCount, mWebCompleted;
    private WorkflowOverviewResponse workflowOverviewResponse;

    public WorkflowManagerViewModel(WorkflowManagerRepository repository) {
        this.mRepository = repository;
        mCurrentPage = 1;
    }

    /**
     * Initializes the ViewModel data with pre-selected dates.
     * @param token auth token.
     * @param startDate start date filter.
     * @param endDate end date filter.
     */
    protected void init(String token, String startDate, String endDate) {
        mToken = token;
        updateDashboard(startDate, endDate);
        createWorkflowTypeItem();
    }

    //region Dashboard Update

    /**
     * Resets every info regarding to the dashboard and perform a request to the server to obtain
     * the new values.
     *
     * @param startDate      start date filter.
     * @param endDate        end date filter.
     * @param workflowTypeId workflow type filter.
     */
    private void updateDashboard(String startDate, String endDate,
                                 @Nullable Integer workflowTypeId) {
        mWebCount = mWebCompleted = 0;
        showLoading.setValue(true);

        setStartDate(startDate);
        setEndDate(endDate);
        setWorkflowTypeId(workflowTypeId);

        resetCurrentPage();

        getWorkflows();
        mWebCount++;

        getOverviewWorkflowsCount();
        mWebCount++;
    }

    /**
     * Updates the dashboard with new dates.
     *
     * @param startDate start date filter.
     * @param endDate   end date filter.
     */
    protected void updateDashboard(String startDate, String endDate) {
        updateDashboard(startDate, endDate, getWorkflowTypeId());
    }

    /**
     * Updates the dashboard with a new workflow type.
     *
     * @param workflowTypeId workflow type filter.
     */
    protected void updateDashboard(Integer workflowTypeId) {
        updateDashboard(getStartDate(), getEndDate(), workflowTypeId);
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

        options.put("status", true); //always true
        options.put("start", getStartDate());
        options.put("end", getEndDate());

        if (getWorkflowTypeId() != null) {
            options.put("workflow_type_id", getWorkflowTypeId());
        }

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

        mHideWorkflowListLiveData.setValue(workflowList.isEmpty());
        mHideMoreButtonLiveData.setValue(workflowResponseDb.getPager().isIsLastPage());
        mWorkflowListLiveData.setValue(workflowList);
    }
    //endregion

    //region User Workflows
    //region Pending Workflows

    /**
     * Performs a request to the server to obtain the user's pending workflows.
     */
    protected void getUserPendingWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getUserWorkflows().getPending()
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
                .subscribe(this::onUserPendingSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's
     * pending workflows. Callback of the {@link #getUserPendingWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onUserPendingSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mUserPendingWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Open Workflows

    /**
     * Performs a request to the server to obtain the user's open workflows.
     */
    protected void getUserOpenWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getUserWorkflows().getOpen()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("profile_related", 1);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, true, options)
                .subscribe(this::onUserOpenSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's open
     * workflows. Callback of the {@link #getUserOpenWorkflows()} request when it's successful.
     *
     * @param workflowResponseDb server response
     */
    private void onUserOpenSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mUserOpenWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Closed Workflows

    /**
     * Performs a request to the server to obtain the user's closed workflows.
     */
    protected void getUserClosedWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getUserWorkflows().getClosed()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("profile_related", 1);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, false, options)
                .subscribe(this::onUserClosedSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's
     * closed workflows. Callback of the {@link #getUserClosedWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onUserClosedSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mUserClosedWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Out of Time Workflows

    /**
     * Performs a request to the server to obtain the user's out of time workflows.
     */
    protected void getUserOutOfTimeWorkflows() {
        if (workflowOverviewResponse.getOverview().getUserWorkflows().getOutOfTime()
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
                .subscribe(this::onUserOutOfTimeSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's out
     * of time workflows. Callback of the {@link #getUserOutOfTimeWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onUserOutOfTimeSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mUserOutOfTimeWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Updated Workflows

    /**
     * Performs a request to the server to obtain the user's updated workflows.
     */
    protected void getUserUpdatedWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getUserWorkflows().getUpdated()
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
                .subscribe(this::onUserUpdatedSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's
     * updated workflows. Callback of the {@link #getUserUpdatedWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onUserUpdatedSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mUserUpdatedWorkflowsListLiveData.setValue(list);
    }
    //endregion
    //endregion

    //region Company Workflows
    //region Pending Workflows

    /**
     * Performs a request to the server to obtain the user's pending workflows.
     */
    protected void getCompanyPendingWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getCompanyWorkflows().getPending()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("pending", true);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, options)
                .subscribe(this::onCompanyPendingSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's
     * pending workflows. Callback of the {@link #getCompanyPendingWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onCompanyPendingSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mCompanyPendingWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Open Workflows

    /**
     * Performs a request to the server to obtain the user's open workflows.
     */
    protected void getCompanyOpenWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getCompanyWorkflows().getOpen()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, true, options)
                .subscribe(this::onCompanyOpenSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's open
     * workflows. Callback of the {@link #getCompanyOpenWorkflows()} request when it's successful.
     *
     * @param workflowResponseDb server response
     */
    private void onCompanyOpenSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mCompanyOpenWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Closed Workflows

    /**
     * Performs a request to the server to obtain the user's closed workflows.
     */
    protected void getCompanyClosedWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getCompanyWorkflows().getClosed()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, false, options)
                .subscribe(this::onCompanyClosedSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's
     * closed workflows. Callback of the {@link #getCompanyClosedWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onCompanyClosedSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mCompanyClosedWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Out of Time Workflows

    /**
     * Performs a request to the server to obtain the user's out of time workflows.
     */
    protected void getCompanyOutOfTimeWorkflows() {
        if (workflowOverviewResponse.getOverview().getCompanyWorkflows().getOutOfTime()
                .getCount() == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("out_of_time", true);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, options)
                .subscribe(this::onCompanyOutOfTimeSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's out
     * of time workflows. Callback of the {@link #getCompanyOutOfTimeWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onCompanyOutOfTimeSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mCompanyOutOfTimeWorkflowsListLiveData.setValue(list);
    }
    //endregion

    //region Updated Workflows

    /**
     * Performs a request to the server to obtain the user's updated workflows.
     */
    protected void getCompanyUpdatedWorkflows() {
        if (Integer.parseInt(workflowOverviewResponse.getOverview().getCompanyWorkflows().getUpdated()
                .getCount()) == 0) {
            //do not request if there is no data
            return;
        }

        Map<String, Object> options = getCommonFilters();

        options.put("latest", true);

        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getWorkflowsByBaseFilters(mToken, options)
                .subscribe(this::onCompanyUpdatedSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    /**
     * Sets the LiveData that will update the UI and display a dialog with a list of the user's
     * updated workflows. Callback of the {@link #getCompanyUpdatedWorkflows()} request when it's
     * successful.
     *
     * @param workflowResponseDb server response
     */
    private void onCompanyUpdatedSuccess(WorkflowResponseDb workflowResponseDb) {
        showLoading.setValue(false);

        List<WorkflowDb> list = workflowResponseDb.getList();
        if (list == null) return;

        mCompanyUpdatedWorkflowsListLiveData.setValue(list);
    }
    //endregion
    //endregion

    //region Workflows Count

    /**
     * Performs a request to the server to obtain the count of each workflow section.
     */
    protected void getOverviewWorkflowsCount() {
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getOverviewWorkflowsCount(mToken, getCommonFilters())
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

        mUserPendingCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getUserWorkflows().getPending().getCount()));
        mUserOpenCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getUserWorkflows().getOpen().getCount()));
        mUserClosedCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getUserWorkflows().getClosed().getCount()));
        mUserOutOfTimeCountLiveData.setValue(
                overviewResponse.getOverview().getUserWorkflows().getOutOfTime().getCount());
        mUserUpdatedCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getUserWorkflows().getUpdated().getCount()));

        mCompanyPendingCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getCompanyWorkflows().getPending().getCount()));
        mCompanyOpenCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getCompanyWorkflows().getOpen().getCount()));
        mCompanyClosedCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getCompanyWorkflows().getClosed().getCount()));
        mCompanyOutOfTimeCountLiveData.setValue(
                overviewResponse.getOverview().getCompanyWorkflows().getOutOfTime().getCount());
        mCompanyUpdatedCountLiveData.setValue(Integer.valueOf(
                overviewResponse.getOverview().getCompanyWorkflows().getUpdated().getCount()));
        mCompanyPeopleInvolvedCountLiveData.setValue(
                overviewResponse.getOverview().getCompanyWorkflows().getPersonsInvolved());
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

    protected Integer getWorkflowTypeId() {
        return mWorkflowTypeId;
    }

    private void setWorkflowTypeId(Integer workflowTypeId) {
        this.mWorkflowTypeId = workflowTypeId;
    }

    /**
     * Creates the WorkflowType form item. Performs a request to the repo to retrieve the options
     * and then send the form item to the UI.
     */
    private void createWorkflowTypeItem() {
        //used to be setWorkflowTypes

        Disposable disposable = Observable.fromCallable(() -> {
            List<WorkflowTypeItemMenu> types = mRepository.getWorklowTypeNames();
            if (types == null || types.size() < 1) {
                return false;
            }

            List<Option> options = new ArrayList<>();
            for (int i = 0; i < types.size(); i++) {
                String name = types.get(i).getName();
                Integer id = types.get(i).getId();

                Option option = new Option(id, name);
                options.add(option);
            }

            return new SingleChoiceFormItem.Builder()
                    .setTitleRes(R.string.form_workflow_type)
                    .setRequired(true)
                    .setTag(TAG_WORKFLOW_TYPE)
                    .setOptions(options)
                    .build();
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(singleChoiceFormItem -> {
                    mAddWorkflowTypeItemLiveData
                            .setValue((SingleChoiceFormItem) singleChoiceFormItem);
                }, throwable -> {
                    Log.d(TAG, "setWorkflowTypes: error " + throwable.getMessage());
                });
        mDisposables.add(disposable);
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

    //region User Observables
    protected LiveData<List<WorkflowDb>> getObservableUserPendingWorkflows() {
        if (mUserPendingWorkflowsListLiveData == null) {
            mUserPendingWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mUserPendingWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableUserOpenWorkflows() {
        if (mUserOpenWorkflowsListLiveData == null) {
            mUserOpenWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mUserOpenWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableUserClosedWorkflows() {
        if (mUserClosedWorkflowsListLiveData == null) {
            mUserClosedWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mUserClosedWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableUserOutOfTimeWorkflows() {
        if (mUserOutOfTimeWorkflowsListLiveData == null) {
            mUserOutOfTimeWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mUserOutOfTimeWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableUserUpdatedWorkflows() {
        if (mUserUpdatedWorkflowsListLiveData == null) {
            mUserUpdatedWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mUserUpdatedWorkflowsListLiveData;
    }

    protected LiveData<Integer> getObservableUserPendingCount() {
        if (mUserPendingCountLiveData == null) {
            mUserPendingCountLiveData = new MutableLiveData<>();
        }
        return mUserPendingCountLiveData;
    }

    protected LiveData<Integer> getObservableUserOpenCount() {
        if (mUserOpenCountLiveData == null) {
            mUserOpenCountLiveData = new MutableLiveData<>();
        }
        return mUserOpenCountLiveData;
    }

    protected LiveData<Integer> getObservableUserClosedCount() {
        if (mUserClosedCountLiveData == null) {
            mUserClosedCountLiveData = new MutableLiveData<>();
        }
        return mUserClosedCountLiveData;
    }

    protected LiveData<Integer> getObservableUserOutOfTimeCount() {
        if (mUserOutOfTimeCountLiveData == null) {
            mUserOutOfTimeCountLiveData = new MutableLiveData<>();
        }
        return mUserOutOfTimeCountLiveData;
    }

    protected LiveData<Integer> getObservableUserUpdatedCount() {
        if (mUserUpdatedCountLiveData == null) {
            mUserUpdatedCountLiveData = new MutableLiveData<>();
        }
        return mUserUpdatedCountLiveData;
    }
    //endregion

    //region Company Observables
    protected LiveData<List<WorkflowDb>> getObservableCompanyPendingWorkflows() {
        if (mCompanyPendingWorkflowsListLiveData == null) {
            mCompanyPendingWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mCompanyPendingWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableCompanyOpenWorkflows() {
        if (mCompanyOpenWorkflowsListLiveData == null) {
            mCompanyOpenWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mCompanyOpenWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableCompanyClosedWorkflows() {
        if (mCompanyClosedWorkflowsListLiveData == null) {
            mCompanyClosedWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mCompanyClosedWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableCompanyOutOfTimeWorkflows() {
        if (mCompanyOutOfTimeWorkflowsListLiveData == null) {
            mCompanyOutOfTimeWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mCompanyOutOfTimeWorkflowsListLiveData;
    }

    protected LiveData<List<WorkflowDb>> getObservableCompanyUpdatedWorkflows() {
        if (mCompanyUpdatedWorkflowsListLiveData == null) {
            mCompanyUpdatedWorkflowsListLiveData = new MutableLiveData<>();
        }
        return mCompanyUpdatedWorkflowsListLiveData;
    }

    protected LiveData<Integer> getObservableCompanyPendingCount() {
        if (mCompanyPendingCountLiveData == null) {
            mCompanyPendingCountLiveData = new MutableLiveData<>();
        }
        return mCompanyPendingCountLiveData;
    }

    protected LiveData<Integer> getObservableCompanyOpenCount() {
        if (mCompanyOpenCountLiveData == null) {
            mCompanyOpenCountLiveData = new MutableLiveData<>();
        }
        return mCompanyOpenCountLiveData;
    }

    protected LiveData<Integer> getObservableCompanyClosedCount() {
        if (mCompanyClosedCountLiveData == null) {
            mCompanyClosedCountLiveData = new MutableLiveData<>();
        }
        return mCompanyClosedCountLiveData;
    }

    protected LiveData<Integer> getObservableCompanyOutOfTimeCount() {
        if (mCompanyOutOfTimeCountLiveData == null) {
            mCompanyOutOfTimeCountLiveData = new MutableLiveData<>();
        }
        return mCompanyOutOfTimeCountLiveData;
    }

    protected LiveData<Integer> getObservableCompanyUpdatedCount() {
        if (mCompanyUpdatedCountLiveData == null) {
            mCompanyUpdatedCountLiveData = new MutableLiveData<>();
        }
        return mCompanyUpdatedCountLiveData;
    }

    protected LiveData<Integer> getObservableCompanyPeopleInvolvedCount() {
        if (mCompanyPeopleInvolvedCountLiveData == null) {
            mCompanyPeopleInvolvedCountLiveData = new MutableLiveData<>();
        }
        return mCompanyPeopleInvolvedCountLiveData;
    }
    //endregion

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

    protected LiveData<SingleChoiceFormItem> getObservableWorkflowTypeItem() {
        if (mAddWorkflowTypeItemLiveData == null) {
            mAddWorkflowTypeItemLiveData = new MutableLiveData<>();
        }
        return mAddWorkflowTypeItemLiveData;
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

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }
}
