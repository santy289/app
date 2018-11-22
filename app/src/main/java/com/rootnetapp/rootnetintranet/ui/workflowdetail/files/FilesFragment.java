package com.rootnetapp.rootnetintranet.ui.workflowdetail.files;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailFilesBinding;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.files.WorkflowPresetsRequest;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailViewModel;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.files.adapters.DocumentsAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import static android.app.Activity.RESULT_OK;

public class FilesFragment extends Fragment {

    private static final int FILE_SELECT_CODE = 555;

    @Inject
    FilesViewModelFactory filesViewModelFactory;
    private FilesViewModel filesViewModel;
    private FragmentWorkflowDetailFilesBinding mBinding;
    private WorkflowListItem mWorkflowListItem;

    private CommentFile fileRequest = null;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        //todo download the files

        return view;
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        final Observer<Boolean> attachObserver = ((Boolean data) -> {
            showLoading(false);
            if (data != null && data) {
                filesViewModel.getFiles(mWorkflowListItem.getWorkflowId());
            } else {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });

        filesViewModel.getObservableError().observe(this, errorObserver);
        filesViewModel.getObservableAttach().observe(this, attachObserver);
        filesViewModel.getObservableFilesTabCounter().observe(this, this::updateTabCounter);

        filesViewModel.showLoading.observe(this, this::showLoading);
        filesViewModel.setDocumentsView.observe(this, this::setDocumentsView);
        filesViewModel.setTemplateTitleWith.observe(this, this::setTemplateTitleWith);
        filesViewModel.showTemplateDocumentsUi.observe(this, this::showTemplateDocumentsUi);
    }

    private void setOnClickListeners() {
        mBinding.btnAttachment.setOnClickListener(v -> showFileChooser());
        mBinding.btnUpload.setOnClickListener(v -> uploadFiles());
    }

    private void uploadFiles() {

        if (fileRequest != null && mDocumentsAdapter != null) {
            List<WorkflowPresetsRequest> request = new ArrayList<>();
            List<Integer> presets = new ArrayList<>();
            int i = 0;
            for (Boolean isSelected : mDocumentsAdapter.isSelected) {
                if (isSelected) {
                    presets.add(mDocumentsAdapter.totalDocuments.get(i).getId());
                }
                i++;
            }
            if (presets.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.select_preset),
                        Toast.LENGTH_SHORT).show();
            } else {
                request.add(new WorkflowPresetsRequest(mWorkflowListItem.getWorkflowId(), presets));
                showLoading(true);
                filesViewModel.attachFile(request, fileRequest); //todo does not upload
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.select_file),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @UiThread
    private void showFileChooser() {

        if (fileRequest == null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(getContext(), "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            fileRequest = null;
            setButtonAttachmentText(getString(R.string.attach));
            setFileUploadedText(getString(R.string.uploaded_file));
            mBinding.tvFileUploaded.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        File file = new File(data.getData().toString());
                        byte[] bytes = Utils.fileToByte(file);
                        String fileName = file.getName();

                        mBinding.tvFileUploaded.setVisibility(View.VISIBLE);
                        setFileUploadedText(mBinding.tvFileUploaded.getText() + " " + fileName);
                        setButtonAttachmentText(getString(R.string.remove_file));

                        String encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);
                        String fileType = Utils.getMimeType(data.getData(), getContext());

                        fileRequest = new CommentFile(encodedFile, fileType, fileName,
                                (int) file.length());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
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

    @UiThread
    private void setFileUploadedText(String text) {
        mBinding.tvFileUploaded.setText(text);
    }

    @UiThread
    private void setButtonAttachmentText(String text) {
        mBinding.btnAttachment.setText(text);
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
}