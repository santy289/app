package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowBinding;
import com.rootnetapp.rootnetintranet.databinding.WorkflowFiltersMenuBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.WorkFlowCreateFragment;
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
    private String[] types;
    private int[] typeIds;

    protected static final int SWITCH_NUMBER = 500;
    protected static final int SWITCH_CREATED_DATE = 501;
    protected static final int SWITCH_UPDATED_DATE = 502;
    protected static final int RADIO_NUMBER = 600;
    protected static final int RADIO_CREATED_DATE = 601;
    protected static final int RADIO_UPDATED_DATE = 602;
    protected static final int RADIO_CLEAR_ALL = 603;
    protected static final int SWITCH_PENDING = 700;
    protected static final int SWITCH_STATUS = 701;
    protected static final int SELECT_TYPE = 702;

    protected static final int CHECK = 11;
    protected static final int UNCHECK = 10;
    protected static final int INDEX_TYPE = 0;
    protected static final int INDEX_CHECK = 1;


    // Used when we have a general workflow.
    final Observer<PagedList<WorkflowListItem>> getAllWorkflowsObserver = (listWorkflows -> {
        if (adapter == null) {
            return;
        }
        workflowViewModel.handleUiAndIncomingList(listWorkflows);
    });

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

        setupWorkflowRecyclerView();
        setupClickListeners();
        setupSearchListener();
        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        workflowViewModel.initWorkflowList(prefs, this);
        subscribe();
        return view;
    }

    @Override
    public void dataAdded() {
        //workflowViewModel.getWorkflows(token);
    }

    @Override
    public void showDetail(WorkflowListItem item) {
        mainActivityInterface.showFragment(WorkflowDetailFragment.newInstance(item,
                mainActivityInterface),true);
    }

    private void setupSearchListener() {
        fragmentWorkflowBinding.inputSearch.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String searchText = fragmentWorkflowBinding.inputSearch.getText().toString();
                workflowViewModel.filterBySearchText(searchText, this);
                return true;
            }
            return false;
        });
    }

    private void setupWorkflowRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fragmentWorkflowBinding.recWorkflows.setLayoutManager(mLayoutManager);
        adapter = new WorkflowExpandableAdapter(this);
        fragmentWorkflowBinding.recWorkflows.setAdapter(adapter);
    }

    private void updateAdapterList(PagedList<WorkflowListItem> workflowDbList) {
        adapter.submitList(workflowDbList);
    }

    private void setupClickListeners() {
        fragmentWorkflowBinding.btnFilters.setOnClickListener(view1 -> {
            PopupWindow popupwindow_obj = initPopMenu();
            popupwindow_obj.showAsDropDown(fragmentWorkflowBinding.btnFilters, -40, 18);
            subscribeToTypeMenu();
        });

        fragmentWorkflowBinding.btnAdd.setOnClickListener(view12 -> {
            mainActivityInterface.showFragment(WorkFlowCreateFragment.newInstance(
                    mainActivityInterface),
                    true
            );
        });

        fragmentWorkflowBinding.chbxSelectAll.setOnClickListener(view -> {
            boolean isChecked = fragmentWorkflowBinding.chbxSelectAll.isChecked();
            workflowViewModel.handleCheckboxAllOnClick(isChecked);
        });
    }

    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    private void showListContent(boolean show) {
        if (show) {
            fragmentWorkflowBinding.recWorkflows.setVisibility(View.VISIBLE);
            fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.GONE);
        } else {
            fragmentWorkflowBinding.recWorkflows.setVisibility(View.GONE);
            fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.VISIBLE);
        }
    }

    private void addWorkflowsObserver() {
        workflowViewModel.getAllWorkflows().observe(this, getAllWorkflowsObserver);
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            //Utils.hideLoading();
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        final Observer<Boolean> addWorkflowsObserver = (setWorkflows -> addWorkflowsObserver());

        // Used when we have some filter operation happening.
        final Observer<PagedList<WorkflowListItem>> updateWithSortedListObserver = (this::updateAdapterList);

        final Observer<int[]> toggleRadioButtonObserver = (toggle -> {
            if (toggle == null || toggle.length < 1) {
                return;
            }
            boolean check = toggle[INDEX_CHECK] == CHECK;
            toggleRadioButtonFilter(toggle[INDEX_TYPE], check);
        });

        final Observer<int[]> toggleSwitchObserver = (toggle -> {
            if (toggle == null || toggle.length < 1) {
                return;
            }
            boolean check = toggle[INDEX_CHECK] == CHECK;
            toggleAscendingDescendingSwitch(toggle[INDEX_TYPE], check);
        });

        final Observer<int[]> toggleSwitchFilterObserver = (toggle -> {
            if (toggle == null || toggle.length < 1) {
                return;
            }
            boolean check = toggle[INDEX_CHECK] == CHECK;
            toogleFilterSwitch(toggle[INDEX_TYPE], check);
        });

        final Observer<Integer> setSelectTypeObserver = ( index -> {
            if (index == null) {
                workflowFiltersMenuBinding.spnWorkflowtype.setSelection(WorkflowViewModel.NO_TYPE_SELECTED);
                return;
            }
            workflowFiltersMenuBinding.spnWorkflowtype.setSelection(index);
        });

        final Observer<Boolean> showListObserver = (this::showListContent);

        final Observer<Boolean> showLoadingObserver = (this::showLoading);

        final Observer<Boolean> setAllCeckboxesObserver = (isChecked -> {
            if (isChecked == null) {
                return;
            }
            adapter.setAllCheckboxes(isChecked);
        });

        workflowViewModel.getObservableError().observe(this, errorObserver);
        workflowViewModel.getObservableShowLoading().observe(this, showLoadingObserver);
        addWorkflowsObserver();
        workflowViewModel.getObservableUpdateWithSortedList().observe(this, updateWithSortedListObserver);
        workflowViewModel.getObservableToggleRadioButton().observe(this, toggleRadioButtonObserver);
        workflowViewModel.getObservableToggleSwitch().observe(this, toggleSwitchObserver);
        workflowViewModel.getObservableShowList().observe(this, showListObserver);
        workflowViewModel.getObservableAddWorkflowObserver().observe(this, addWorkflowsObserver);
        workflowViewModel.getObservableSetAllCheckboxesList().observe(this, setAllCeckboxesObserver);
        workflowViewModel.getObservableToggleFilterSwitch().observe(this, toggleSwitchFilterObserver);
        workflowViewModel.getObservableSetSelectType().observe(this, setSelectTypeObserver);
    }

    private void subscribeToTypeMenu() {
        final Observer<List<WorkflowTypeItemMenu>> typeListMenuObserver = (list -> {
            if (list == null) {
                return;
            }
            setupSpinnerWorkflowType(list);
        });
        workflowViewModel.getObservableTypeItemMenu().observe(this, typeListMenuObserver);
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
        workflowViewModel.initSortBy();
        workflowViewModel.initFilters();
        setFilterBoxListeners();
        return popupWindow;
    }

    private void setupSpinnerWorkflowType(List<WorkflowTypeItemMenu> itemMenus) {
        if (this.getContext() == null) {
            return;
        }
        if (workflowFiltersMenuBinding == null || workflowFiltersMenuBinding.spnWorkflowtype == null) {
            return;
        }

        typeIds = new int[itemMenus.size()];
        types = new String[itemMenus.size()];
        typeIds[0] = WorkflowViewModel.NO_TYPE_SELECTED;
        types[0] = getString(R.string.no_selection);
        WorkflowTypeItemMenu menu;
        int until = itemMenus.size();
        for (int i = 1; i < until; i++) {
            menu = itemMenus.get(i);
            typeIds[i] = menu.getId();
            types[i] = itemMenus.get(i).name;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                types
        );

        workflowFiltersMenuBinding.spnWorkflowtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (types == null || typeIds == null) {
                    return;
                }
                int workflowTypeId = typeIds[position];
                workflowViewModel.loadWorkflowsByType(workflowTypeId, WorkflowFragment.this);
                prepareWorkflowListWithFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not needed but class needs to override it.
            }
        });

        workflowFiltersMenuBinding.spnWorkflowtype.setAdapter(adapter);
    }

    private void prepareWorkflowListWithFilters() {
        boolean isCheckedMyPendings = workflowFiltersMenuBinding.swchMyworkflows.isChecked();
        boolean isCheckedStatus = workflowFiltersMenuBinding.swchStatus.isChecked();
        int typeIdPositionInArray = workflowFiltersMenuBinding.spnWorkflowtype.getSelectedItemPosition();
        int workflowTypeId = typeIds[typeIdPositionInArray];
        workflowViewModel.handleWorkflowTypeFilters(
                WorkflowFragment.this,
                workflowTypeId,
                typeIdPositionInArray,
                isCheckedMyPendings,
                isCheckedStatus);
    }

    private void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        workflowViewModel.handleRadioButtonClicked(checked, view.getId());
        prepareWorkflowListWithFilters();
    }


    private void setFilterBoxListeners() {
        // filter switch listeners
        workflowFiltersMenuBinding.swchMyworkflows.setOnClickListener(view -> {
            boolean isChecked = workflowFiltersMenuBinding.swchMyworkflows.isChecked();
            workflowViewModel.loadMyPendingWorkflows(isChecked, this);
        });

        workflowFiltersMenuBinding.swchStatus.setOnClickListener(view -> prepareWorkflowListWithFilters());

        // radio button listeners
        workflowFiltersMenuBinding.chbxWorkflownumber.setOnClickListener(this::onRadioButtonClicked);
        workflowFiltersMenuBinding.chbxCreatedate.setOnClickListener(this::onRadioButtonClicked);
        workflowFiltersMenuBinding.chbxUpdatedate.setOnClickListener(this::onRadioButtonClicked);

        // ascending / descending listeners

        workflowFiltersMenuBinding.swchWorkflownumber.setOnClickListener(view -> {
            Switch aSwitch = ((Switch)view);
            boolean isChecked = aSwitch.isChecked();
            workflowViewModel.handleSwitchOnClick(RADIO_NUMBER, Sort.sortType.BYNUMBER, isChecked);
            setSwitchAscendingDescendingText(workflowFiltersMenuBinding.swchWorkflownumber, isChecked);
            prepareWorkflowListWithFilters();
        });
        workflowFiltersMenuBinding.swchCreatedate.setOnClickListener(view -> {
            Switch aSwitch = ((Switch)view);
            boolean isChecked = aSwitch.isChecked();
            workflowViewModel.handleSwitchOnClick(RADIO_CREATED_DATE, Sort.sortType.BYCREATE, isChecked);
            setSwitchAscendingDescendingText(workflowFiltersMenuBinding.swchCreatedate, isChecked);
            prepareWorkflowListWithFilters();
        });
        workflowFiltersMenuBinding.swchUpdatedate.setOnClickListener(view -> {
            Switch aSwitch = ((Switch)view);
            boolean isChecked = aSwitch.isChecked();
            workflowViewModel.handleSwitchOnClick(RADIO_UPDATED_DATE, Sort.sortType.BYUPDATE, isChecked);
            setSwitchAscendingDescendingText(workflowFiltersMenuBinding.swchUpdatedate, isChecked);
            prepareWorkflowListWithFilters();
        });
    }


    private void toggleAscendingDescendingSwitch(int switchType, boolean check) {
        switch (switchType) {
            case SWITCH_NUMBER:
                workflowFiltersMenuBinding.swchWorkflownumber.setChecked(check);
                setSwitchAscendingDescendingText(workflowFiltersMenuBinding.swchWorkflownumber, check);
                break;
            case SWITCH_CREATED_DATE:
                workflowFiltersMenuBinding.swchCreatedate.setChecked(check);
                setSwitchAscendingDescendingText(workflowFiltersMenuBinding.swchCreatedate, check);
                break;
            case SWITCH_UPDATED_DATE:
                workflowFiltersMenuBinding.swchUpdatedate.setChecked(check);
                setSwitchAscendingDescendingText(workflowFiltersMenuBinding.swchUpdatedate, check);
                break;
            default:
                Log.d(TAG, "toggleAscendingDescendingSwitch: Trying to perform a toggle and there is no related Switch object");
                break;
        }
    }

    private void toogleFilterSwitch(int switchType, boolean check) {
        switch (switchType) {
            case SWITCH_PENDING:
                workflowFiltersMenuBinding.swchMyworkflows.setChecked(check);
                break;
            case SWITCH_STATUS:
                workflowFiltersMenuBinding.swchStatus.setChecked(check);
                break;
            default:
                Log.d(TAG, "toogleFilterSwitch: Trying to perform a toggle and there is no related Switch object");
                break;
        }
    }

    private void setSwitchAscendingDescendingText(Switch switchType, boolean check) {
        if (check) {
            switchType.setText(getString(R.string.ascending));
        } else {
            switchType.setText(getString(R.string.descending));
        }
    }

    private void toggleRadioButtonFilter(int radioType, boolean check) {
        switch (radioType) {
            case RADIO_NUMBER:
                workflowFiltersMenuBinding.chbxWorkflownumber.setChecked(check);
                break;
            case RADIO_CREATED_DATE:
                workflowFiltersMenuBinding.chbxCreatedate.setChecked(check);
                break;
            case RADIO_UPDATED_DATE:
                workflowFiltersMenuBinding.chbxUpdatedate.setChecked(check);
                break;
            case RADIO_CLEAR_ALL:
                workflowFiltersMenuBinding.radioGroupSortBy.clearCheck();
            default:
                Log.d(TAG, "toggleRadioButtonFilter: Trying to perform toggle on unknown radio button");
                break;
        }
    }
}
