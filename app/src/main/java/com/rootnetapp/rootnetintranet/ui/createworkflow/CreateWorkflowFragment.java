package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentCreateWorkflowBinding;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.FileFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.adapters.FormItemsAdapter;
import com.rootnetapp.rootnetintranet.ui.createworkflow.dialog.DialogMessage;
import com.rootnetapp.rootnetintranet.ui.createworkflow.dialog.ValidateFormDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class CreateWorkflowFragment extends Fragment {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    private CreateWorkflowViewModel viewModel;
    private FragmentCreateWorkflowBinding mBinding;

    private static final String TAG = "CreateFragment";
    private final String FILE_CHOOSER_DIR = "/storage/emulated/legacy";

    private MenuItem uploadMenu;
    private FormItemsAdapter mAdapter;
    private WorkflowListItem mWorkflowListItem;

    public CreateWorkflowFragment() { }

    /**
     * Creates a new instance of this fragment with the create mode active.
     *
     * @return this fragment.
     */
    public static CreateWorkflowFragment newInstance() {
        return newInstance(null);
    }

    /**
     * Creates a new instance of this fragment with the edit mode active.
     *
     * @param itemToEdit the workflow that will be edited.
     *
     * @return this fragment.
     */
    public static CreateWorkflowFragment newInstance(@Nullable WorkflowListItem itemToEdit) {
        CreateWorkflowFragment fragment = new CreateWorkflowFragment();
        fragment.mWorkflowListItem = itemToEdit;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_create_workflow,
                container,
                false
        );
        View view = mBinding.getRoot();

        ((RootnetApp) getActivity().getApplication()).getAppComponent().
                inject(this);

        viewModel = ViewModelProviders
                .of(this, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);
        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        setupSubmitButton();
        setOnClickListeners();
        setupFormRecycler();
        subscribe();

        viewModel.initForm(token, mWorkflowListItem);

        return view;
    }

    private void subscribe() {

        viewModel.getObservableAddWorkflowTypeItem().observe(this, this::addWorkflowTypeItem);
        viewModel.getObservableAddFormItem().observe(this, this::addItemToForm);
        viewModel.getObservableSetFormItemList().observe(this, this::setItemListToForm);
        viewModel.getObservableValidationUi().observe(this, this::updateValidationUi);
        viewModel.getObservableShowLoading().observe(this, this::showLoading);
        viewModel.getObservableShowDialogMessage().observe(this, this::showDialog);
        viewModel.getObservableGoBack().observe(this, back -> goBack());

        viewModel.showUploadButton.observe(this, this::setUploadMenu);

        viewModel.chooseFile.observe(this, choose -> chooseFile());

    }

    private void setupSubmitButton() {
        mBinding.btnCreate.setText(
                mWorkflowListItem == null ? R.string.create_workflow : R.string.edit_workflow);
    }

    private void setupFormRecycler() {
        mAdapter = new FormItemsAdapter(getContext(), getChildFragmentManager(), new ArrayList<>());
        mBinding.rvFields.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvFields.setAdapter(mAdapter);
        mBinding.rvFields.setNestedScrollingEnabled(false);
    }

    private void setOnClickListeners() {
        mBinding.btnCreate.setOnClickListener(v -> {
            mAdapter.retrieveValuesFromViews(mBinding.rvFields);
            viewModel.handleCreateWorkflowAction();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_upload:
                viewModel.showUploadFilePicker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.workflow_create_menu, menu);
        uploadMenu = menu.findItem(R.id.menu_upload);
        uploadMenu.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setUploadMenu(Boolean visible) {
        uploadMenu.setVisible(visible);
    }

    private void chooseFile() {
        new ChooserDialog().with(getContext())
                .withStartFile(FILE_CHOOSER_DIR)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        viewModel.selectUploadFile(path, pathFile);
                        Toast.makeText(getContext(), "FILE: " + path, Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }

    @UiThread
    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    private void showDialog(DialogMessage dialogMessage) {
        FragmentManager fm = getFragmentManager();

        String message = getString(dialogMessage.message);
        if (dialogMessage.messageAggregate != null) message += dialogMessage.messageAggregate;

        ValidateFormDialog dialog = ValidateFormDialog.newInstance(
                getString(dialogMessage.title),
                message,
                dialogMessage.list
        );

        dialog.show(fm, "validate_dialog");
    }

    private void goBack() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }

    /**
     * Inserts the WorkflowType form item to the RecyclerView.
     *
     * @param singleChoiceFormItem form item containing the workflow types.
     */
    @UiThread
    private void addWorkflowTypeItem(SingleChoiceFormItem singleChoiceFormItem) {
        mAdapter.addItem(singleChoiceFormItem);

        singleChoiceFormItem.setOnSelectedListener(item -> {
            if (item.getValue() == null) {
                viewModel.clearForm();
                mAdapter.setHasToEvaluateValid(false);
                return;
            }

            String selection = item.getValue().getName();
            viewModel.generateFieldsByType(selection);
        });

        if (mWorkflowListItem != null) {
            // triggers selection for the items to be created, since we already have a type.
            singleChoiceFormItem.getOnSelectedListener().onSelected(singleChoiceFormItem);
        }
    }

    /**
     * Inserts a single form item to the RecyclerView. This will refresh the UI with the added
     * item.
     *
     * @param item item to insert.
     */
    @UiThread
    private void addItemToForm(BaseFormItem item) {
        //check for any FileFormItem
        if (item instanceof FileFormItem) {
            createFileFormItemListener((FileFormItem) item);
        }

        mAdapter.addItem(item);
    }

    /**
     * Inserts multiple form items to the RecyclerView. This will refresh the UI with the added
     * items.
     *
     * @param list items to insert.
     */
    @UiThread
    private void setItemListToForm(List<BaseFormItem> list) {
        //check for any FileFormItem
        for (BaseFormItem item : list) {
            if (item instanceof FileFormItem) {
                createFileFormItemListener((FileFormItem) item);
            }
        }

        mAdapter.setData(list);
    }

    private void createFileFormItemListener(FileFormItem fileFormItem) {
        fileFormItem.setOnButtonClickedListener(
                () -> {
                    if (checkExternalStoragePermissions()) {
                        showFileChooser(fileFormItem);
                    }
                });
    }

    /**
     * Communicates with the RecyclerView adapter and invoke the validation UI changes.
     *
     * @param firstInvalidItem used to scroll to it.
     */
    @UiThread
    private void updateValidationUi(BaseFormItem firstInvalidItem) {
        mAdapter.setHasToEvaluateValid(true);

        if (firstInvalidItem == null) return;
        int firstInvalidPosition = mAdapter.getItemPosition(firstInvalidItem);
        mBinding.rvFields.scrollToPosition(firstInvalidPosition); //todo does not work
        //todo maybe show a Toast too
    }

    /**
     * Displays a native file chooser, the user must select which file they wish to upload. In case
     * that the file chooser cannot be opened, shows a Toast message.
     */
    @UiThread
    private void showFileChooser(FileFormItem fileFormItem) {
        //todo set fileFormItem to viewModel
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(
                    intent,
                    getString(R.string.workflow_detail_comments_fragment_select_file)),
                    CreateWorkflowViewModel.REQUEST_FILE_TO_ATTACH);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            showToastMessage(R.string.workflow_detail_comments_fragment_no_file_manager);
        }
    }

    /**
     * Verify whether the user has granted permissions to read/write the external storage.
     *
     * @return whether the permissions are granted.
     */
    private boolean checkExternalStoragePermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    CreateWorkflowViewModel.REQUEST_EXTERNAL_STORAGE_PERMISSIONS);

            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        viewModel.handleFileSelectedResult(getContext(), requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
