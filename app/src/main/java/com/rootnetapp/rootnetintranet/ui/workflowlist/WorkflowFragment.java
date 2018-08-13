package com.rootnetapp.rootnetintranet.ui.workflowlist;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowBinding;
import com.rootnetapp.rootnetintranet.databinding.WorkflowFiltersMenuBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.domain.Sort;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailFragment;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.WorkflowExpandableAdapter;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowDialog;

import java.util.List;

import javax.inject.Inject;

public class WorkflowFragment extends Fragment implements WorkflowFragmentInterface {

    @Inject
    WorkflowViewModelFactory workflowViewModelFactory;
    WorkflowViewModel workflowViewModel;
    private FragmentWorkflowBinding fragmentWorkflowBinding;
    private WorkflowFiltersMenuBinding workflowFiltersMenuBinding;
    private MainActivityInterface mainActivityInterface;
    private WorkflowExpandableAdapter adapter;
    private Sort sorting;
    private String token;

    private static final String TAG = "WorkflowFragment";

    public WorkflowFragment() {
        // Required empty public constructor
    }

    public static WorkflowFragment newInstance(MainActivityInterface mainActivityInterface) {
        WorkflowFragment fragment = new WorkflowFragment();
        fragment.mainActivityInterface = mainActivityInterface;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentWorkflowBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow, container, false);
        View view = fragmentWorkflowBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        workflowViewModel = ViewModelProviders
                .of(this, workflowViewModelFactory)
                .get(WorkflowViewModel.class);
        sorting = new Sort();
        subscribe();
        setupWorkflowRecyclerView();
        setupClickListeners();
        //Utils.showLoading(getContext());
        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        workflowViewModel.initWorkflowList(prefs);
        return view;
    }

    private void setupWorkflowRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fragmentWorkflowBinding.recWorkflows.setLayoutManager(mLayoutManager);
        adapter = new WorkflowExpandableAdapter(this);
        fragmentWorkflowBinding.recWorkflows.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fragmentWorkflowBinding.btnFilters.setOnClickListener(view1 -> {
            PopupWindow popupwindow_obj = initPopMenu();
            popupwindow_obj.showAsDropDown(fragmentWorkflowBinding.btnFilters, -40, 18);
        });
        fragmentWorkflowBinding.btnAdd.setOnClickListener(view12 ->
                mainActivityInterface.showDialog(CreateWorkflowDialog.newInstance(this)));
    }

    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    private void subscribe() {
        final Observer<List<Workflow>> workflowsObserver = ((List<Workflow> data) -> {
            Log.d(TAG, "subscribe: HEREHERHE");
            //Utils.hideLoading();
            if (null != data) {
                if(data.size()!=0){
                    fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.GONE);
//                    adapter = new WorkflowExpandableAdapter(this);
//                    fragmentWorkflowBinding.recWorkflows.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                }else{
                    fragmentWorkflowBinding.recWorkflows.setVisibility(View.GONE);
                    fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.VISIBLE);
                }
            }else{
                fragmentWorkflowBinding.recWorkflows.setVisibility(View.GONE);
                fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.VISIBLE);
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            //Utils.hideLoading();
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });
        final Observer<List<Workflow>> getAllWorkflowsObserver = (listWorkflows -> {
            if (adapter == null || listWorkflows == null) {
                fragmentWorkflowBinding.recWorkflows.setVisibility(View.GONE);
                fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.VISIBLE);
                return;
            }
            if(listWorkflows.size() < 1){
                fragmentWorkflowBinding.recWorkflows.setVisibility(View.GONE);
                fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.VISIBLE);
                return;
            }
            // before updating check if we need to apply filters.
            if (sorting.getSortingType() == Sort.sortType.NONE) {
                adapter.setWorkflows(listWorkflows);
            } else {
                workflowViewModel.applyFilters(sorting);
            }
            fragmentWorkflowBinding.recWorkflows.setVisibility(View.VISIBLE);
            fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.GONE);
        });
        final Observer<List<Workflow>> updateWithSortedListObserver = (
                listWorklows -> adapter.setWorkflows(listWorklows)
        );
        final Observer<Boolean> showLoadingObserver = (this::showLoading);
        workflowViewModel.getObservableWorkflows().observe(this, workflowsObserver);
        workflowViewModel.getObservableError().observe(this, errorObserver);
        workflowViewModel.getObservableShowLoading().observe(this, showLoadingObserver);
        workflowViewModel.getAllWorkflows().observe(this, getAllWorkflowsObserver);
        workflowViewModel.getObservableUpdateWithSortedList().observe(this, updateWithSortedListObserver);
    }

    private PopupWindow initPopMenu() {
        final PopupWindow popupWindow = new PopupWindow(getContext());

        // inflate your layout or dynamically add view
        workflowFiltersMenuBinding =
                DataBindingUtil.inflate(getLayoutInflater(), R.layout.workflow_filters_menu, null, false);
        popupWindow.setFocusable(true);
        popupWindow.setWidth((int) getResources().getDimension(R.dimen.filters_width));
        popupWindow.setHeight((int) getResources().getDimension(R.dimen.filters_height));
        popupWindow.setContentView(workflowFiltersMenuBinding.getRoot());
        switch (sorting.getSortingType()) {
            case BYNUMBER: {
                workflowFiltersMenuBinding.chbxWorkflownumber.setChecked(true);
                break;
            }
            case BYCREATE: {
                workflowFiltersMenuBinding.chbxCreatedate.setChecked(true);
                break;
            }
            case BYUPDATE: {
                workflowFiltersMenuBinding.chbxUpdatedate.setChecked(true);
                break;
            }
        }

        if (sorting.getNumberSortOrder().equals(Sort.sortOrder.ASC)) {
            workflowFiltersMenuBinding.swchWorkflownumber.setChecked(true);
            workflowFiltersMenuBinding.swchWorkflownumber.setText(getString(R.string.ascending));
        } else {
            workflowFiltersMenuBinding.swchWorkflownumber.setChecked(false);
        }
        if (sorting.getCreatedSortOrder().equals(Sort.sortOrder.ASC)) {
            workflowFiltersMenuBinding.swchCreatedate.setChecked(true);
            workflowFiltersMenuBinding.swchCreatedate.setText(getString(R.string.ascending));

        } else {
            workflowFiltersMenuBinding.swchCreatedate.setChecked(false);
        }
        if (sorting.getUpdatedSortOrder().equals(Sort.sortOrder.ASC)) {
            workflowFiltersMenuBinding.swchUpdatedate.setChecked(true);
            workflowFiltersMenuBinding.swchUpdatedate.setText(getString(R.string.ascending));
        } else {
            workflowFiltersMenuBinding.swchUpdatedate.setChecked(false);
        }

        setFilterBoxListeners();

        return popupWindow;
    }

    private void setFilterBoxListeners() {
        workflowFiltersMenuBinding.chbxWorkflownumber.setOnClickListener(this::onRadioButtonClicked);
        workflowFiltersMenuBinding.chbxCreatedate.setOnClickListener(this::onRadioButtonClicked);
        workflowFiltersMenuBinding.chbxUpdatedate.setOnClickListener(this::onRadioButtonClicked);

        workflowFiltersMenuBinding.swchWorkflownumber.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sorting.setNumberSortOrder(Sort.sortOrder.ASC);
                workflowFiltersMenuBinding.swchWorkflownumber.setText(getString(R.string.ascending));
            } else {
                sorting.setNumberSortOrder(Sort.sortOrder.DESC);
                workflowFiltersMenuBinding.swchWorkflownumber.setText(getString(R.string.descending));
            }
            workflowViewModel.applyFilters(sorting);
        });
        workflowFiltersMenuBinding.swchCreatedate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sorting.setCreatedSortOrder(Sort.sortOrder.ASC);
                workflowFiltersMenuBinding.swchCreatedate.setText(getString(R.string.ascending));
            } else {
                sorting.setCreatedSortOrder(Sort.sortOrder.DESC);
                workflowFiltersMenuBinding.swchCreatedate.setText(getString(R.string.descending));
            }
            workflowViewModel.applyFilters(sorting);
        });
        workflowFiltersMenuBinding.swchUpdatedate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sorting.setUpdatedSortOrder(Sort.sortOrder.ASC);
                workflowFiltersMenuBinding.swchUpdatedate.setText(getString(R.string.ascending));
            } else {
                sorting.setUpdatedSortOrder(Sort.sortOrder.DESC);
                workflowFiltersMenuBinding.swchUpdatedate.setText(getString(R.string.descending));
            }
            workflowViewModel.applyFilters(sorting);
        });
    }

    private void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.chbx_workflownumber: {
                if (checked) {
                    if (sorting.getSortingType().equals(Sort.sortType.BYNUMBER)) {
                        sorting.setSortingType(Sort.sortType.NONE);
                        if (view.getParent() instanceof RadioGroup) {
                            ((RadioGroup) view.getParent()).clearCheck();
                        }
                    } else {
                        sorting.setSortingType(Sort.sortType.BYNUMBER);
                    }
                }
                break;
            }
            case R.id.chbx_createdate: {
                if (checked) {
                    if (sorting.getSortingType().equals(Sort.sortType.BYCREATE)) {
                        sorting.setSortingType(Sort.sortType.NONE);
                        if (view.getParent() instanceof RadioGroup) {
                            ((RadioGroup) view.getParent()).clearCheck();
                        }
                    } else {
                        sorting.setSortingType(Sort.sortType.BYCREATE);
                    }
                }
                break;
            }
            case R.id.chbx_updatedate: {
                if (checked) {
                    if (sorting.getSortingType().equals(Sort.sortType.BYUPDATE)) {
                        sorting.setSortingType(Sort.sortType.NONE);
                        if (view.getParent() instanceof RadioGroup) {
                            ((RadioGroup) view.getParent()).clearCheck();
                        }
                    } else {
                        sorting.setSortingType(Sort.sortType.BYUPDATE);
                    }
                }
                break;
            }
        }
        workflowViewModel.applyFilters(sorting);
    }

    @Override
    public void dataAdded() {
        workflowViewModel.getWorkflows(token);
    }

    @Override
    public void showDetail(Workflow item) {
        mainActivityInterface.showFragment(WorkflowDetailFragment.newInstance(item,
                mainActivityInterface),true);
    }


}
