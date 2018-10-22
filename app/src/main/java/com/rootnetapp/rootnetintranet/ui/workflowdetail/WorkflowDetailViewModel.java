package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.icu.text.IDNA;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.models.createworkflow.SpecificApprovers;
import com.rootnetapp.rootnetintranet.models.createworkflow.StatusSpecific;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.requests.files.WorkflowPresetsRequest;
import com.rootnetapp.rootnetintranet.models.responses.attach.AttachResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.models.responses.file.FilesResponse;
import com.rootnetapp.rootnetintranet.models.responses.templates.Templates;
import com.rootnetapp.rootnetintranet.models.responses.templates.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Preset;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Approver;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ApproverHistory;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Step;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.Information;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.InformationAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.TYPE_LIST;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.TYPE_PRODUCT;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.TYPE_ROLE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.TYPE_SERVICE;

public class WorkflowDetailViewModel extends ViewModel implements  FormSettings.FormSettingsViewModelDelegate{
    private MutableLiveData<List<Comment>> mCommentsLiveData;
    private MutableLiveData<Comment> mCommentLiveData;
    private MutableLiveData<Boolean> mAttachLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private WorkflowDetailRepository repository;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<String> setCommentHeaderCounter;
    protected MutableLiveData<Boolean> showImportantInfoSection;
    protected MutableLiveData<List<Step>> loadImportantInfoSection;
    protected MutableLiveData<Boolean> showTemplateDocumentsUi;
    protected MutableLiveData<String> setTemplateTitleWith;
    protected MutableLiveData<List<DocumentsFile>> setDocumentsView;
    protected MutableLiveData<List<Approver>> updateCurrentApproversList;
    protected MutableLiveData<List<String>> updateApproveSpinner;
    protected MutableLiveData<Boolean> hideApproveSpinnerOnEmptyData;
    protected MutableLiveData<Boolean> hideApproverListOnEmptyData;
    protected MutableLiveData<List<ProfileInvolved>> updateProfilesInvolved;
    protected MutableLiveData<Boolean> hideProfilesInvolvedList;
    protected MutableLiveData<Boolean> hideGlobalApprovers;
    protected MutableLiveData<Boolean> hideSpecificApprovers;
    protected MutableLiveData<List<ProfileInvolved>> updateGlobalApproverList;
    protected MutableLiveData<List<ProfileInvolved>> updateSpecificApproverList;
    protected MutableLiveData<List<ApproverHistory>> updateApprovalHistoryList;
    protected MutableLiveData<Boolean> hideHistoryApprovalList;
    protected MutableLiveData<Boolean> setWorkflowIsOpen;
    protected MutableLiveData<List<Information>> updateInformationListUi;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private boolean isPrivateComment = false;
    private String token;
    private WorkflowListItem workflowListItem; // in DB but has limited data about the workflow.
    private WorkflowDb workflow; // Not in DB and more complete response from network.

    private static final String TAG = "DetailViewModel";
    private static final String format = "MMM d, y - h:m a";

    FormSettings formSettings;

