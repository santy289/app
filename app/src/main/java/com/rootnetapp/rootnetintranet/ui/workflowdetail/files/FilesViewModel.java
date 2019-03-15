package com.rootnetapp.rootnetintranet.ui.workflowdetail.files;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.files.AttachFilesRequest;
import com.rootnetapp.rootnetintranet.models.requests.files.WorkflowPresetsRequest;
import com.rootnetapp.rootnetintranet.models.responses.attach.AttachResponse;
import com.rootnetapp.rootnetintranet.models.responses.downloadfile.DownloadFileResponse;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.models.responses.file.FilesResponse;
import com.rootnetapp.rootnetintranet.models.responses.templates.Templates;
import com.rootnetapp.rootnetintranet.models.responses.templates.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.presets.Preset;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_FILE_VIEW;

public class FilesViewModel extends ViewModel {

    private static final String TAG = "FilesViewModel";

    protected static final int REQUEST_FILE_TO_ATTACH = 555;
    protected static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 700;

    private FilesRepository mRepository;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private MutableLiveData<Integer> mToastMessageLiveData;
    private MutableLiveData<Boolean> mAttachSuccessLiveData;
    private MutableLiveData<Integer> mFilesTabCounter;
    private MutableLiveData<String> mUploadedFileNameLiveData;
    private MutableLiveData<Integer> mAttachButtonTextLiveData;
    private MutableLiveData<FileUiData> mOpenDownloadedFileLiveData;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<Boolean> showTemplateDocumentsUiEmpty;
    protected MutableLiveData<Boolean> showTemplateDocumentsUiPermissions;
    protected MutableLiveData<String> setTemplateTitleWith;
    protected MutableLiveData<List<DocumentsFile>> setDocumentsView;

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the workflow.
    private CommentFile mFileRequest;
    private List<Preset> mPresets;
    private DocumentsFile mDocumentFileToDownload;
    private Preset mPresetToDownload;
    private boolean hasViewPermissions;

    protected FilesViewModel(FilesRepository filesRepository) {
        this.mRepository = filesRepository;
        this.showLoading = new MutableLiveData<>();
        this.showTemplateDocumentsUiEmpty = new MutableLiveData<>();
        this.showTemplateDocumentsUiPermissions = new MutableLiveData<>();
        this.setTemplateTitleWith = new MutableLiveData<>();
        this.setDocumentsView = new MutableLiveData<>();
    }

    protected void initDetails(String token, WorkflowListItem workflow, String userId,
                               String userPermissions) {
        this.mToken = token;
        this.mWorkflowListItem = workflow;

        checkPermissions(userPermissions);

        if (hasViewPermissions) {
            getWorkflowType(mToken, mWorkflowListItem.getWorkflowTypeId());
        }
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
    }

    /**
     * Verifies all of the user permissions related to this ViewModel and {@link FilesFragment}.
     * Hide the UI related to the unauthorized actions.
     *
     * @param permissionsString users permissions.
     */
    private void checkPermissions(String permissionsString) {
        RootnetPermissionsUtils permissionsUtils = new RootnetPermissionsUtils(permissionsString);

        hasViewPermissions = permissionsUtils.hasPermission(WORKFLOW_FILE_VIEW);

        showTemplateDocumentsUiPermissions.setValue(hasViewPermissions);
    }

    protected List<Preset> getPresets() {
        return mPresets;
    }

    private void getTemplateBy(int templateId) {
        if (templateId < 1) {
            showTemplateDocumentsUiEmpty.setValue(false);
            return;
        }
        getTemplate(mToken, templateId);
    }

    private void getTemplate(String auth, int templateId) {
        Disposable disposable = mRepository
                .getTemplate(auth, templateId)
                .subscribe(this::onTemplateSuccess,
                        this::onFailure);

        mDisposables.add(disposable);
    }

