package com.rootnetapp.rootnetintranet.ui.workflowdetail.information;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowUser;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.models.createworkflow.geolocation.GeolocationMetaData;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Step;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.SelectedLocation;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.Information;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.InformationAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_ALL;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_MY_OWN;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_OWN;

public class InformationViewModel extends ViewModel {

    private static final String TAG = "InformationViewModel";

    private InformationRepository mRepository;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<WorkflowUser> mUpdateOwnerUiLiveData;
    private MutableLiveData<Boolean> mShowNoConnectionViewLiveData;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<List<Information>> updateInformationListUi;
    protected MutableLiveData<Boolean> showImportantInfoSection;
    protected MutableLiveData<List<Step>> loadImportantInfoSection;
    protected MutableLiveData<Boolean> showEditButtonLiveData;

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
        this.showEditButtonLiveData = new MutableLiveData<>();
        this.formSettings = new FormSettings();
    }

    protected void initDetails(String token, String userId, String userPermissions,
                               WorkflowListItem workflow) {
        this.mToken = token;
        this.mWorkflowListItem = workflow;
        showLoading.setValue(true);
        getWorkflow(mToken, mWorkflowListItem.getWorkflowId());
        checkEditPermissions(userId == null ? 0 : Integer.parseInt(userId), userPermissions);
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
    }

    private void checkEditPermissions(int userId, String permissionsString) {
        List<String> permissionsToCheck = new ArrayList<>();

        if (mWorkflowListItem.getOwnerId() == userId) {
            permissionsToCheck.add(WORKFLOW_EDIT_MY_OWN);
            permissionsToCheck.add(WORKFLOW_EDIT_OWN);
        } else {
            permissionsToCheck.add(WORKFLOW_EDIT_ALL);
        }

        RootnetPermissionsUtils permissionsUtils = new RootnetPermissionsUtils(permissionsString);
        boolean hasEditPermissions = permissionsUtils.hasPermissions(permissionsToCheck);
        showEditButtonLiveData.setValue(hasEditPermissions);
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
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<FieldConfig> jsonAdapter = moshi.adapter(FieldConfig.class);
        for (int i = 0; i < metaList.size(); i++) {
            Meta meta = metaList.get(i);
            try {
                FieldConfig config = jsonAdapter.fromJson(meta.getWorkflowTypeFieldConfig());
                TypeInfo typeInfo = config.getTypeInfo();
                if (typeInfo == null) {
                    continue;
                }

                info = formSettings.formatStringToObject(meta, config);

                if (info == null) {
                    continue;
                }

                //for geolocation only, set the view type and the selected location
                if (typeInfo.getType().equals(FormSettings.TYPE_GEOLOCATION)) {
                    info.setViewType(InformationAdapter.ViewType.GEOLOCATION);

                    if (meta.getValue() == null || meta.getValue().isEmpty()
                            || meta.getValue().equals("\"\"")) {
                        continue;
                    }

                    JsonAdapter<GeolocationMetaData> geolocationMetaDataJsonAdapter = moshi
                            .adapter(GeolocationMetaData.class);
                    GeolocationMetaData geolocationMetaData = geolocationMetaDataJsonAdapter
                            .fromJson(meta.getValue());

                    if (geolocationMetaData == null
                            || geolocationMetaData.getValue() == null
                            || geolocationMetaData.getValue().getLatLng() == null
                            || geolocationMetaData.getValue().getLatLng().size() < 2) {
                        continue;
                    }

                    LatLng latLng = new LatLng(geolocationMetaData.getValue().getLatLng().get(0),
                            geolocationMetaData.getValue().getLatLng().get(1));
                    String addressName = geolocationMetaData.getValue().getAddress();

                    SelectedLocation selectedLocation = new SelectedLocation(latLng, addressName);

                    info.setSelectedLocation(selectedLocation);
                }

                informationList.add(info);

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
        mUpdateOwnerUiLiveData.setValue(mWorkflow.getAuthor());
        showLoading.setValue(false);
        mShowNoConnectionViewLiveData.setValue(false);
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
        mErrorLiveData.setValue(Utils.getOnFailureStringRes(throwable));

        if (throwable instanceof UnknownHostException) {
            mShowNoConnectionViewLiveData.setValue(true);
            showEditButtonLiveData.setValue(false);
        }
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<WorkflowUser> getObservableUpdateOwnerUi() {
        if (mUpdateOwnerUiLiveData == null) {
            mUpdateOwnerUiLiveData = new MutableLiveData<>();
        }
        return mUpdateOwnerUiLiveData;
    }

    protected LiveData<Boolean> getObservableShowNoConnectionView() {
        if (mShowNoConnectionViewLiveData == null) {
            mShowNoConnectionViewLiveData = new MutableLiveData<>();
        }
        return mShowNoConnectionViewLiveData;
    }
}