    public WorkflowDetailViewModel(WorkflowDetailRepository workflowDetailRepository) {
        this.repository = workflowDetailRepository;
        this.showLoading = new MutableLiveData<>();
        this.setCommentHeaderCounter = new MutableLiveData<>();
        this.showImportantInfoSection = new MutableLiveData<>();
        this.loadImportantInfoSection = new MutableLiveData<>();
        this.showTemplateDocumentsUi = new MutableLiveData<>();
        this.setTemplateTitleWith = new MutableLiveData<>();
        this.setDocumentsView = new MutableLiveData<>();
        this.updateCurrentApproversList = new MutableLiveData<>();
        this.updateApproveSpinner = new MutableLiveData<>();
        this.hideApproveSpinnerOnEmptyData = new MutableLiveData<>();
        this.hideApproverListOnEmptyData = new MutableLiveData<>();
        this.updateProfilesInvolved = new MutableLiveData<>();
        this.hideProfilesInvolvedList = new MutableLiveData<>();
        this.hideGlobalApprovers = new MutableLiveData<>();
        this.hideSpecificApprovers = new MutableLiveData<>();
        this.updateGlobalApproverList = new MutableLiveData<>();
        this.updateSpecificApproverList = new MutableLiveData<>();
        this.updateApprovalHistoryList = new MutableLiveData<>();
        this.hideHistoryApprovalList = new MutableLiveData<>();
        this.setWorkflowIsOpen = new MutableLiveData<>();
        this.updateInformationListUi = new MutableLiveData<>();
        this.formSettings = new FormSettings();
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    protected void initDetails(String token, WorkflowListItem workflow) {
        this.token = token;
        this.workflowListItem = workflow;
        getWorkflow(this.token, this.workflowListItem.getWorkflowId());

        getComments(this.token, this.workflowListItem.getWorkflowId());
    }

    protected void commentIsPrivate(boolean isPrivate) {
        this.isPrivateComment = isPrivate;
    }

    protected List<Preset> getPresets() {
        return presets;
    }


    private void getWorkflowType(String auth, int typeId) {
        Disposable disposable = repository
                .getWorkflowType(auth, typeId)
                .subscribe(this::onTypeSuccess, throwable -> {
                    onFailure(throwable);
                });
        disposables.add(disposable);
    }

    private void getWorkflow(String auth, int workflowId) {
        Disposable disposable = repository
                .getWorkflow(auth, workflowId)
                .subscribe(this::onWorkflowSuccess, throwable -> {
                    onFailure(throwable);
                });
        disposables.add(disposable);
    }

    private void getTemplate(String auth, int templateId) {
        Disposable disposable = repository
                .getTemplate(auth, templateId)
                .subscribe(this::onTemplateSuccess,
                        throwable -> {
                            onFailure(throwable);
                        });

        disposables.add(disposable);
    }

    protected void getFiles(String auth, int workflowId) {
        Disposable disposable = repository
                .getFiles(auth, workflowId)
                .subscribe(this::onFilesSuccess, throwable -> {
                    onFailure(throwable);
                });
        disposables.add(disposable);
    }

    private void getComments(String auth, int workflowId) {
        Disposable disposable = repository
                .getComments(auth, workflowId)
                .subscribe(this::onCommentsSuccess, throwable -> {
                    onFailure(throwable);
                });
        disposables.add(disposable);
    }

    public void postComment(String comment, List<CommentFile> files) {
        showLoading.setValue(true);
        Disposable disposable = repository
                .postComment(
                        token,
                        workflowListItem.getWorkflowId(),
                        comment,
                        isPrivateComment,
                        files)
                .subscribe(this::onPostCommentSuccess,
                        this::onFailure);
        disposables.add(disposable);
    }

    public void attachFile(String auth, List<WorkflowPresetsRequest> request, CommentFile fileRequest) {
        Disposable disposable = repository
                .attachFile(auth, request, fileRequest)
                .subscribe(this::onAttachSuccess, throwable-> {
                    onFailure(throwable);
                });
        disposables.add(disposable);
    }

    private void updateCommentCounterHeader(int counter) {
        setCommentHeaderCounter.setValue("(" + String.valueOf(counter) + ")");
    }

    /**
     * Finds the a Status from the Status List in the current WorkflowType object.
     *
     * @param statusId Status id to find.
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

    private List<Preset> presets;
    private void getTemplateBy(int templateId) {
        if (templateId < 1) {
            showTemplateDocumentsUi.setValue(false);
            return;
        }
        getTemplate(token, templateId);
    }

    private void setNextApprovalSection() {
        if (workflow == null || currentWorkflowType == null) {
            return;
        }


    }

    private WorkflowTypeDb currentWorkflowType;
    private Status currentStatus;

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

        currentStatus = findStatusInListBy(workflowListItem.getCurrentStatus());
        setImportantInfoSection(currentStatus);
        getTemplateBy(currentWorkflowType.getTemplateId());
        this.presets = currentWorkflowType.getPresets();

        updateWorkflowInformation(workflow, currentWorkflowType);


        // Update current approvers list on UI.
        List<Approver> typeConfigurationApprovers = currentStatus.getApproversList();
        SpecificApprovers currentSpecificApprovers = workflow.getCurrentSpecificApprovers();

        updateCurrentApproverUi(typeConfigurationApprovers, currentSpecificApprovers);

        // Update approval spinner.
        List<Integer> nextStatusIds = workflow.getCurrentStatusRelations();
        updateApproveSpinnerUi(workflow, nextStatusIds);
    }



    /**
     * Updates the info section UI for this workflow.
     * @param workflow Workflow with info to display on the UI.
     */
    private void updateWorkflowInformation(WorkflowDb workflow, WorkflowTypeDb workflowTypeDb) {
        List<Information> informationList = new ArrayList<>();

        String startDate = Utils.serverFormatToFormat(workflow.getStart(), format);
        String endDate = Utils.serverFormatToFormat(workflow.getEnd(), format);

        Information info = new Information(R.string.title, workflow.getTitle());
        informationList.add(info);
        info = new Information(R.string.description, workflow.getDescription());
        informationList.add(info);
        info = new Information(R.string.start_date, startDate);
        informationList.add(info);
        info = new Information(R.string.end_date, endDate);
        informationList.add(info);

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


                info = formSettings.formatStringToObject(meta, config, this);

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

    @Override
    public void findInNetwork(Object value, Information information, FieldConfig fieldConfig){

        // TODO make new observers for receiving new items to ADD to the informatino list recycler view.
        // we need to tell what change in the set basically the new items added during the first run

        switch (fieldConfig.getTypeInfo().getType()) {
            case TYPE_ROLE:
                // TODO request to the network the names for those ids in value object.
                // TODO check if it is multiple or not, value can be an int or array of int.





                break;
            case TYPE_PRODUCT:
                // TODO request to the network the names for those ids in value object.
                // TODO check if it is multiple or not, value can be an int or array of int.





                break;

            case TYPE_LIST:






                break;

            case TYPE_SERVICE:
                break;




        }













    }

    /**
     * Updates UI section for current approvers. If this list is empty it will hide its recycler view.
     *
     * @param typeConfigurationApprovers
     * @param currentSpecificApprovers
     */
    private void updateCurrentApproverUi(List<Approver> typeConfigurationApprovers, SpecificApprovers currentSpecificApprovers) {
        List<Approver> result = new ArrayList<>();

        if (typeConfigurationApprovers.size() > 0) {
            result = typeConfigurationApprovers;
        }

        List<Integer> globalList = currentSpecificApprovers.global;
        List<StatusSpecific> statusSpecificList = currentSpecificApprovers.statusSpecific;

        // TODO look for RxJava chaining instead of calling multiple functions.
        generateApproverListForProfileIds(globalList, statusSpecificList, result);
    }

    private void generateApproverListForProfileIds(List<Integer> globalList,  List<StatusSpecific> statusSpecificList, List<Approver> approverList) {
        if (globalList == null || globalList.size() < 1) {
            generateApproverListForStatusSpecificIds(statusSpecificList, approverList);
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            Approver approver;
            ProfileInvolved profileInvolved;
            for (int i = 0; i < globalList.size(); i++) {
                profileInvolved = repository.getProfileBy(globalList.get(i));
                if (profileInvolved == null) {
                    continue;
                }
                approver = generateApproverWith(profileInvolved);
                approverList.add(approver);
            }
            return approverList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(approverListResult -> {
                    generateApproverListForStatusSpecificIds(statusSpecificList, approverListResult);
                }, throwable -> {
                    generateApproverListForStatusSpecificIds(statusSpecificList, approverList);
                    Log.d(TAG, "updateProfilesInvolvedUi: Something went wrong - " + throwable.getMessage());
                });

        disposables.add(disposable);
    }

    public Approver generateApproverWith(ProfileInvolved profileInvolved) {
        Approver approver = new Approver();
        approver.isRequire = false;
        approver.entityAvatar = profileInvolved.picture;
        approver.canChangeMind = false;
        approver.entityName = profileInvolved.fullName;
        return approver;
    }

    private void generateApproverListForStatusSpecificIds(List<StatusSpecific> statusSpecificList, List<Approver> approverList) {
        if (statusSpecificList == null || statusSpecificList.size() < 1) {
            if (approverList.size() < 1) {
                hideApproverListOnEmptyData.setValue(true);
            } else {
                updateCurrentApproversList.setValue(approverList);
            }
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            Approver approver;
            ProfileInvolved profileInvolved;
            for (int i = 0; i < statusSpecificList.size(); i++) {
                profileInvolved = repository.getProfileBy(statusSpecificList.get(i).user);
                if (profileInvolved == null) {
                    continue;
                }
                approver = generateApproverWith(profileInvolved);
                approverList.add(approver);
            }
            return approverList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(approverListResult -> {
                    if (approverListResult.size() < 1) {
                        hideApproverListOnEmptyData.setValue(true);
                    } else {
                        updateCurrentApproversList.setValue(approverListResult);
                    }
                }, throwable -> {
                    if (approverList.size() < 1) {
                        hideApproverListOnEmptyData.setValue(true);
                    } else {
                        updateCurrentApproversList.setValue(approverList);
                    }
                    Log.d(TAG, "updateProfilesInvolvedUi: Something went wrong - " + throwable.getMessage());
                });

        disposables.add(disposable);
    }

    /**
     * Update the spinner in the Next Approvers section. This spinner is used to approve or reject
     * a status. It may send an empty list or all the necessary names for the spinner.
     *
     * @param workflow Current workflow.
     * @param nextStatusIds List of ids specified by a Workflow in order to look in a WorkflowType.
     */
    private void updateApproveSpinnerUi(WorkflowDb workflow, List<Integer> nextStatusIds) {
        if (!workflow.isLoggedIsApprover()) {
            hideApproveSpinnerOnEmptyData.setValue(true);
            return;
        }
        List<String> nextStatusList = new ArrayList<>();
        if (nextStatusIds.size() < 1) {
            hideApproveSpinnerOnEmptyData.setValue(true);
            return;
        }

        Status status;
        String name;
        for (int i = 0; i < nextStatusIds.size(); i++) {
            status = findStatusInListBy(nextStatusIds.get(i));
            if (status == null) {
                continue;
            }
            name = status.getName();
            if (name == null) {
                continue;
            }
            nextStatusList.add(name);
        }

        updateApproveSpinner.setValue(nextStatusList);
    }


    /**
     * Handles success when requesting for a workflow by id to the endpoint.
     *
     * @param workflowResponse Network response with workflow data.
     */
    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        getWorkflowType(this.token, this.workflowListItem.getWorkflowTypeId());
        workflow = workflowResponse.getWorkflow();
        setWorkflowIsOpen.setValue(workflow.isOpen());

        updateProfilesInvolvedUi(workflow.getProfilesInvolved());

        SpecificApprovers approvers = workflow.getSpecificApprovers();
        updateApproverSpecificListUi(approvers.global, GLOBAL_APPROVER_TYPE);

        List<Integer> statusSpecific = new ArrayList<>();
        for (int i = 0; i < approvers.statusSpecific.size(); i++) {
            statusSpecific.add(approvers.statusSpecific.get(i).user);
        }

        updateApproverSpecificListUi(statusSpecific, STATUS_SPECIFIC_APPROVER_TYPE);
        updateApproverHistoryListUi(workflow.getWorkflowApprovalHistory());

    }


    private void updateApproverHistoryListUi(List<ApproverHistory> approverHistoryList) {
        if (approverHistoryList == null || approverHistoryList.size() < 1) {
            hideHistoryApprovalList.setValue(true);
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            ApproverHistory approverHistory;
            ProfileInvolved profileInvolved;

            for (int i = 0; i < approverHistoryList.size(); i++) {
                approverHistory = approverHistoryList.get(i);
                profileInvolved = repository.getProfileBy(approverHistory.approverId);
                if (profileInvolved == null) {
                    continue;
                }
                approverHistory.avatarPicture = profileInvolved.picture;
                return  approverHistoryList;
            }
            return approverHistoryList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(approverHistories -> {
                    updateApprovalHistoryList.setValue(approverHistories);
                }, throwable -> {
                    hideHistoryApprovalList.setValue(true);
                    Log.d(TAG, "updateProfilesInvolvedUi: Something went wrong - " + throwable.getMessage());
                });

        disposables.add(disposable);
    }


    public static final int GLOBAL_APPROVER_TYPE = 0;
    public static final int STATUS_SPECIFIC_APPROVER_TYPE = 1;

    private void updateApproverSpecificListUi(List<Integer> approverList, int approverType) {
        if (approverList == null || approverList.size() < 1) {
            if (approverType == GLOBAL_APPROVER_TYPE) {
                hideGlobalApprovers.setValue(true);
            } else if (approverType == STATUS_SPECIFIC_APPROVER_TYPE) {
                hideSpecificApprovers.setValue(true);
            }
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            ProfileInvolved profileInvolved;
            List<ProfileInvolved> profileInvolvedList = new ArrayList<>();
            for (int i = 0; i < approverList.size(); i++) {
                profileInvolved = repository.getProfileBy(approverList.get(i));
                if (profileInvolved == null) {
                    continue;
                }
                profileInvolvedList.add(profileInvolved);
            }
            return profileInvolvedList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profileInvolvedList -> {
                    if (approverType == STATUS_SPECIFIC_APPROVER_TYPE) {
                        updateSpecificApproverList.setValue(profileInvolvedList);
                    } else if (approverType == GLOBAL_APPROVER_TYPE) {
                        updateGlobalApproverList.setValue(profileInvolvedList);
                    }
                }, throwable -> {
                    if (approverType == GLOBAL_APPROVER_TYPE) {
                        hideGlobalApprovers.setValue(true);
                    } else if (approverType == STATUS_SPECIFIC_APPROVER_TYPE) {
                        hideSpecificApprovers.setValue(true);
                    }
                    Log.d(TAG, "updateProfilesInvolvedUi: Something went wrong - " + throwable.getMessage());
                });

        disposables.add(disposable);
    }

    /**
     * Given some profile ids it will look in the profiles tables in the local database for matching
     * Profiles, and return a ProfileInvolved object with limited profile information for the UI.
     * It will look for those profiles in the background thread.
     *
     * @param profilesId List of profiles to look in the database.
     */
    private void updateProfilesInvolvedUi(List<Integer> profilesId) {
        if (profilesId == null || profilesId.size() < 1) {
            hideProfilesInvolvedList.setValue(true);
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            List<ProfileInvolved> profilesList = new ArrayList<>();
            ProfileInvolved profileInvolved;
            for (int i = 0; i < profilesId.size(); i++) {
                profileInvolved = repository.getProfileBy(profilesId.get(i));
                if (profileInvolved == null) {
                    continue;
                }
                profilesList.add(profileInvolved);
            }
            return profilesList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setProfilesInvovledOnUi, throwable -> {
                    Log.d(TAG, "updateProfilesInvolvedUi: Something went wrong - " + throwable.getMessage());
                });
        disposables.add(disposable);
    }

    /**
     * Sends back to the View a list of profiles that are involved to the current workflow.
     *
     * @param profiles Profiles to be used for UI list.
     */
    private void setProfilesInvovledOnUi(List<ProfileInvolved> profiles) {
        if (profiles == null || profiles.size() < 1) {
            hideProfilesInvolvedList.setValue(true);
            return;
        }
        updateProfilesInvolved.setValue(profiles);
    }

    private void onTemplateSuccess(TemplatesResponse templatesResponse) {
        Templates templates = templatesResponse.getTemplates();
        if (templates == null) {
            return;
        }

        setTemplateTitleWith.setValue(templates.getName());
        getFiles(token, this.workflowListItem.getWorkflowId());
    }

    private void onFilesSuccess(FilesResponse filesResponse) {
        List<DocumentsFile> documents = filesResponse.getList();
        if (documents == null) {
            return;
        }
        setDocumentsView.setValue(documents);
    }

    // TODO Remove when we finally have comments List in ViewModel and NOT in Fragment.
    private int commentsCounter = 0;

    private void onCommentsSuccess(CommentsResponse commentsResponse) {
        List<Comment> comments = commentsResponse.getResponse();

        if (comments == null) {
            showLoading.setValue(false);
            return;
        }
        commentsCounter = comments.size();
        updateCommentCounterHeader(comments.size());
        mCommentsLiveData.setValue(commentsResponse.getResponse());
        showLoading.setValue(false);
    }

    private void onPostCommentSuccess(CommentResponse commentResponse) {
        commentsCounter += 1;
        updateCommentCounterHeader(commentsCounter);
        mCommentLiveData.setValue(commentResponse.getResponse());
        showLoading.setValue(false);
    }

    private void onAttachSuccess(AttachResponse attachResponse) {
        mAttachLiveData.setValue(true);
    }

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    public LiveData<List<Comment>> getObservableComments() {
        if (mCommentsLiveData == null) {
            mCommentsLiveData = new MutableLiveData<>();
        }
        return mCommentsLiveData;
    }

    public LiveData<Comment> getObservableComment() {
        if (mCommentLiveData == null) {
            mCommentLiveData = new MutableLiveData<>();
        }
        return mCommentLiveData;
    }

    public LiveData<Boolean> getObservableAttach() {
        if (mAttachLiveData == null) {
            mAttachLiveData = new MutableLiveData<>();
        }
        return mAttachLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

}
