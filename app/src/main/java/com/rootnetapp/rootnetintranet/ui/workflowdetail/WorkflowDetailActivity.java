package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.ActivityWorkflowDetailBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.WorkflowDetailViewPagerAdapter;

import java.io.File;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_workflow_detail);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        workflowDetailViewModel = ViewModelProviders
                .of(this, workflowViewModelFactory)
                .get(WorkflowDetailViewModel.class);

        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        setOnClickListeners();
        subscribe();

        showLoading(true);

        WorkflowListItem mWorkflowListItem = getIntent().getParcelableExtra(EXTRA_WORKFLOW_LIST_ITEM);
        if (mWorkflowListItem == null) {
            String workflowId;
            workflowId = getIntent().getStringExtra(INTENT_EXTRA_ID);
            workflowDetailViewModel.initWithId(token, workflowId);
            subscribeForIdInit();
        } else {
            workflowDetailViewModel.initWithDetails(token, mWorkflowListItem);
        }
    }

    /**
     * Method will initialize the UI using an WorkflowListItem object coming from the user selection
     * in workflow list.
     *
     * @param workflowListItem
     */
    private void initUiWith(WorkflowListItem workflowListItem) {
        setActionBar(workflowListItem);
        setupViewPager(workflowListItem);
        workflowDetailViewModel.getObservableWorflowListItem().removeObservers(this);
    }

    /**
     * Subscription used only if we are receiving an id as the data to initialize the Details screen.
     * For instance, we get an id when a user opens this activity through a notification tap
     * action.
     */
    private void subscribeForIdInit() {
        workflowDetailViewModel.getObservableHandleRepoWorkflowRequest().observe(
                this,
                workflowDb -> {
                    Log.d(TAG, "subscribeForIdInit: we got a workflow");
                    workflowDetailViewModel.getObservableHandleRepoWorkflowRequest().removeObservers(this);
                });
    }

    private void setActionBar(WorkflowListItem workflowListItem) {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = workflowListItem.title;
        String subtitle = workflowListItem.getWorkflowTypeKey();
        if (title == null || title.isEmpty()) title = getTitle().toString();
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
    }

    /**
     * Initializes and set the {@link WorkflowDetailViewPagerAdapter} for the {@link ViewPager}.
     */
    private void setupViewPager(WorkflowListItem workflowListItem) {
        mViewPagerAdapter = new WorkflowDetailViewPagerAdapter(this, workflowListItem,
                getSupportFragmentManager());
        mBinding.viewPager.setAdapter(mViewPagerAdapter);
    }

    private void setOnClickListeners() {
        mBinding.fab.setOnClickListener(v -> workflowDetailViewModel.toggleWorkflowActivation());
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
        workflowDetailViewModel.updateActiveStatusFromUserAction
                .observe(this, this::updateWorkflowStatus);
        workflowDetailViewModel.retrieveWorkflowPdfFile
                .observe(this, this::openPdfFile);
        workflowDetailViewModel.handleShowLoadingByRepo.observe(this, this::showLoading);
        workflowDetailViewModel.handleSetWorkflowIsOpenByRepo
                .observe(this, this::updateWorkflowStatus);
        workflowDetailViewModel.getObservableWorflowListItem().observe(this, this::initUiWith);

        workflowDetailViewModel.showLoading.observe(this, this::showLoading);
        workflowDetailViewModel.setWorkflowIsOpen.observe(this, this::updateWorkflowStatus);
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
     * Changes the UI state (text and color) of the selected status. This is called after user
     * interaction with the {@link Spinner} and after the API request is completed.
     *
     * @param statusUiData object that contains the current state of the status UI.
     */
    @UiThread
    private void updateWorkflowStatus(StatusUiData statusUiData) {
        mBinding.fab.setSupportBackgroundTintList(ColorStateList
                .valueOf(ContextCompat.getColor(this, statusUiData.getSelectedColor())));
        mBinding.fab
                .setImageDrawable(ContextCompat.getDrawable(this, statusUiData.getSelectedIcon()));
    }

    /**
     * Creates an {@link Intent} chooser to open a PDF file. If the device is not suitable to read
     * the file, will display a {@link Toast} message. Uses a {@link FileProvider} to create the
     * file URI, instead of using the {@link Uri#fromFile(File)} method.
     *
     * @param pdfFile the file to be opened.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v4/content/FileProvider">FileProvider</a>
     */
    @UiThread
    private void openPdfFile(File pdfFile) {
        if (pdfFile == null) return;

        Intent target = new Intent(Intent.ACTION_VIEW);

        Uri fileUri = FileProvider.getUriForFile(this,
                this.getApplicationContext().getPackageName() + ".fileprovider", pdfFile);

        target.setDataAndType(fileUri, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

            return true;

        } else if (item.getItemId() == R.id.export_pdf) {
            if (checkExternalStoragePermissions()) {
                workflowDetailViewModel.handleExportPdf();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}