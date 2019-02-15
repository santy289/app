package com.rootnetapp.rootnetintranet.ui.workflowdetail.information;

import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Step;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.Information;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class InformationViewModel extends ViewModel {

    private static final String TAG = "InformationViewModel";

    private InformationRepository mRepository;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private MutableLiveData<Integer> mErrorLiveData;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<List<Information>> updateInformationListUi;
    protected MutableLiveData<Boolean> showImportantInfoSection;
    protected MutableLiveData<List<Step>> loadImportantInfoSection;

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the workflow.
    private WorkflowDb mWorkflow; // Not in DB and more complete response from network.

    private FormSettings formSettings;

    protected InformationViewModel(InformationRepository informationRepository) {
        this.mRepository = informationRepository;
        this.showLoading = new MutableLiveData<>();
        this.updateInformationListUi = new MutableLiveData<>();
        this.showImportantInfoSection = new MutableLiveData<>();
        this.loadImportantInfoSection = new MutableLiveData<>();
        this.formSettings = new FormSettings();
    }

    protected void initDetails(String token, WorkflowListItem workflow) {
        this.mToken = token;
        this.mWorkflowListItem = workflow;
        getWorkflow(mToken, mWorkflowListItem.getWorkflowId());
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
    }

    /**
     * Updates the info section UI for this workflow.
     *
     * @param workflow Workflow with info to display on the UI.
     */
    private void updateWorkflowInformation(WorkflowDb workflow, WorkflowTypeDb workflowTypeDb) {
        List<Information> informationList = new ArrayList<>();

        String startDate = null;
        String endDate = null;
        if (workflow.getStart() != null) {
            startDate = Utils.getFormattedDate(workflow.getStart(), Utils.SERVER_DATE_FORMAT,
                    Utils.STANDARD_DATE_DISPLAY_FORMAT);
        }
        if (workflow.getEnd() != null) {
            endDate = Utils.getFormattedDate(workflow.getEnd(), Utils.SERVER_DATE_FORMAT,
                    Utils.STANDARD_DATE_DISPLAY_FORMAT);
        }

        Information info = new Information(R.string.title, workflow.getTitle());
        informationList.add(info);
        info = new Information(R.string.description, workflow.getDescription());
        informationList.add(info);

        if (startDate != null) {
            info = new Information(R.string.start_date, startDate);
            informationList.add(info);
        }
        if (endDate != null) {
            info = new Information(R.string.end_date, endDate);
            informationList.add(info);
        }

        if (workflow.getMetas().isEmpty()) {
            updateInformationListUi.setValue(informationList);
            return;
        }

        List<Meta> metaList = workflow.getMetas();
        Meta meta;
        WorkflowMetas metaData;
        Moshi moshi = new Moshi.Builder().build();
        FieldConfig config;
        TypeInfo typeInfo;
        String value;
        JsonAdapter<FieldConfig> jsonAdapter = moshi.adapter(FieldConfig.class);
        for (int i = 0; i < metaList.size(); i++) {
            meta = metaList.get(i);
            try {
                config = jsonAdapter.fromJson(meta.getWorkflowTypeFieldConfig());
                typeInfo = config.getTypeInfo();
                if (typeInfo == null) {
                    continue;
                }

                info = formSettings.formatStringToObject(meta, config);

                if (info == null) {
                    continue;
                }

                informationList.add(info);

//                value = (String) meta.getDisplayValue();
//
//                infoList.add(new Information(item.getWorkflowTypeFieldName(), value));

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "updateWorkflowInformation: " + e.getMessage());
            }
        }

        updateInformationListUi.setValue(informationList);
    }

    /**
     * Populates section regarding important information about a workflow.
     *
     * @param currentStatus Status used to populate the information on the UI.
     */
    private void setImportantInfoSection(Status currentStatus) {
        if (currentStatus == null || currentStatus.getSteps() == null) {
            showImportantInfoSection.setValue(false);
            return;
        }

        Collections.sort(
                currentStatus.getSteps(),
                (s1, s2) -> s1.getOrder() - s2.getOrder()
        );

        List<Step> steps = currentStatus.getSteps();
        loadImportantInfoSection.setValue(steps);
        showImportantInfoSection.setValue(true);
    }

    /**
     * Calls the repository for obtaining a new Workflow Type by a type id.
     *
     * @param auth   Access token to use for endpoint request.
     * @param typeId Id that will be passed on to the endpoint.
     */
    private void getWorkflowType(String auth, int typeId) {
        Disposable disposable = mRepository
                .getWorkflowType(auth, typeId)
                .subscribe(this::onTypeSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    private void getWorkflow(String auth, int workflowId) {
        Disposable disposable = mRepository
                .getWorkflow(auth, workflowId)
                .subscribe(this::onWorkflowSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    private WorkflowTypeDb currentWorkflowType;
    //private Status currentStatus; //TODO make sure we are not using this variable in functions and in here updateUIWithWorkflowType().

    /**
     * Handles success response from endpoint when looking for a workflow type.
     *
     * @param response Incoming response from server.
     */
    private void onTypeSuccess(WorkflowTypeResponse response) {
        currentWorkflowType = response.getWorkflowType();
        if (currentWorkflowType == null) {
            return;
        }
        updateUIWithWorkflowType(currentWorkflowType, mWorkflowListItem.getCurrentStatus());
    }

    /**
     * Handles success when requesting for a workflow by id to the endpoint.
     *
     * @param workflowResponse Network response with workflow data.
     */
    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        mWorkflow = workflowResponse.getWorkflow();
        getWorkflowType(mToken, mWorkflowListItem.getWorkflowTypeId());
    }

    private void updateUIWithWorkflowType(WorkflowTypeDb currentWorkflowType, int statusId) {
        Status currentStatus = findStatusInListBy(statusId);
        setImportantInfoSection(currentStatus);

        updateWorkflowInformation(mWorkflow, currentWorkflowType);
    }

    /**
     * Finds the a Status from the Status List in the current WorkflowType object.
     *
     * @param statusId Status id to find.
     *
     * @return Returns a Status object or null if it doesn't find anything.
     */
    private Status findStatusInListBy(int statusId) {
        List<Status> statusList = currentWorkflowType.getStatus();
        if (statusList == null || statusList.size() < 1) {
            return null;
        }

        Status status;
        for (int i = 0; i < statusId; i++) {
            status = statusList.get(i);
            if (status.getId() == statusId) {
                return status;
            }
        }

        return null;
    }

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }
}
