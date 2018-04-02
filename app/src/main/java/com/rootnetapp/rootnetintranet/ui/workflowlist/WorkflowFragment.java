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
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
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
        // Inflate the layout for this fragment
        fragmentWorkflowBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow, container, false);
        View view = fragmentWorkflowBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        mainActivityInterface.changeTitle("");
        workflowViewModel = ViewModelProviders
                .of(this, workflowViewModelFactory)
                .get(WorkflowViewModel.class);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fragmentWorkflowBinding.recWorkflows.setLayoutManager(mLayoutManager);
        Utils.showLoading(getContext());
        sorting = new Sort();
        subscribe();
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        token = prefs.getString("token", "");
        workflowViewModel.getWorkflows(token);
        fragmentWorkflowBinding.btnFilters.setOnClickListener(view1 -> {
            PopupWindow popupwindow_obj = popupMenu();
            popupwindow_obj.showAsDropDown(fragmentWorkflowBinding.btnFilters, -40, 18);
        });
        fragmentWorkflowBinding.btnAdd.setOnClickListener(view12 ->
                mainActivityInterface.showDialog(CreateWorkflowDialog.newInstance(this)));
        return view;
    }

    private void subscribe() {
        final Observer<List<Workflow>> workflowsObserver = ((List<Workflow> data) -> {
            Utils.hideLoading();
            if (null != data) {
                adapter = new WorkflowExpandableAdapter(data);
                fragmentWorkflowBinding.recWorkflows.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            Utils.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });
        workflowViewModel.getObservableWorkflows().observe(this, workflowsObserver);
        workflowViewModel.getObservableError().observe(this, errorObserver);
    }

    public PopupWindow popupMenu() {
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
        workflowFiltersMenuBinding.chbxWorkflownumber.setOnClickListener(this::onRadioButtonClicked);
        workflowFiltersMenuBinding.chbxCreatedate.setOnClickListener(this::onRadioButtonClicked);
        workflowFiltersMenuBinding.chbxUpdatedate.setOnClickListener(this::onRadioButtonClicked);
        if (sorting.getNumberSortOrder().equals(sortOrder.ASC)) {
            workflowFiltersMenuBinding.swchWorkflownumber.setChecked(true);
            workflowFiltersMenuBinding.swchWorkflownumber.setText(getString(R.string.ascending));
        } else {
            workflowFiltersMenuBinding.swchWorkflownumber.setChecked(false);
        }
        if (sorting.getCreatedSortOrder().equals(sortOrder.ASC)) {
            workflowFiltersMenuBinding.swchCreatedate.setChecked(true);
            workflowFiltersMenuBinding.swchCreatedate.setText(getString(R.string.ascending));

        } else {
            workflowFiltersMenuBinding.swchCreatedate.setChecked(false);
        }
        if (sorting.getUpdatedSortOrder().equals(sortOrder.ASC)) {
            workflowFiltersMenuBinding.swchUpdatedate.setChecked(true);
            workflowFiltersMenuBinding.swchUpdatedate.setText(getString(R.string.ascending));
        } else {
            workflowFiltersMenuBinding.swchUpdatedate.setChecked(false);
        }
        workflowFiltersMenuBinding.swchWorkflownumber.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sorting.setNumberSortOrder(sortOrder.ASC);
                workflowFiltersMenuBinding.swchWorkflownumber.setText(getString(R.string.ascending));
            } else {
                sorting.setNumberSortOrder(sortOrder.DESC);
                workflowFiltersMenuBinding.swchWorkflownumber.setText(getString(R.string.descending));
            }
            workflowViewModel.applyFilters(sorting);
        });
        workflowFiltersMenuBinding.swchCreatedate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sorting.setCreatedSortOrder(sortOrder.ASC);
                workflowFiltersMenuBinding.swchCreatedate.setText(getString(R.string.ascending));
            } else {
                sorting.setCreatedSortOrder(sortOrder.DESC);
                workflowFiltersMenuBinding.swchCreatedate.setText(getString(R.string.descending));
            }
            workflowViewModel.applyFilters(sorting);
        });
        workflowFiltersMenuBinding.swchUpdatedate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sorting.setUpdatedSortOrder(sortOrder.ASC);
                workflowFiltersMenuBinding.swchUpdatedate.setText(getString(R.string.ascending));
            } else {
                sorting.setUpdatedSortOrder(sortOrder.DESC);
                workflowFiltersMenuBinding.swchUpdatedate.setText(getString(R.string.descending));
            }
            workflowViewModel.applyFilters(sorting);
        });

        return popupWindow;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.chbx_workflownumber: {
                if (checked) {
                    if (sorting.getSortingType().equals(sortType.BYNUMBER)) {
                        sorting.setSortingType(sortType.NONE);
                        if (view.getParent() instanceof RadioGroup) {
                            ((RadioGroup) view.getParent()).clearCheck();
                        }
                    } else {
                        sorting.setSortingType(sortType.BYNUMBER);
                    }
                }
                break;
            }
            case R.id.chbx_createdate: {
                if (checked) {
                    if (sorting.getSortingType().equals(sortType.BYCREATE)) {
                        sorting.setSortingType(sortType.NONE);
                        if (view.getParent() instanceof RadioGroup) {
                            ((RadioGroup) view.getParent()).clearCheck();
                        }
                    } else {
                        sorting.setSortingType(sortType.BYCREATE);
                    }
                }
                break;
            }
            case R.id.chbx_updatedate: {
                if (checked) {
                    if (sorting.getSortingType().equals(sortType.BYUPDATE)) {
                        sorting.setSortingType(sortType.NONE);
                        if (view.getParent() instanceof RadioGroup) {
                            ((RadioGroup) view.getParent()).clearCheck();
                        }
                    } else {
                        sorting.setSortingType(sortType.BYUPDATE);
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

    public class Sort {

        private sortType sortingType;
        private sortOrder numberSortOrder;
        private sortOrder createdSortOrder;
        private sortOrder updatedSortOrder;

        public Sort() {
            this.sortingType = sortType.NONE;
            this.numberSortOrder = sortOrder.DESC;
            this.createdSortOrder = sortOrder.DESC;
            this.updatedSortOrder = sortOrder.DESC;
        }

        public sortType getSortingType() {
            return sortingType;
        }

        public void setSortingType(sortType sortingType) {
            this.sortingType = sortingType;
        }

        public sortOrder getNumberSortOrder() {
            return numberSortOrder;
        }

        public void setNumberSortOrder(sortOrder numberSortOrder) {
            this.numberSortOrder = numberSortOrder;
        }

        public sortOrder getCreatedSortOrder() {
            return createdSortOrder;
        }

        public void setCreatedSortOrder(sortOrder createdSortOrder) {
            this.createdSortOrder = createdSortOrder;
        }

        public sortOrder getUpdatedSortOrder() {
            return updatedSortOrder;
        }

        public void setUpdatedSortOrder(sortOrder updatedSortOrder) {
            this.updatedSortOrder = updatedSortOrder;
        }
    }

    enum sortType {
        NONE,
        BYNUMBER,
        BYCREATE,
        BYUPDATE
    }

    enum sortOrder {
        ASC,
        DESC
    }
}
