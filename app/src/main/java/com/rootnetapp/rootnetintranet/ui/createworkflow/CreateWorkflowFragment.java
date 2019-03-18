package com.rootnetapp.rootnetintranet.ui.createworkflow;

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
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentCreateWorkflowBinding;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.FileFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.GeolocationFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.IntentFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.adapters.FormItemsAdapter;
import com.rootnetapp.rootnetintranet.ui.createworkflow.dialog.DialogMessage;
import com.rootnetapp.rootnetintranet.ui.createworkflow.dialog.ValidateFormDialog;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.GeolocationActivity;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.GeolocationViewModel;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.SelectedLocation;
import com.rootnetapp.rootnetintranet.ui.main.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class CreateWorkflowFragment extends Fragment implements CreateWorkflowFragmentInterface {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    private CreateWorkflowViewModel viewModel;
    private FragmentCreateWorkflowBinding mBinding;

    private static final String TAG = "CreateWorkflowFragment";

    private FormItemsAdapter mAdapter;
    private FormItemsAdapter mPeopleInvolvedAdapter;
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
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
        String loggedUserId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");

        setupSubmitButton();
        setOnClickListeners();
        setupFormRecycler();
        setupPeopleInvolvedRecycler();
        subscribe();

        viewModel.initForm(token, mWorkflowListItem, loggedUserId, permissionsString);

        return view;
    }

    private void subscribe() {
        viewModel.getObservableToastMessage().observe(getViewLifecycleOwner(), this::showToastMessage);
        viewModel.getObservableAddWorkflowTypeItem().observe(getViewLifecycleOwner(), this::addWorkflowTypeItem);
        viewModel.getObservableAddPeopleInvolvedItem().observe(getViewLifecycleOwner(), this::addPeopleInvolvedItem);
        viewModel.getObservableAddFormItem().observe(getViewLifecycleOwner(), this::addItemToForm);
        viewModel.getObservableSetFormItemList().observe(getViewLifecycleOwner(), this::setItemListToForm);
        viewModel.getObservableAddPeopleInvolvedFormItem()
                .observe(getViewLifecycleOwner(), this::addItemToPeopleInvolvedForm);
        viewModel.getObservableSetPeopleInvolvedFormItemList()
                .observe(getViewLifecycleOwner(), this::setItemListToPeopleInvolvedForm);
        viewModel.getObservableValidationUi().observe(getViewLifecycleOwner(), this::updateValidationUi);
        viewModel.getObservableShowLoading().observe(getViewLifecycleOwner(), this::showLoading);
        viewModel.getObservableShowDialogMessage().observe(getViewLifecycleOwner(), this::showDialog);
        viewModel.getObservableGoBack().observe(getViewLifecycleOwner(), back -> goBack());
        viewModel.getObservableUpdateFormItem().observe(getViewLifecycleOwner(), this::updateFormItemUi);
        viewModel.getObservableDownloadedFileUiData().observe(getViewLifecycleOwner(), this::openDownloadedFile);
        viewModel.getObservableEnableSubmitButton().observe(getViewLifecycleOwner(), this::enableSubmitButton);
    }

    private void setupSubmitButton() {
        mBinding.btnCreate.setText(
                mWorkflowListItem == null ? R.string.create_workflow : R.string.edit_workflow);
    }

    /**
     * Set the proper animations to the ViewFlipper depending on which direction the movement is.
     *
     * @param isNext true - view is on the right of the current view; false - view is on the left of
     *               the current view.
     */
    private void setupViewFlipperAnimations(boolean isNext) {
        if (isNext) {
            //next view
            mBinding.viewFlipper.setInAnimation(
                    AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
            mBinding.viewFlipper.setOutAnimation(
                    AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left));
        } else {
            //previous view
            mBinding.viewFlipper.setInAnimation(
                    AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left));
            mBinding.viewFlipper.setOutAnimation(
                    AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right));
        }
    }

    private void setupFormRecycler() {
        mAdapter = new FormItemsAdapter(getContext(), getChildFragmentManager(), new ArrayList<>(),
                this);
        mBinding.rvFields.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvFields.setAdapter(mAdapter);
        mBinding.rvFields.setNestedScrollingEnabled(false);
    }

    private void setOnClickListeners() {
        mBinding.btnCreate.setOnClickListener(v -> {
            mAdapter.retrieveValuesFromViews(mBinding.rvFields);
            viewModel.handleCreateWorkflowAction();
        });

        mBinding.btnBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
        hideSoftInputKeyboard();
    }

    @UiThread
    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    @UiThread
    private void showDialog(DialogMessage dialogMessage) {
        FragmentManager fm = getFragmentManager();

        String message = getString(dialogMessage.message);
        if (dialogMessage.messageAggregate != null) {
            message = getString(dialogMessage.message, dialogMessage.messageAggregate);
        }

        ValidateFormDialog dialog = ValidateFormDialog.newInstance(
                getString(dialogMessage.title),
                message,
                dialogMessage.list
        );

        dialog.show(fm, "validate_dialog");
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    private void goBack() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
            hideSoftInputKeyboard();
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
                return;
            }

            String selection = item.getValue().getName();
            viewModel.generateFieldsByType(selection);

            mAdapter.setHasToEvaluateValid(false); //clear validation marks
        });

        if (mWorkflowListItem != null) {
            // triggers selection for the items to be created, since we already have a type.
            singleChoiceFormItem.getOnSelectedListener().onSelected(singleChoiceFormItem);
        }
    }

    /**
     * Inserts the People Involved form item to the RecyclerView.
     *
     * @param intentFormItem form item containing the people involved action
     */
    @UiThread
    private void addPeopleInvolvedItem(IntentFormItem intentFormItem) {
        mAdapter.addItem(intentFormItem);

        intentFormItem.setOnButtonClickedListener(() -> {
            setupViewFlipperAnimations(true);
            mBinding.viewFlipper.showNext();

            viewModel.getWorkflowTypeInfo();
        });
    }

    /**
     * Inserts a single form item to the RecyclerView. This will refresh the UI with the added
     * item.
     *
     * @param item item to insert.
     */
    @UiThread
    private void addItemToForm(BaseFormItem item) {
        //for FileFormItem and GeolocationFormItem
        createFormItemListenerIfNeeded(item);

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
        //for FileFormItem and GeolocationFormItem
        for (BaseFormItem item : list) {
            createFormItemListenerIfNeeded(item);
        }

        mAdapter.setData(list);
    }

    private void createFormItemListenerIfNeeded(BaseFormItem item) {
        //check for any FileFormItem
        if (item instanceof FileFormItem) {
            createFileFormItemListener((FileFormItem) item);
        }
        //check for any GeolocationFormItem
        else if (item instanceof GeolocationFormItem) {
            createGeolocationFormItemListener((GeolocationFormItem) item);
        }
    }

    private void createFileFormItemListener(FileFormItem fileFormItem) {
        fileFormItem.setOnButtonClickedListener(() -> showFileChooser(fileFormItem));
    }

    private void createGeolocationFormItemListener(GeolocationFormItem geolocationFormItem) {
        geolocationFormItem.setOnButtonClickedListener(() -> {
            startActivityForResult(new Intent(getActivity(),
                    GeolocationActivity.class), CreateWorkflowViewModel.REQUEST_GEOLOCATION);
            viewModel.setCurrentRequestingGeolocationFormItem(geolocationFormItem);
        });
    }

    /**
     * Communicates with the RecyclerView adapter and invoke the validation UI changes.
     *
     * @param firstInvalidItem used to scroll to it.
     */
    @UiThread
    private void updateValidationUi(BaseFormItem firstInvalidItem) {
        //update item views
        mAdapter.setHasToEvaluateValid(true);

        //show toast error message
        showToastMessage(R.string.fill_all_form_items);

        //scroll to the first item that is not valid
        if (firstInvalidItem == null) return;
        int firstInvalidPosition = mAdapter.getItemPosition(firstInvalidItem);
        mBinding.rvFields.scrollToPosition(firstInvalidPosition); //todo does not work
    }

    /**
     * Displays a native file chooser, the user must select which file they wish to upload. In case
     * that the file chooser cannot be opened, shows a Toast message.
     */
    @UiThread
    private void showFileChooser(FileFormItem fileFormItem) {
        hideSoftInputKeyboard();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");

        //specify multiple MIME types
        intent.putExtra(Intent.EXTRA_MIME_TYPES, Utils.ALLOWED_MIME_TYPES);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(
                    intent,
                    getString(R.string.workflow_detail_comments_fragment_select_file)),
                    CreateWorkflowViewModel.REQUEST_FILE_TO_ATTACH);

            //hold the reference while the user is choosing a file
            viewModel.setCurrentRequestingFileFormItem(fileFormItem);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            showToastMessage(R.string.workflow_detail_comments_fragment_no_file_manager);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        viewModel.handleActivityResult(getContext(), requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @UiThread
    private void updateFormItemUi(BaseFormItem formItem) {
        mAdapter.notifyItemChanged(mAdapter.getItemPosition(formItem));
    }

    /**
     * Sends a request to the ViewModel to retrieve the specified file in order to be opened by the
     * device. Should check WRITE/READ external storage permissions before requesting.
     *
     * @param fileId file ID to download.
     */
    @Override
    public void downloadFile(int fileId) {
        if (checkExternalStoragePermissions()) {
            viewModel.downloadFile(fileId);
        } else {
            viewModel.setQueuedFile(fileId);
        }
    }

    /**
     * Opens the MapsActivity to display the selected location.
     *
     * @param geolocationFormItem form item containing the location to pinpoint.
     */
    @Override
    public void showLocation(GeolocationFormItem geolocationFormItem) {
        Intent intent = new Intent(getActivity(), GeolocationActivity.class);

        SelectedLocation selectedLocation = new SelectedLocation(geolocationFormItem.getValue(),
                geolocationFormItem.getName());
        intent.putExtra(GeolocationViewModel.EXTRA_SHOW_LOCATION, selectedLocation);

        startActivity(intent);
    }

    /**
     * This intercepts the onBackPressed from the Activity. The Activity must add a condition in
     * order to allow the Fragment to intercept the callback. See {@link
     * MainActivity#onBackPressed()} for an example.
     *
     * @return true - this Fragment handled the callback; false - the Activity must handle the
     * callback
     */
    @Override
    public boolean onBackPressed() {
        if (mBinding.viewFlipper.getDisplayedChild() == 0) {
            return false; //normal onBackPressed by Activity
        }

        //validate People Involved item (check if completed)
        viewModel.validatePeopleInvolvedFormItems();

        //this fragment will handle the onBackPressed instead of the activity
        setupViewFlipperAnimations(false);
        mBinding.viewFlipper.showPrevious();
        return true;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        viewModel.handleRequestPermissionsResult(requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Creates an {@link Intent} chooser the downloaded file. If the device is not suitable to read
     * the file, will display a {@link Toast} message. Uses a {@link FileProvider} to create the
     * file URI, instead of using the {@link Uri#fromFile(File)} method.
     *
     * @param downloadedFileUiData the file data containing the file to be opened.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v4/content/FileProvider">FileProvider</a>
     */
    @UiThread
    private void openDownloadedFile(DownloadedFileUiData downloadedFileUiData) {
        if (downloadedFileUiData.getFile() == null) return;

        Intent target = new Intent(Intent.ACTION_VIEW);

        Uri fileUri = FileProvider.getUriForFile(getContext(),
                getContext().getApplicationContext().getPackageName() + ".fileprovider",
                downloadedFileUiData.getFile());

        target.setDataAndType(fileUri, downloadedFileUiData.getMimeType());
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target,
                getString(R.string.workflow_detail_comments_fragment_open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here
            showToastMessage(R.string.workflow_detail_comments_fragment_cannot_open_file);
        }
    }

    //region People Involved Form
    private void setupPeopleInvolvedRecycler() {
        mPeopleInvolvedAdapter = new FormItemsAdapter(getContext(), getChildFragmentManager(),
                new ArrayList<>(),
                this);
        mBinding.rvPeopleInvolvedFields.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvPeopleInvolvedFields.setAdapter(mPeopleInvolvedAdapter);
        mBinding.rvPeopleInvolvedFields.setNestedScrollingEnabled(false);
    }

    /**
     * Inserts a single form item to the RecyclerView. This will refresh the UI with the added
     * item.
     *
     * @param item item to insert.
     */
    @UiThread
    private void addItemToPeopleInvolvedForm(BaseFormItem item) {
        mPeopleInvolvedAdapter.addItem(item);
    }

    /**
     * Inserts multiple form items to the RecyclerView. This will refresh the UI with the added
     * items.
     *
     * @param list items to insert.
     */
    @UiThread
    private void setItemListToPeopleInvolvedForm(List<BaseFormItem> list) {
        mPeopleInvolvedAdapter.setData(list);
    }
    //endregion

    @UiThread
    private void enableSubmitButton(boolean enabled) {
        mBinding.btnCreate.setEnabled(enabled);
    }

    private void hideSoftInputKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
