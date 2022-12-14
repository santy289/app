package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.ActivityWorkflowDetailBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.WorkflowDetailViewPagerAdapter;

import java.io.File;
import java.util.Locale;

import javax.inject.Inject;

import static com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailViewModel.REQUEST_EXTERNAL_STORAGE_PERMISSIONS;

public class WorkflowDetailActivity extends AppCompatActivity {

    public static final String EXTRA_WORKFLOW_LIST_ITEM = "Extra.WorkflowListItem";

    private static final String TAG = "WorkflowDetailActivity";

    public static final String INTENT_EXTRA_ID = "intranet_workflow_id";

    @Inject
    WorkflowDetailViewModelFactory workflowViewModelFactory;
    private WorkflowDetailViewModel workflowDetailViewModel;
    private ActivityWorkflowDetailBinding mBinding;
    private WorkflowDetailViewPagerAdapter mViewPagerAdapter;
    private Menu mMenu;
    private OnOpenStatusChangedListener mOnOpenStatusChangedListener;
    private boolean isSignatureEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_workflow_detail);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        workflowDetailViewModel = ViewModelProviders
                .of(this, workflowViewModelFactory)
                .get(WorkflowDetailViewModel.class);

        workflowDetailViewModel.onStart(getBaseContext().getContentResolver());

        SharedPreferences prefs = getSharedPreferences(PreferenceKeys.PREF_SESSION, Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");
        String loggedUserId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");
        isSignatureEnabled = prefs.getBoolean(PreferenceKeys.PREF_SIGNATURE, false);

        subscribe();

        showLoading(true);

        WorkflowListItem mWorkflowListItem = getIntent()
                .getParcelableExtra(EXTRA_WORKFLOW_LIST_ITEM);
        if (mWorkflowListItem == null) {
            String workflowId;
            workflowId = getIntent().getStringExtra(INTENT_EXTRA_ID);
            workflowDetailViewModel.initWithId(token, workflowId, loggedUserId, permissionsString);
            subscribeForIdInit();
        } else {
            workflowDetailViewModel
                    .initWithDetails(token, mWorkflowListItem, loggedUserId, permissionsString);
        }

        mBinding.fabSpeedDial.getMainFab().setSupportImageTintList(ColorStateList.valueOf(
                Color.WHITE)); //this is the only way to change the icon color
    }

    /**
     * Method will initialize some UI features using an WorkflowListItem object coming from the user selection
     * in workflow list.
     *
     * @param workflowListItem
     */
    private void initSoftUiWith(WorkflowListItem workflowListItem) {
        setActionBar(workflowListItem);
        workflowDetailViewModel.getObservableSoftWorkflowListItem().removeObservers(this);
    }

    /**
     * Method will fully initialize the UI with a fetched object from the server.
     *
     * @param workflowListItem
     */
    private void initUiWith(WorkflowListItem workflowListItem) {
        setupViewPager(workflowListItem);
        setupSpeedDialFab();
        workflowDetailViewModel.getObservableWorkflowListItem().removeObservers(this);
    }

    /**
     * Subscription used only if we are receiving an id as the data to initialize the Details
     * screen. For instance, we get an id when a user opens this activity through a notification tap
     * action.
     */
    private void subscribeForIdInit() {
        workflowDetailViewModel.getObservableHandleRepoWorkflowRequest().observe(
                this,
                workflowDb -> {
                    Log.d(TAG, "subscribeForIdInit: we got a workflow");
                    workflowDetailViewModel.getObservableHandleRepoWorkflowRequest()
                            .removeObservers(this);
                });

        workflowDetailViewModel.getObservableFetchFromServer().observe(
                this,
                needToFetch -> {
                    Log.d(TAG, "subscribeForIdInit: need to fetch from server");
                    workflowDetailViewModel.getObservableFetchFromServer()
                            .removeObservers(this);
                });
    }

    private void setActionBar(WorkflowListItem workflowListItem) {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = workflowListItem.getTitle();
        String subtitle = workflowListItem.getWorkflowTypeKey();
        if (title == null || title.isEmpty()) title = getTitle().toString();
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
    }

    /**
     * Initializes and set the {@link WorkflowDetailViewPagerAdapter} for the {@link ViewPager}.
     */
    private void setupViewPager(WorkflowListItem workflowListItem) {
        mViewPagerAdapter = new WorkflowDetailViewPagerAdapter(this,
                workflowListItem,
                getSupportFragmentManager(),
                isSignatureEnabled);
        mBinding.viewPager.setAdapter(mViewPagerAdapter);
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(this, getString(data), Toast.LENGTH_LONG).show();
            }
        });

        workflowDetailViewModel.getObservableError().observe(this, errorObserver);
        workflowDetailViewModel.getObservableShowToastMessage()
                .observe(this, this::showToastMessage);
        workflowDetailViewModel.getObservableCommentsTabCounter()
                .observe(this, this::updateCommentsTabCounter);
        workflowDetailViewModel.getObservableFilesTabCounter()
                .observe(this, this::updateFilesTabCounter);
        workflowDetailViewModel.retrieveWorkflowPdfFile
                .observe(this, this::openPdfFile);
        workflowDetailViewModel.handleShowLoadingByRepo.observe(this, this::showLoading);
        workflowDetailViewModel.getObservableWorkflowListItem().observe(this, this::initUiWith);
        workflowDetailViewModel.getObservableSoftWorkflowListItem().observe(this, this::initSoftUiWith);
        workflowDetailViewModel.getObservableWorkflowTypeVersion()
                .observe(this, this::updateToolbarSubtitleWithWorkflowVersion);
        workflowDetailViewModel.getObservableShowNotFoundView()
                .observe(this, this::showNotFoundView);
        workflowDetailViewModel.getObservableShowExportPdfButton()
                .observe(this, this::showExportPdfMenuItem);
        workflowDetailViewModel.getObservableShowDelete()
                .observe(this, this::showDeleteMenuItem);
        workflowDetailViewModel.getObservableShareWorkflow()
                .observe(this, this::showShareIntentChooser);

        workflowDetailViewModel.getObservableShowEnableDisable()
                .observe(this, this::showEnableDisableMenuItem);
        workflowDetailViewModel.updateEnabledDisabledStatusFromUserAction
                .observe(this, this::showEnableDisableMenuItem);

        workflowDetailViewModel.getObservableShowOpenClose()
                .observe(this, this::showOpenCloseMenuItem);
        workflowDetailViewModel.updateOpenClosedStatusFromUserAction
                .observe(this, this::showOpenCloseMenuItem);

        workflowDetailViewModel.deleteWorkflowRepsonseLiveData
                .observe(this, this::finishActivityWorkflowDeleted);

        workflowDetailViewModel.showLoading.observe(this, this::showLoading);
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(this);
        } else {
            Utils.hideLoading();
        }
    }

    /**
     * Creates an {@link Intent} chooser to open a PDF file. If the device is not suitable to read
     * the file, will display a {@link Toast} message. Uses a {@link FileProvider} to create the
     * file URI, instead of using the {@link Uri#fromFile(File)} method.
     *
     * @param fileUri the uri to be opened.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v4/content/FileProvider">FileProvider</a>
     */
    @UiThread
    private void openPdfFile(Uri fileUri) {
        if (fileUri == null) return;

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(fileUri, "application/pdf");
        target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent intent = Intent.createChooser(target,
                getString(R.string.workflow_detail_activity_open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here
            showToastMessage(R.string.workflow_detail_activity_no_pdf_reader);
        }
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                this,
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    @UiThread
    private void updateCommentsTabCounter(Integer count) {
        mViewPagerAdapter.setCommentsCounter(count);
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @UiThread
    private void updateFilesTabCounter(Integer count) {
        mViewPagerAdapter.setFilesCounter(count);
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @UiThread
    private void updateToolbarSubtitleWithWorkflowVersion(String versionString) {
        if (getSupportActionBar() == null) return;

        String currentSubtitle = (String) getSupportActionBar().getSubtitle();
        currentSubtitle = String.format(Locale.US, "%s (%s)", currentSubtitle, versionString);
        getSupportActionBar().setSubtitle(currentSubtitle);
    }

    @UiThread
    private void showNotFoundView(boolean showNotFound) {
        mBinding.lytNotFound.setVisibility(showNotFound ? View.VISIBLE : View.GONE);
        mBinding.lytDetails.setVisibility(showNotFound ? View.GONE : View.VISIBLE);

        //hide the menu items
        if (showNotFound) {
            mMenu.findItem(R.id.enable).setVisible(false);
            mMenu.findItem(R.id.disable).setVisible(false);
            mMenu.findItem(R.id.open).setVisible(false);
            mMenu.findItem(R.id.close).setVisible(false);
            mMenu.findItem(R.id.export_pdf).setVisible(false);
            mMenu.findItem(R.id.share).setVisible(false);
            mMenu.findItem(R.id.delete).setVisible(false);
        }
    }

    @UiThread
    private void showExportPdfMenuItem(boolean show) {
        if (mMenu == null) return;

        mMenu.findItem(R.id.export_pdf).setVisible(show);
    }

    @UiThread
    private void showEnableDisableMenuItem(boolean showEnable) {
        if (mMenu == null) return;

        if (workflowDetailViewModel.hasEnableDisablePermissions()) {
            mMenu.findItem(R.id.enable).setVisible(showEnable);
            mMenu.findItem(R.id.disable).setVisible(!showEnable);
        } else {
            mMenu.findItem(R.id.enable).setVisible(false);
            mMenu.findItem(R.id.disable).setVisible(false);
        }
    }

    @UiThread
    private void showOpenCloseMenuItem(boolean showOpen) {
        if (mOnOpenStatusChangedListener != null) {
            //pass the open status to the listener
            mOnOpenStatusChangedListener.onOpenStatusChanged(!showOpen);
        }

        if (mMenu == null) return;

        if (workflowDetailViewModel.hasOpenClosePermissions()) {
            mMenu.findItem(R.id.open).setVisible(showOpen);
            mMenu.findItem(R.id.close).setVisible(!showOpen);
        } else {
            mMenu.findItem(R.id.open).setVisible(false);
            mMenu.findItem(R.id.close).setVisible(false);
        }
    }

    @UiThread
    private void showDeleteMenuItem(boolean show) {
        if (mMenu == null) return;

        mMenu.findItem(R.id.delete).setVisible(show);
    }

    /**
     * This is called after the delete workflow service response.
     *
     * @param isDeleted if the workflow has been deleted, finish this activity.
     */
    @UiThread
    private void finishActivityWorkflowDeleted(boolean isDeleted) {
        if (!isDeleted) return;

        setResult(RESULT_OK);
        finish();
    }

    /**
     * Opens an IntentChooser to share plain text.
     *
     * @param textToShare the text to share.
     */
    @UiThread
    private void showShareIntentChooser(String textToShare) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, textToShare);
        Intent chooserIntent = Intent.createChooser(sharingIntent,
                getString(R.string.workflow_detail_activity_share_chooser));
        startActivity(chooserIntent);
    }

    /**
     * Verify whether the user has granted permissions to read/write the external storage.
     *
     * @return whether the permissions are granted.
     */
    private boolean checkExternalStoragePermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                 Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSIONS);

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        workflowDetailViewModel.handleRequestPermissionsResult(requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workflow_detail, menu);

        mMenu = menu;

        mMenu.findItem(R.id.export_pdf).setVisible(workflowDetailViewModel.hasExportPermissions());
        mMenu.findItem(R.id.delete).setVisible(workflowDetailViewModel.hasDeletePermissions());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

            return true;

        } else if (item.getItemId() == R.id.enable) {
            workflowDetailViewModel.setWorkflowEnabledStatus(true);

        } else if (item.getItemId() == R.id.disable) {
            workflowDetailViewModel.setWorkflowEnabledStatus(false);

        } else if (item.getItemId() == R.id.open) {
            workflowDetailViewModel.setWorkflowOpenStatus(true);

        } else if (item.getItemId() == R.id.close) {
            workflowDetailViewModel.setWorkflowOpenStatus(false);

        } else if (item.getItemId() == R.id.export_pdf) {
            if (checkExternalStoragePermissions()) {
                workflowDetailViewModel.handleExportPdf();
            }

        } else if (item.getItemId() == R.id.share) {
            SharedPreferences sharedPreferences = getSharedPreferences("Sessions",
                    Context.MODE_PRIVATE);
            String domainJson = sharedPreferences.getString(PreferenceKeys.PREF_DOMAIN, "");

            workflowDetailViewModel.shareWorkflow(domainJson);

        } else if (item.getItemId() == R.id.delete) {
            showDeleteConfirmationDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.workflow_detail_activity_delete_dialog_title);

        String workflowKey = workflowDetailViewModel.getWorkflowListItem().getWorkflowTypeKey();
        builder.setMessage(getString(
                R.string.workflow_detail_activity_delete_dialog_msg,
                workflowKey
        ));
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.accept,
                (dialog, which) -> workflowDetailViewModel.deleteWorkflow());
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    //region SpeedDial
    private void setupSpeedDialFab() {
        //reverse order
        addActionItem(R.id.fab_comment, R.string.quick_actions_comment,
                R.drawable.ic_message_black_24dp);
        if (workflowDetailViewModel.hasChangeStatusPermissions()) {
            addActionItem(R.id.fab_change_status, R.string.quick_actions_change_status,
                    R.drawable.ic_compare_arrows_black_24dp);
        }

        //addActionItem(R.id.fab_approve_workflow, R.string.quick_actions_approve_workflow, R.drawable.ic_like_black_24dp);

        if (workflowDetailViewModel.hasEditPermissions()) {
            addActionItem(R.id.fab_edit_workflow, R.string.quick_actions_edit_workflow,
                    R.drawable.ic_workflow_black_24dp);
        }

        addActionItem(R.id.fab_upload_file, R.string.quick_actions_upload_file,
                R.drawable.ic_file_upload_white_24dp);


        if (isSignatureEnabled) {
            addActionItem(R.id.fab_digital_signature, R.string.workflow_detail_signature_fragment_title,
                    R.drawable.ic_file_black);
        }

        mBinding.fabSpeedDial.setOnActionSelectedListener(this::handleSpeedDialClick);
        mBinding.fabSpeedDial.getMainFab().setSupportImageTintList(ColorStateList.valueOf(
                Color.WHITE)); //this is the only way to change the icon color
    }

    private void addActionItem(@IdRes int idRes, @StringRes int titleRes,
                               @DrawableRes int drawableRes) {
        mBinding.fabSpeedDial.addActionItem(
                new SpeedDialActionItem.Builder(idRes, drawableRes)
                        .setLabel(getString(titleRes))
                        .setFabBackgroundColor(ContextCompat.getColor(this, R.color.white))
                        .setFabImageTintColor(ContextCompat.getColor(this, R.color.black))
                        .setLabelBackgroundColor(ContextCompat.getColor(this, R.color.white))
                        .setLabelColor(ContextCompat.getColor(this, R.color.dark_gray))
                        .setLabelClickable(false)
                        .create()
        );
    }

    private boolean handleSpeedDialClick(SpeedDialActionItem speedDialActionItem) {
        int itemPosition;
        switch (speedDialActionItem.getId()) {

            case R.id.fab_upload_file:
                itemPosition = WorkflowDetailViewPagerAdapter.FILES;
                break;

            case R.id.fab_edit_workflow:
                itemPosition = WorkflowDetailViewPagerAdapter.INFORMATION;
                break;

            case R.id.fab_approve_workflow:
                itemPosition = WorkflowDetailViewPagerAdapter.STATUS;
                break;

            case R.id.fab_change_status:
                itemPosition = WorkflowDetailViewPagerAdapter.FLOWCHART;
                break;

            case R.id.fab_comment:
                itemPosition = WorkflowDetailViewPagerAdapter.COMMENTS;
                break;

            case R.id.fab_digital_signature:
                itemPosition = WorkflowDetailViewPagerAdapter.SIGNATURE;
                break;
            default:
                return false;
        }

        mBinding.viewPager.setCurrentItem(itemPosition);

        return false; // true to keep the Speed Dial open
    }
    //endregion

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);

        super.onBackPressed();
    }

    /**
     * Listener that will update the open status of the workflow.
     */
    public interface OnOpenStatusChangedListener {

        void onOpenStatusChanged(boolean isOpen);
    }

    public void setOnOpenStatusChangedListener(OnOpenStatusChangedListener listener) {
        mOnOpenStatusChangedListener = listener;
    }
}