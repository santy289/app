package com.rootnetapp.rootnetintranet.ui.workflowdetail;


import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.profile.ProfileDao;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.files.WorkflowPresetsRequest;
import com.rootnetapp.rootnetintranet.models.responses.attach.AttachResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.file.FilesResponse;
import com.rootnetapp.rootnetintranet.models.responses.templates.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowdetail.WorkflowApproveRejectResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowDetailRepository {
    private ApiInterface service;
    private ProfileDao profileDao;
    private WorkflowTypeDbDao workflowTypeDbDao;

    private MutableLiveData<WorkflowApproveRejectResponse> responseApproveRejection;
    private MutableLiveData<Boolean> showLoading;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "DetailRepository";

    public WorkflowDetailRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.profileDao = database.profileDao();
        this.workflowTypeDbDao = database.workflowTypeDbDao();
    }

    /**
     * Gets a profile that is involved to a workflow. Calls ProfileDao object and it is necessary
     * to call in the background. Otherwise will throw and error if it is run on the foreground.
     *
     * @param id Id from Workflow listed as profile involved.
     * @return Returns a ProfileInvolved object with the necessary data.
     */
    public ProfileInvolved getProfileBy(int id) {
        return profileDao.getProfilesInvolved(id);
    }

    public Observable<WorkflowTypeResponse> getWorkflowType(String auth, int typeId) {
        return service.getWorkflowType(auth, typeId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TemplatesResponse> getTemplate(String auth, int templateId) {
        return service.getTemplate(auth, templateId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FilesResponse> getFiles(String auth, int workflowId) {
        return service.getFiles(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentsResponse> getComments(String auth, int workflowId) {
        return service.getComments(auth, workflowId, 10, 1).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentResponse> postComment(String auth, int workflowId, String comment, boolean isPrivate, List<CommentFile> files) {
        return service.postComment(
                auth,
                workflowId,
                comment,
                isPrivate,
                files)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AttachResponse> attachFile(String auth, List<WorkflowPresetsRequest> request, CommentFile fileRequest) {
        return service.attachFile(auth, request, fileRequest).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    void approveWorkflow(String token, int workflowId, boolean isApproved, int nextStatus) {
        Disposable disposable = service.postApproveReject(
                token,
                workflowId,
                isApproved,
                nextStatus
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    responseApproveRejection.postValue(success);
                }, throwable -> {
                    Log.d(TAG, "approveWorkflow: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    void getWorkflowTypeFromDb(int workflowTypeId) {
        Disposable disposable = workflowTypeDbDao.getWorkflowTypeBy(workflowTypeId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workflowTypeDb -> {
                    // TODO call back to calling ViewModel using a LiveData.
                }, throwable -> {
                    Log.d(TAG, "approveWorkflow: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }



    protected void clearDisposables() {
        disposables.clear();
    }

    protected LiveData<WorkflowApproveRejectResponse> getApproveRejectResponse() {
        if (responseApproveRejection == null) {
            responseApproveRejection = new MutableLiveData<>();
        }
        return responseApproveRejection;
    }

    protected LiveData<Boolean> getErrorShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }

}