    protected void getFiles(int workflowId) {
        Disposable disposable = mRepository
                .getFiles(mToken, workflowId)
                .subscribe(this::onFilesSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    private void onTemplateSuccess(TemplatesResponse templatesResponse) {
        Templates templates = templatesResponse.getTemplates();
        if (templates == null) {
            return;
        }

        setTemplateTitleWith.setValue(templates.getName());
        getFiles(this.mWorkflowListItem.getWorkflowId());
    }

    private void onFilesSuccess(FilesResponse filesResponse) {
        List<DocumentsFile> documents = filesResponse.getList();
        if (documents == null) {
            return;
        }
        setDocumentsView.setValue(documents);
        setFilesTabCounter(documents.size());
    }

    /**
     * Sets the queued preset to download. This is only used if the system is requesting
     * permissions.
     *
     * @param presetToDownload queued file.
     */
    protected void setPresetToDownload(Preset presetToDownload) {
        mPresetToDownload = presetToDownload;
    }

    /**
     * Sets the queued file to download. This is only used if the system is requesting permissions.
     *
     * @param documentsFile queued file.
     */
    protected void setDocumentFileToDownload(DocumentsFile documentsFile) {
        mDocumentFileToDownload = documentsFile;
    }

    /**
     * Checks if the requested permissions were granted and then proceed to export the PDF file.
     *
     * @param requestCode  to identify the request
     * @param grantResults array containing the request results.
     */
    protected void handleRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSIONS: {
                // check for both permissions
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permissions granted
                    if (mPresetToDownload != null) {
                        downloadPreset(mPresetToDownload);

                    } else if (mDocumentFileToDownload != null) {
                        downloadDocumentFile(mDocumentFileToDownload);

                    } else {
                        mToastMessageLiveData.setValue(R.string.error);
                    }

                    // clear the references
                    setPresetToDownload(null);
                    setDocumentFileToDownload(null);

                } else {
                    // at least one permission was denied
                    mToastMessageLiveData.setValue(
                            R.string.workflow_detail_files_fragment_permissions_not_granted);
                }
            }
        }
    }

    /**
     * Handles the result of the file chooser intent. Retrieves information about the selected file
     * and sends that info to the UI. Also, creates the FileRequest object that will be used in
     * {@link #uploadFile(List)}
     *
     * @param context     used to retrieve the file name and size.
     * @param requestCode ActivityResult requestCode.
     * @param resultCode  ActivityResult resultCode.
     * @param data        the file URI that was selected.
     */
    protected void handleFileSelectedResult(Context context, int requestCode, int resultCode,
                                            Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_TO_ATTACH:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();

                        if (uri == null) {
                            mToastMessageLiveData.setValue(R.string.select_file);
                            return;
                        }

                        Cursor returnCursor = context.getContentResolver()
                                .query(uri, null, null, null, null);

                        if (returnCursor == null) {
                            mToastMessageLiveData.setValue(R.string.error_selecting_file);
                            return;
                        }

                        returnCursor.moveToFirst();

                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        int size = (int) returnCursor.getLong(sizeIndex);

                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        String fileName = returnCursor.getString(nameIndex);

                        returnCursor.close();

                        byte[] bytes = Utils.fileToByte(context.getContentResolver(), uri);

                        String encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);
                        String fileType = Utils.getMimeType(data.getData(), context);

                        mUploadedFileNameLiveData.setValue(fileName);
                        mAttachButtonTextLiveData.setValue(R.string.remove_file);

                        mFileRequest = new CommentFile(encodedFile, fileType, fileName, size);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * This should be called every time the file request is no longer valid.
     */
    protected void clearFileRequest() {
        mFileRequest = null;
    }

    protected CommentFile getFileRequest() {
        return mFileRequest;
    }

    protected boolean isAnyPresetSelected(List<Preset> presetsList) {
        List<Integer> presets = new ArrayList<>();

        for (Preset preset : presetsList) {
            if (preset.isSelected()) {
                presets.add(preset.getId());
            }
        }

        if (presets.isEmpty()) {
            mToastMessageLiveData.setValue(R.string.select_preset);
            return false;
        }

        return true;
    }

    /**
     * Creates the object that will be sent to the endpoint to upload a file. For this to be
     * completed, the user must have selected a preset.
     *
     * @param presetsList all of the presets that are available, used to check which one the user
     *                    selected.
     */
    protected void uploadFile(List<Preset> presetsList) {
        if (mFileRequest == null) {
            mToastMessageLiveData.setValue(R.string.select_file);
            return;
        }

        AttachFilesRequest request = new AttachFilesRequest();
        List<WorkflowPresetsRequest> presetsRequestList = new ArrayList<>();
        List<Integer> presets = new ArrayList<>();

        for (Preset preset : presetsList) {
            if (preset.isSelected()) {
                presets.add(preset.getId());
            }
        }

        if (presets.isEmpty()) {
            mToastMessageLiveData.setValue(R.string.select_preset);
            return;
        }

        WorkflowPresetsRequest presetsRequest = new WorkflowPresetsRequest();
        presetsRequest.setWorkflowId(mWorkflowListItem.getWorkflowId());
        presetsRequest.setPresets(presets);
        presetsRequest.setFile(mFileRequest);
        presetsRequest.setPresetType(WorkflowPresetsRequest.PRESET_TYPE_FILE);
        presetsRequestList.add(presetsRequest);

        request.setWorkflows(presetsRequestList);

        attachFile(request);
    }

    private void attachFile(AttachFilesRequest request) {
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .attachFile(mToken, request)
                .subscribe(this::onAttachSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    private void onAttachSuccess(AttachResponse attachResponse) {
        showLoading.setValue(false);
        mAttachSuccessLiveData.setValue(attachResponse.getList().size() > 0);
        getFiles(mWorkflowListItem.getWorkflowId());
    }

    protected void downloadPreset(Preset preset) {
        if (preset.getPresetFile() == null) {
            mToastMessageLiveData
                    .setValue(R.string.workflow_detail_files_fragment_preset_unavailable);
            return;
        }

        downloadFile(preset.getPresetFile().getEntity().toLowerCase(Locale.US),
                preset.getPresetFile().getId());
    }

    protected void downloadDocumentFile(DocumentsFile documentsFile) {
        downloadFile(DocumentsFile.FILE_ENTITY, documentsFile.getId());
    }

    private void downloadFile(String entity, int fileId) {
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .downloadFile(mToken, entity, fileId)
                .subscribe(this::onDownloadSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    private void onDownloadSuccess(DownloadFileResponse downloadFileResponse) {
        showLoading.setValue(false);

        // the API will return a base64 string representing the file

        String base64 = downloadFileResponse.getFile().getContent();
        if (base64 == null || base64.isEmpty()) {
            mToastMessageLiveData.setValue(R.string.error);
            return;
        }

        String fileName = downloadFileResponse.getFile().getFilename();
        try {
            FileUiData fileUiData = new FileUiData(
                    Utils.decodeFileFromBase64Binary(base64, fileName),
                    downloadFileResponse.getFile().getMime());
            mOpenDownloadedFileLiveData.setValue(fileUiData);

        } catch (IOException e) {
            Log.e(TAG, "downloadFile: ", e);
            mToastMessageLiveData.setValue(R.string.error);
        }
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

    /**
     * Handles success response from endpoint when looking for a workflow type.
     *
     * @param response Incoming response from server.
     */
    private void onTypeSuccess(WorkflowTypeResponse response) {
        WorkflowTypeDb currentWorkflowType = response.getWorkflowType();
        if (currentWorkflowType == null) {
            return;
        }
        updateUIWithWorkflowType(currentWorkflowType);
    }

    private void updateUIWithWorkflowType(WorkflowTypeDb currentWorkflowType) {
        getTemplateBy(currentWorkflowType.getTemplateId());
        this.mPresets = currentWorkflowType.getPresets();
    }

    private void setFilesTabCounter(int counter) {
        mFilesTabCounter.setValue(counter);
    }

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
    }

    protected LiveData<Integer> getObservableToastMessage() {
        if (mToastMessageLiveData == null) {
            mToastMessageLiveData = new MutableLiveData<>();
        }
        return mToastMessageLiveData;
    }

    protected LiveData<Boolean> getObservableAttachSuccess() {
        if (mAttachSuccessLiveData == null) {
            mAttachSuccessLiveData = new MutableLiveData<>();
        }
        return mAttachSuccessLiveData;
    }

    protected LiveData<Integer> getObservableFilesTabCounter() {
        if (mFilesTabCounter == null) {
            mFilesTabCounter = new MutableLiveData<>();
        }
        return mFilesTabCounter;
    }

    protected LiveData<String> getObservableUploadedFileName() {
        if (mUploadedFileNameLiveData == null) {
            mUploadedFileNameLiveData = new MutableLiveData<>();
        }
        return mUploadedFileNameLiveData;
    }

    protected LiveData<Integer> getObservableAttachButtonText() {
        if (mAttachButtonTextLiveData == null) {
            mAttachButtonTextLiveData = new MutableLiveData<>();
        }
        return mAttachButtonTextLiveData;
    }

    protected LiveData<FileUiData> getObservableOpenDownloadedFile() {
        if (mOpenDownloadedFileLiveData == null) {
            mOpenDownloadedFileLiveData = new MutableLiveData<>();
        }
        return mOpenDownloadedFileLiveData;
    }
}
