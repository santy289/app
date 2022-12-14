package com.rootnetapp.rootnetintranet.ui.workflowdetail.files;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailFilesBinding;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.models.responses.workflows.presets.Preset;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailViewModel;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.files.adapters.DocumentsAdapter;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import static com.rootnetapp.rootnetintranet.ui.workflowdetail.files.FilesViewModel.REQUEST_EXTERNAL_STORAGE_PERMISSIONS;
import static com.rootnetapp.rootnetintranet.ui.workflowdetail.files.FilesViewModel.REQUEST_FILE_TO_ATTACH;

public class FilesFragment extends Fragment implements FilesFragmentInterface {

    @Inject
    FilesViewModelFactory filesViewModelFactory;
    private FilesViewModel filesViewModel;
    private FragmentWorkflowDetailFilesBinding mBinding;
    private WorkflowListItem mWorkflowListItem;

    private DocumentsAdapter mDocumentsAdapter = null;

    private WorkflowDetailViewModel workflowDetailViewModel;

    private static final String SAVE_WORKFLOW_TYPE = "SAVE_WORKFLOW_TYPE";


    public FilesFragment() {
        // Required empty public constructor
    }

    public static FilesFragment newInstance(WorkflowListItem item) {
        FilesFragment fragment = new FilesFragment();
        fragment.mWorkflowListItem = item;
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_WORKFLOW_TYPE, mWorkflowListItem);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mWorkflowListItem = savedInstanceState.getParcelable(SAVE_WORKFLOW_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_files, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        filesViewModel = ViewModelProviders
                .of(this, filesViewModelFactory)
                .get(FilesViewModel.class);

        if (getParentFragment() != null) {
            workflowDetailViewModel = ViewModelProviders
                    .of(getParentFragment())
                    .get(WorkflowDetailViewModel.class);
        }

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
        String loggedUserId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");

        setOnClickListeners();
        subscribe();
        filesViewModel.initDetails(token, mWorkflowListItem, loggedUserId, permissionsString);

        return view;
    }

    private void subscribe() {

        filesViewModel.getObservableToastMessage().observe(getViewLifecycleOwner(), this::showToastMessage);
        filesViewModel.getObservableAttachSuccess().observe(getViewLifecycleOwner(), this::handleAttachmentUiResponse);
        filesViewModel.getObservableFilesTabCounter().observe(getViewLifecycleOwner(), this::updateTabCounter);
        filesViewModel.getObservableUploadedFileName().observe(getViewLifecycleOwner(), this::setFileUploadedTextWith);
        filesViewModel.getObservableAttachButtonText().observe(getViewLifecycleOwner(), this::setButtonAttachmentText);
        filesViewModel.getObservableOpenDownloadedFile().observe(getViewLifecycleOwner(), this::openDownloadedFile);

        filesViewModel.showLoading.observe(getViewLifecycleOwner(), this::showLoading);
        filesViewModel.setDocumentsView.observe(getViewLifecycleOwner(), this::setDocumentsView);
        filesViewModel.setTemplateTitleWith.observe(getViewLifecycleOwner(), this::setTemplateTitleWith);
        filesViewModel.showTemplateDocumentsUiEmpty
                .observe(getViewLifecycleOwner(), this::showTemplateDocumentsUiEmpty);
        filesViewModel.showTemplateDocumentsUiPermissions
                .observe(getViewLifecycleOwner(), this::showTemplateDocumentsUiPermissions);
        filesViewModel.showDownloadTemplateButton
                .observe(getViewLifecycleOwner(), this::setShowDownloadTemplateButton);
        filesViewModel.showDownloadFileButton.observe(getViewLifecycleOwner(), this::setShowDownloadFileButton);
        filesViewModel.showAttachUploadFileButton.observe(getViewLifecycleOwner(), this::showAttachUploadFileButton);
    }

