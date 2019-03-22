package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.activation.WorkflowActivationResponse;
import com.rootnetapp.rootnetintranet.models.responses.exportpdf.ExportPdfResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowDetailRepository {

    private final ApiInterface service;
    private final WorkflowDbDao workflowDbDao;

    private MutableLiveData<Throwable> errorLiveData;
    private MutableLiveData<ExportPdfResponse> exportPdfResponseLiveData;
    private MutableLiveData<WorkflowActivationResponse> openCloseResponseLiveData;
    private MutableLiveData<WorkflowActivationResponse> enableDisableResponseLiveData;
    private MutableLiveData<WorkflowListItem> retrieveFromDbWorkflow;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "WorkflowDetailRepo";

    public WorkflowDetailRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.workflowDbDao = database.workflowDbDao();
    }

    protected void clearDisposables() {
        disposables.clear();
    }

    /**
     * Gets the desired Workflow by the object ID.
     *
     * @param auth       Access token to use for endpoint request.
     * @param workflowId object ID that will be passed on to the endpoint.
     */
    public Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * This method attempts to first find the workflow type id from the database, then it looks for
     * data coming from the WorkflowDb and WorkflowTypeDb tables to create a WorkflowListItem object
     * that is used by WorkflowDetailActivity's UI.
     *
     * @param token      Access token.
     * @param workflowId Workflow Id to use for querying data.
     */
    protected void getWorkflowFromDataSources(String token, int workflowId) {
        Disposable disposable = workflowDbDao
                .loadWorkflowTypeId(workflowId)
                .flatMap(workflowTypeId -> workflowDbDao
                        .getWorkflowDbBy(workflowId, workflowTypeId.getId()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workflowListItem -> {
                    if (workflowListItem == null) {
                        // TODO go to network
                        Log.d(TAG, "getWorkflowFromDataSources: ");
                    } else {
                        retrieveFromDbWorkflow.setValue(workflowListItem);
                    }
                }, throwable -> {
                    Log.d(TAG, "Error: " + throwable.getMessage());
                });

        disposables.add(disposable);
    }

    /**
     * Gets a PDF file (base64 encoded) for the specified Workflow.
     *
     * @param token      Access token to use for endpoint request.
     * @param workflowId object ID to retrieve the PDF file.
     */
    protected void getWorkflowPdfFile(String token, int workflowId) {
        Disposable disposable = service.getWorkflowPdfFile(token, workflowId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> exportPdfResponseLiveData.setValue(success), throwable -> {
                    Log.d(TAG, "exportPdfFile: " + throwable.getMessage());
                    errorLiveData.setValue(throwable);
                });
        disposables.add(disposable);
    }

    /**
     * Sets the open/closed status for a specific workflow.
     *
     * @param token      Access token to use for endpoint request.
     * @param workflowId single object ID to set the active status. The endpoint allows an array of
     *                   workflow IDs, but in this method we will only work with one workflow ID.
     * @param isOpen     whether to open or close the Workflow.
     */
    protected void postWorkflowActivationOpenClose(String token, int workflowId, boolean isOpen) {
        List<Integer> workflowIds = new ArrayList<>();
        workflowIds.add(workflowId);

        Disposable disposable = service.postWorkflowActivationOpenClose(token, workflowIds, isOpen)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> openCloseResponseLiveData.setValue(success), throwable -> {
                    Log.d(TAG, "postWorkflowActivationOpenClose: " + throwable.getMessage());
                    errorLiveData.setValue(throwable);
                });
        disposables.add(disposable);
    }

    /**
     * Sets the enabled/disabled status for a specific workflow.
     *
     * @param token      Access token to use for endpoint request.
     * @param workflowId single object ID to set the active status. The endpoint allows an array of
     *                   workflow IDs, but in this method we will only work with one workflow ID.
     * @param isEnabled  whether to open or close the Workflow.
     */
    protected void postWorkflowActivationEnableDisable(String token, int workflowId,
                                                       boolean isEnabled) {
        List<Integer> workflowIds = new ArrayList<>();
        workflowIds.add(workflowId);

        Disposable disposable = service
                .postWorkflowActivationEnableDisable(token, workflowIds, isEnabled)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> enableDisableResponseLiveData.setValue(success),
                        throwable -> {
                            Log.d(TAG, "postWorkflowActivationEnableDisable: " + throwable
                                    .getMessage());
                            errorLiveData.setValue(throwable);
                        });
        disposables.add(disposable);
    }

    protected LiveData<ExportPdfResponse> getExportPdfResponse() {
        if (exportPdfResponseLiveData == null) {
            exportPdfResponseLiveData = new MutableLiveData<>();
        }
        return exportPdfResponseLiveData;
    }

    protected LiveData<Throwable> getErrorLiveData() {
        if (errorLiveData == null) {
            errorLiveData = new MutableLiveData<>();
        }
        return errorLiveData;
    }

    protected LiveData<WorkflowListItem> getObservableRetrieveFromDbWorkflow() {
        if (retrieveFromDbWorkflow == null) {
            retrieveFromDbWorkflow = new MutableLiveData<>();
        }
        return retrieveFromDbWorkflow;
    }

    protected LiveData<WorkflowActivationResponse> getOpenCloseResponse() {
        if (openCloseResponseLiveData == null) {
            openCloseResponseLiveData = new MutableLiveData<>();
        }
        return openCloseResponseLiveData;
    }

    protected LiveData<WorkflowActivationResponse> getEnableDisableResponse() {
        if (enableDisableResponseLiveData == null) {
            enableDisableResponseLiveData = new MutableLiveData<>();
        }
        return enableDisableResponseLiveData;
    }
}
