package com.rootnetapp.rootnetintranet.ui.workflowdetail.files;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailFilesBinding;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailViewModel;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.files.adapters.DocumentsAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import static com.rootnetapp.rootnetintranet.ui.workflowdetail.files.FilesViewModel.REQUEST_FILE_TO_ATTACH;

public class FilesFragment extends Fragment {

    @Inject
    FilesViewModelFactory filesViewModelFactory;
    private FilesViewModel filesViewModel;
    private FragmentWorkflowDetailFilesBinding mBinding;
    private WorkflowListItem mWorkflowListItem;

    private DocumentsAdapter mDocumentsAdapter = null;

    private WorkflowDetailViewModel workflowDetailViewModel;

    public FilesFragment() {
        // Required empty public constructor
    }

    public static FilesFragment newInstance(WorkflowListItem item) {
        FilesFragment fragment = new FilesFragment();
        fragment.mWorkflowListItem = item;
        return fragment;
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
        String token = "Bearer " + prefs.getString("token", "");

        setOnClickListeners();
        subscribe();
        filesViewModel.initDetails(token, mWorkflowListItem);

        return view;
    }

    private void subscribe() {

        filesViewModel.getObservableToastMessage().observe(this, this::showToastMessage);
        filesViewModel.getObservableAttachSuccess().observe(this, this::handleAttachmentUiResponse);
        filesViewModel.getObservableFilesTabCounter().observe(this, this::updateTabCounter);
        filesViewModel.getObservableUploadedFileName().observe(this, this::setFileUploadedTextWith);
        filesViewModel.getObservableAttachButtonText().observe(this, this::setButtonAttachmentText);

        filesViewModel.showLoading.observe(this, this::showLoading);
        filesViewModel.setDocumentsView.observe(this, this::setDocumentsView);
        filesViewModel.setTemplateTitleWith.observe(this, this::setTemplateTitleWith);
        filesViewModel.showTemplateDocumentsUi.observe(this, this::showTemplateDocumentsUi);
    }

    private void setOnClickListeners() {
        mBinding.btnAttachment.setOnClickListener(v -> showFileChooser());
        mBinding.btnUpload.setOnClickListener(
                v -> filesViewModel.uploadFile(mDocumentsAdapter.totalDocuments));
    }

    /**
     * If there is no file selected, displays a native file chooser, the user must select which file
     * they wish to upload. In case that the file chooser cannot be opened, shows a Toast message.
     * Otherwise, clears the current selected file and allows the user to select a new file.
     */
    @UiThread
    private void showFileChooser() {

        if (filesViewModel.getFileRequest() == null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
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
     * Whether to display the templates list or not, the only scenario where this list would be
     * hidden includes an absence of templates.
     *
     * @param show whether to show the UI.
     */
    @UiThread
    private void showTemplateDocumentsUi(boolean show) {
        if (show) {
            mBinding.rvFiles.setVisibility(View.VISIBLE);
            mBinding.btnAttachment.setVisibility(View.VISIBLE);
            mBinding.btnUpload.setVisibility(View.VISIBLE);
//            mBinding.tvFileUploaded.setVisibility(View.VISIBLE); //only show when a file was attached
            mBinding.tvNoFiles.setVisibility(View.GONE);
        } else {
            mBinding.rvFiles.setVisibility(View.GONE);
            mBinding.btnAttachment.setVisibility(View.GONE);
            mBinding.btnUpload.setVisibility(View.GONE);
            mBinding.tvFileUploaded.setVisibility(View.GONE);
            mBinding.tvNoFiles.setVisibility(View.VISIBLE);
        }
    }

    @UiThread
    private void setTemplateTitleWith(String name) {
        String title = getString(R.string.template) + " " + name;
        mBinding.tvTitleFiles.setText(title);
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
                filesViewModel.getPresets(),
                documents
        );
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
}