    private void setOnClickListeners() {
        mBinding.btnAttachment.setOnClickListener(v -> showFileChooser());
        mBinding.btnUpload.setOnClickListener(
                v -> {
                    if (mDocumentsAdapter == null) return;
                    filesViewModel.uploadFile(mDocumentsAdapter.totalDocuments);
                });
    }

    /**
     * If there is no file selected, displays a native file chooser, the user must select which file
     * they wish to upload. In case that the file chooser cannot be opened, shows a Toast message.
     * Otherwise, clears the current selected file and allows the user to select a new file.
     */
    @UiThread
    private void showFileChooser() {

        if (filesViewModel.getFileRequest() == null) {
            if (mDocumentsAdapter == null) return;

            boolean isPresetSelected = filesViewModel
                    .isAnyPresetSelected(mDocumentsAdapter.totalDocuments);
            if (!isPresetSelected) return;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");

            //specify multiple MIME types
            intent.putExtra(Intent.EXTRA_MIME_TYPES, Utils.ALLOWED_MIME_TYPES);

            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(
                        intent,
                        getString(R.string.workflow_detail_files_fragment_select_file)),
                        REQUEST_FILE_TO_ATTACH);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                showToastMessage(R.string.workflow_detail_files_fragment_no_file_manager);
            }
        } else {
            clearFileRequest();
        }
    }

    /**
     * Clears the current selected file by removing any references to it and enabling the user to
     * select a new file.
     */
    private void clearFileRequest() {
        filesViewModel.clearFileRequest();
        setButtonAttachmentText(R.string.attach);
        setFileUploadedTextWith(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        filesViewModel.handleFileSelectedResult(getContext(), requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    /**
     * Whether to display the templates list or not due to the absence of templates.
     *
     * @param show whether to show the UI.
     */
    @UiThread
    private void showTemplateDocumentsUiEmpty(boolean show) {
        mBinding.tvNoPermissions.setVisibility(View.INVISIBLE);

        if (show) {
            mBinding.rvFiles.setVisibility(View.VISIBLE);
//            mBinding.tvFileUploaded.setVisibility(View.VISIBLE); //only show when a file was attached
            mBinding.tvTitleFiles.setVisibility(View.VISIBLE);
            mBinding.viewTitleFiles.setVisibility(View.VISIBLE);
            mBinding.tvNoFiles.setVisibility(View.GONE);
        } else {
            mBinding.rvFiles.setVisibility(View.GONE);
            mBinding.tvFileUploaded.setVisibility(View.GONE);
            mBinding.tvNoFiles.setVisibility(View.VISIBLE);
            mBinding.tvTitleFiles.setVisibility(View.GONE);
            mBinding.viewTitleFiles.setVisibility(View.GONE);
        }
    }

    /**
     * Whether to display the templates list or not due to no view permissions
     *
     * @param show whether to show the UI.
     */
    @UiThread
    private void showTemplateDocumentsUiPermissions(boolean show) {
        mBinding.tvNoFiles.setVisibility(View.INVISIBLE);

        if (show) {
            mBinding.rvFiles.setVisibility(View.VISIBLE);
//            mBinding.tvFileUploaded.setVisibility(View.VISIBLE); //only show when a file was attached
            mBinding.tvTitleFiles.setVisibility(View.VISIBLE);
            mBinding.viewTitleFiles.setVisibility(View.VISIBLE);
            mBinding.tvNoPermissions.setVisibility(View.GONE);
        } else {
            mBinding.rvFiles.setVisibility(View.GONE);
            mBinding.tvFileUploaded.setVisibility(View.GONE);
            mBinding.tvTitleFiles.setVisibility(View.GONE);
            mBinding.viewTitleFiles.setVisibility(View.GONE);
            mBinding.tvNoPermissions.setVisibility(View.VISIBLE);
        }
    }

    @UiThread
    private void setTemplateTitleWith(String name) {
//        String title = getString(R.string.template) + " " + name;
//        mBinding.tvTitleFiles.setText(title);
    }

    /**
     * Concats the file name to a "File uploaded" title.
     *
     * @param fileName only the file name that was attached.
     */
    @UiThread
    private void setFileUploadedTextWith(String fileName) {
        mBinding.tvFileUploaded.setVisibility(fileName != null ? View.VISIBLE : View.GONE);

        String text = getString(R.string.uploaded_file) + " " + fileName;
        mBinding.tvFileUploaded.setText(text);
    }

    @UiThread
    private void setButtonAttachmentText(@StringRes int stringRes) {
        mBinding.btnAttachment.setText(getString(stringRes));
    }

    @UiThread
    private void setDocumentsView(List<DocumentsFile> documents) {
        mDocumentsAdapter = new DocumentsAdapter(
                this,
                filesViewModel.getPresets(),
                documents
        );
        mDocumentsAdapter
                .setShowTemplateDownloadButton(filesViewModel.hasViewPermissions());
        mDocumentsAdapter.setShowFileDownloadButton(filesViewModel.hasViewFilesPermissions());
        mBinding.rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvFiles.setAdapter(mDocumentsAdapter);
        mBinding.rvFiles.setNestedScrollingEnabled(false);
    }

    @UiThread
    private void updateTabCounter(Integer counter) {
        if (workflowDetailViewModel != null) {
            workflowDetailViewModel.setFilesCounter(counter);
        }
    }

    @UiThread
    private void handleAttachmentUiResponse(boolean success) {
        if (success) {
            clearFileRequest();
        } else {
            showToastMessage(R.string.error);
        }
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void downloadPreset(Preset preset) {
        if (checkExternalStoragePermissions()) {
            filesViewModel.downloadPreset(preset);
        } else {
            filesViewModel.setPresetToDownload(preset);

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSIONS);
        }
    }

    @Override
    public void downloadDocumentFile(DocumentsFile documentsFile) {
        if (checkExternalStoragePermissions()) {
            filesViewModel.downloadDocumentFile(documentsFile);
        } else {
            filesViewModel.setDocumentFileToDownload(documentsFile);

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSIONS);
        }
    }

    /**
     * Creates an {@link Intent} chooser the downloaded file. If the device is not suitable to read
     * the file, will display a {@link Toast} message. Uses a {@link FileProvider} to create the
     * file URI, instead of using the {@link Uri#fromFile(File)} method.
     *
     * @param fileUiData the file data containing the file to be opened.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v4/content/FileProvider">FileProvider</a>
     */
    @UiThread
    private void openDownloadedFile(FileUiData fileUiData) {
        if (fileUiData.getFile() == null) return;

        Intent target = new Intent(Intent.ACTION_VIEW);

        Uri fileUri = FileProvider.getUriForFile(getContext(),
                getContext().getApplicationContext().getPackageName() + ".fileprovider",
                fileUiData.getFile());

        target.setDataAndType(fileUri, fileUiData.getMimeType());
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target,
                getString(R.string.workflow_detail_files_fragment_open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here
            showToastMessage(R.string.workflow_detail_files_fragment_cannot_open_file);
        }
    }

    /**
     * Verify whether the user has granted permissions to read/write the external storage.
     *
     * @return whether the permissions are granted.
     */
    private boolean checkExternalStoragePermissions() {
        // Here, thisActivity is the current activity
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        filesViewModel.handleRequestPermissionsResult(requestCode, grantResults);
    }

    @UiThread
    private void setShowDownloadTemplateButton(boolean show) {
        if (mDocumentsAdapter == null) return;

        mDocumentsAdapter.setShowTemplateDownloadButton(show);
    }

    @UiThread
    private void setShowDownloadFileButton(boolean show) {
        if (mDocumentsAdapter == null) return;

        mDocumentsAdapter.setShowFileDownloadButton(show);
    }

    @UiThread
    private void showAttachUploadFileButton(boolean show) {
        mBinding.btnAttachment.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.btnUpload.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}