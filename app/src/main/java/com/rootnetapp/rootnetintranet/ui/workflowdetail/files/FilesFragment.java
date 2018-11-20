package com.rootnetapp.rootnetintranet.ui.workflowdetail.files;

import android.content.Context;
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
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.DocumentsAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class FilesFragment extends Fragment {

    @Inject
    FilesViewModelFactory filesViewModelFactory;
    FilesViewModel filesViewModel;
    private FragmentWorkflowDetailFilesBinding mBinding;
    private WorkflowListItem mWorkflowListItem;
    private String mToken;

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

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        mToken = "Bearer " + prefs.getString("token", "");

        subscribe();
        filesViewModel.initDetails(mToken, mWorkflowListItem);

        //todo user actions

        return view;
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        filesViewModel.getObservableError().observe(this, errorObserver);

        filesViewModel.showLoading.observe(this, this::showLoading);
        filesViewModel.setDocumentsView.observe(this, this::setDocumentsView);
        filesViewModel.setTemplateTitleWith.observe(this, this::setTemplateTitleWith);
        filesViewModel.showTemplateDocumentsUi.observe(this, this::showTemplateDocumentsUi);
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
//            mBinding.tvFileUploaded.setVisibility(View.VISIBLE); //todo only show when a file was uploaded
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
    private void setDocumentsView(List<DocumentsFile> documents) {
        DocumentsAdapter documentsAdapter = new DocumentsAdapter(
                filesViewModel.getPresets(),
                documents
        );
        mBinding.rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvFiles.setAdapter(documentsAdapter);
    }
}