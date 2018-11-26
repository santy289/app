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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.WorkflowDetailViewPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class WorkflowDetailFragment extends Fragment {

    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 700;

    @Inject
    WorkflowDetailViewModelFactory workflowViewModelFactory;
    private WorkflowDetailViewModel workflowDetailViewModel;
    private FragmentWorkflowDetailBinding mBinding;
    private MainActivityInterface mMainActivityInterface;
    private WorkflowListItem mWorkflowListItem;
    private WorkflowDetailViewPagerAdapter mViewPagerAdapter;

    public WorkflowDetailFragment() {
        // Required empty public constructor
    }

    public static WorkflowDetailFragment newInstance(WorkflowListItem item,
                                                     MainActivityInterface mainActivityInterface) {
        WorkflowDetailFragment fragment = new WorkflowDetailFragment();
        fragment.mWorkflowListItem = item;
        fragment.mMainActivityInterface = mainActivityInterface;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        workflowDetailViewModel = ViewModelProviders
                .of(this, workflowViewModelFactory)
                .get(WorkflowDetailViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");
        mBinding.tvWorkflowId.setText(mWorkflowListItem.getTitle());
        mBinding.tvWorkflowName.setText(mWorkflowListItem.getWorkflowTypeKey());

        setupViewPager();
        setOnClickListeners();
        subscribe();

        showLoading(true);
        workflowDetailViewModel.initDetails(token, mWorkflowListItem);

        return view;
    }

    /**
     * Initializes and set the {@link WorkflowDetailViewPagerAdapter} for the {@link ViewPager}.
     */
    private void setupViewPager() {
        mViewPagerAdapter = new WorkflowDetailViewPagerAdapter(getContext(), mWorkflowListItem,
                getChildFragmentManager());
        mBinding.viewPager.setAdapter(mViewPagerAdapter);
    }

    private void setOnClickListeners() {
        mBinding.btnOptions.setOnClickListener(this::showPopupMenu);
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        workflowDetailViewModel.getObservableError().observe(this, errorObserver);
        workflowDetailViewModel.getObservableShowToastMessage()
                .observe(this, this::showToastMessage);
        workflowDetailViewModel.getObservableCommentsTabCounter()
                .observe(this, this::updateCommentsTabCounter);
        workflowDetailViewModel.getObservableFilesTabCounter()
                .observe(this, this::updateFilesTabCounter);
        workflowDetailViewModel.getObservableStatusSpinner()
                .observe(this, this::updateStatusSpinner);
        workflowDetailViewModel.updateActiveStatusFromUserAction
                .observe(this, this::updateWorkflowStatus);
        workflowDetailViewModel.retrieveWorkflowPdfFile
                .observe(this, this::openPdfFile);
        workflowDetailViewModel.handleShowLoadingByRepo.observe(this, this::showLoading);

        workflowDetailViewModel.showLoading.observe(this, this::showLoading);
        workflowDetailViewModel.setWorkflowIsOpen.observe(this, this::updateWorkflowStatus);
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
    private void updateWorkflowStatus(StatusUiData statusUiData) {
        int selectedIndex = statusUiData.getSelectedIndex();

        mBinding.spStatus.setSelection(selectedIndex);
        mBinding.spStatus
                .setTag(selectedIndex); //workaround to prevent the listener from being called
        mBinding.viewSpinnerBackground.setBackgroundTintList(ColorStateList
                .valueOf(ContextCompat.getColor(getContext(),
                        statusUiData.getColorResList().get(selectedIndex))));
    }

    @UiThread
    private void openPdfFile(File pdfFile) {
        if (pdfFile == null) return;

        Intent target = new Intent(Intent.ACTION_VIEW);

        Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", pdfFile);

        target.setDataAndType(fileUri, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target,
                getString(R.string.workflow_detail_status_fragment_open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here
            showToastMessage(R.string.workflow_detail_status_fragment_no_pdf_reader);
        }
    }

    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Opens a {@link PopupMenu} with the defined options
     *
     * @param anchor the view which should act as the anchor for the menu. Normally it should be the
     *               one that the user tapped to invoke this menu.
     */
    private void showPopupMenu(View anchor) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.menu_workflow_detail, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (checkExternalStoragePermissions()) {
                workflowDetailViewModel.handleExportPdf();
            }

            return true;
        });

        popup.show();
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
    private void updateStatusSpinner(StatusUiData statusUiData) {
        List<String> statusList = new ArrayList<>();
        for (int stringRes : statusUiData.getStringResList()) {
            statusList.add(getString(stringRes));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.status_spinner_item, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spStatus.setAdapter(adapter);
        mBinding.spStatus
                .setSelection(0, false); //prevent the listener from being called on initialization
        mBinding.spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //workaround to prevent the listener from being called
                //code is executed only when the user is making a selection
                Object tag = mBinding.spStatus.getTag();
                if (tag == null || (int) tag != position) {
                    workflowDetailViewModel.setStatusSelection(position);
                    workflowDetailViewModel.handleWorkflowActivation(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                    REQUEST_EXTERNAL_STORAGE_PERMISSIONS);

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSIONS: {
                // check for both permissions
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permissions granted
                    workflowDetailViewModel.handleExportPdf();
                }
            }
        }
    }
}