package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
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

    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<WorkflowActivationResponse> activationResponseLiveData;
    private MutableLiveData<ExportPdfResponse> exportPdfResponseLiveData;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "WorkflowDetailRepo";

    public WorkflowDetailRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
    }

    protected void clearDisposables() {
        disposables.clear();
    }

    public Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Sets the active status (open/closed) for a specific workflow.
     *
     * @param token      Access token to use for endpoint request.
     * @param workflowId single object ID to set the active status. The endpoint allows an array of
     *                   workflow IDs, but in this method we will only work with one workflow ID.
     * @param isOpen     whether to open or close the Workflow.
     */
    protected void postWorkflowActivation(String token, int workflowId, boolean isOpen) {
        List<Integer> workflowIds = new ArrayList<>();
        workflowIds.add(workflowId);

        Disposable disposable = service.postWorkflowActivation(
                token,
                workflowIds,
                isOpen
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> activationResponseLiveData.setValue(success), throwable -> {
                    Log.d(TAG, "activateWorkflow: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    /**
     * Gets a PDF file for the specified Workflow.
     *
     * @param token      Access token to use for endpoint request.
     * @param workflowId object ID to retrieve the PDF file.
     */
    protected void getWorkflowPdfFile(String token, int workflowId) {
        Disposable disposable = service.getWorkflowPdfFile(
                token,
                workflowId
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> exportPdfResponseLiveData.setValue(success), throwable -> {
                    Log.d(TAG, "activateWorkflow: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    protected LiveData<WorkflowActivationResponse> getActivationResponse() {
        if (activationResponseLiveData == null) {
            activationResponseLiveData = new MutableLiveData<>();
        }
        return activationResponseLiveData;
    }

    protected LiveData<ExportPdfResponse> getExportPdfResponse() {
        if (exportPdfResponseLiveData == null) {
            exportPdfResponseLiveData = new MutableLiveData<>();
        }
        return exportPdfResponseLiveData;
    }

    protected LiveData<Boolean> getErrorShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }

}
