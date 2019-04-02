package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.util.ArrayMap;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.RightDrawerOptionList;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowViewModel.BASE_FILTER_ALL_ID;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowViewModel.NO_TYPE_SELECTED;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowViewModel.STATUS_FILTER_OPEN_ID;

public class FilterSettings {

    private boolean isCheckedMyPending;
    private boolean isCheckedStatus;
    private int typeIdPositionInArray;

    // Todo for now we are using the naming workflowTypeId, but we should change it to originalTypeId.
    // todo this is because we are using original_id property from our WorkflowTypeDB in this variable.
    private int workflowTypeId;

    private String searchText;
    private boolean onTime;
    private boolean latest;

    public static final int RIGHT_DRAWER_FILTER_TYPE_ITEM_ID = -10;
    private final String TAG = "FILTER_SETTINGS";

    // All dynamic filters and their options are here. Including filter by workflow type.
    private ArrayMap<Integer, RightDrawerOptionList> rightDrawerOptionsList;
    // Filter List with each item UI data and Id reference to use look for options in rightDrawerOptionsList.
    private List<WorkflowTypeMenu> filterDrawerList;

    private List<WorkflowTypeMenu> workflowTypeOptionsList;
    private List<WorkflowTypeMenu> baseFilterOptionsList;
    private List<WorkflowTypeMenu> statusFilterOptionList;
    private Map<String, Object> dynamicFilters;
    private int workflowTypeFilterIndexSelected;
    private int baseFilterIndexSelected;
    private int statusFilterIndexSelected;

    public FilterSettings() {
        this.isCheckedMyPending = false;
        this.isCheckedStatus = true;
        this.typeIdPositionInArray = 0;
        this.workflowTypeId = NO_TYPE_SELECTED;
        this.searchText = "";
        this.onTime = true;
        this.latest = true;
        this.rightDrawerOptionsList = new ArrayMap<>();
        this.filterDrawerList = new ArrayList<>();
        this.workflowTypeOptionsList = new ArrayList<>();
        this.baseFilterOptionsList = new ArrayList<>();
        this.statusFilterOptionList = new ArrayList<>();
        this.dynamicFilters = new HashMap<>();
        this.workflowTypeFilterIndexSelected = 0;
        this.baseFilterIndexSelected = 0;
        this.statusFilterIndexSelected = 1;
    }

    public void resetFilterSettings() {
        int test = 0;
    }

    public int getSizeOfRightDrawerOptionsListMap() {
        return rightDrawerOptionsList.size();
    }

    public boolean isCheckedMyPending() {
        return isCheckedMyPending;
    }

    public void setCheckedMyPending(boolean checkedMyPending) {
        isCheckedMyPending = checkedMyPending;
    }

    public boolean isCheckedStatus() {
        return isCheckedStatus;
    }

    public void setCheckedStatus(boolean checkedStatus) {
        isCheckedStatus = checkedStatus;
    }

    public int getTypeIdPositionInArray() {
        return typeIdPositionInArray;
    }

    public void setTypeIdPositionInArray(int typeIdPositionInArray) {
        this.typeIdPositionInArray = typeIdPositionInArray;
    }

    public List<WorkflowTypeMenu> getBaseFilterOptionsList() {
        return baseFilterOptionsList;
    }

    public List<WorkflowTypeMenu> getWorkflowTypeOptionsList() {
        return workflowTypeOptionsList;
    }

    public int getSizeWorkflowTypeOptionList() {
        return workflowTypeOptionsList.size();
    }

    public void setWorkflowTypeOptionsList(List<WorkflowTypeMenu> workflowTypeOptionsList) {
        this.workflowTypeOptionsList = workflowTypeOptionsList;
    }

    public int getSizeBseFilterOptionList() {
        return baseFilterOptionsList.size();
    }

    public void setBaseFilterOptionsList(List<WorkflowTypeMenu> baseFilterOptionsList) {
        this.baseFilterOptionsList = baseFilterOptionsList;
    }

    public int getSizeStatusFilterOptionList() {
        return statusFilterOptionList.size();
    }

    public void setStatusFilterOptionList(List<WorkflowTypeMenu> statusFilterOptionList) {
        this.statusFilterOptionList = statusFilterOptionList;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(int workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Map<String, Object> getDynamicFilters() {
        return dynamicFilters;
    }

    public void addDynamicFilter(String key, Object value) {
        if (dynamicFilters.containsKey(key)) dynamicFilters.remove(key);

        dynamicFilters.put(key, value);
    }

    public ArrayMap<Integer, RightDrawerOptionList> getRightDrawerOptionsList() {
        return rightDrawerOptionsList;
    }

    public void setRightDrawerOptionsList(
            ArrayMap<Integer, RightDrawerOptionList> rightDrawerOptionsList) {
        this.rightDrawerOptionsList = rightDrawerOptionsList;
    }

    public void addRightDrawerOptions(int id, RightDrawerOptionList optionsList) {
        this.rightDrawerOptionsList.put(id, optionsList);
    }

    public List<WorkflowTypeMenu> getFilterDrawerList() {
        return filterDrawerList;
    }

    public void setFilterDrawerList(List<WorkflowTypeMenu> filterDrawerList) {
        this.filterDrawerList = filterDrawerList;
    }

    public void addFilterListMenu(WorkflowTypeMenu menu) {
        filterDrawerList.add(menu);
    }

    protected boolean hasIdinFilterDrawerList(int id) {
        WorkflowTypeMenu filter;
        for (int i = 0; i < filterDrawerList.size(); i++) {
            filter = filterDrawerList.get(i);
            if (filter.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public WorkflowTypeMenu getFilterListItem(int atPosition) {
        return filterDrawerList.get(atPosition);
    }

    public void saveOptionsListFor(int filterFieldId, ArrayList<WorkflowTypeMenu> optionsList) {
        switch (filterFieldId) {
            case RIGHT_DRAWER_FILTER_TYPE_ITEM_ID:
                handleTypeOptions(filterFieldId, optionsList);
                break;
            default:
                Log.d(TAG, "saveOptionsListFor: ");
                break;
        }
    }

    protected void updateFilterListItemSelected(WorkflowTypeMenu withMenuItem) {
        setWorkflowTypeId(withMenuItem.getWorkflowTypeId());
        WorkflowTypeMenu filterItemMenu = filterDrawerList.get(filterListIndexSelected);
        String stringsSelected = getAllOptionsSelectedAsString();
        filterItemMenu.setSubTitle(stringsSelected);
    }

    protected void updateWorkflowTypeListFilterItem(WorkflowTypeMenu menu) {
        WorkflowTypeMenu workflowTypeMenu = filterDrawerList.get(0);
        if (menu == null) {
            workflowTypeMenu.setSubTitle("");
            workflowTypeMenu.setWorkflowTypeId(WorkflowViewModel.WORKFLOW_TYPE_FIELD);
            return;
        }
        workflowTypeMenu.setSubTitle(menu.getLabel());
        workflowTypeMenu.setWorkflowTypeId(menu.getWorkflowTypeId());
    }

    protected boolean isTypeAlreadySelected(int workflowTypeId) {
        WorkflowTypeMenu workflowTypeMenu = filterDrawerList.get(0);
        return workflowTypeMenu.getWorkflowTypeId() == workflowTypeId;
    }

    protected void clearDynamicFields() {
        RightDrawerOptionList typeList = rightDrawerOptionsList.valueAt(0);
        Integer listItemId = rightDrawerOptionsList.keyAt(0);
        rightDrawerOptionsList.clear();
        rightDrawerOptionsList.put(listItemId, typeList);

        WorkflowTypeMenu workflowTypeMenu = filterDrawerList.get(0);
        filterDrawerList.clear();
        filterDrawerList.add(workflowTypeMenu);
    }

    protected void updateRightDrawerOptionListWithSelected(WorkflowTypeMenu menu,
                                                           boolean isMultipleChoice) {
        WorkflowTypeMenu dynamicFieldInFilterList = filterDrawerList.get(filterListIndexSelected);
        RightDrawerOptionList drawerOptionList = getRightDrawerOptionList(
                dynamicFieldInFilterList.getId());
        if (drawerOptionList == null) {
            Log.d(TAG,
                    "updateRightDrawerOptionListWithSelected: Not able to find a RightDrawerOption");
            return;
        }
        Integer fieldId = menu.getId();
        ArrayList<Integer> fieldIds = drawerOptionList.getFieldIdOptionsSelected();
        if (fieldIds.contains(fieldId)) {
            fieldIds.remove(fieldId);
        } else {
            if (isMultipleChoice) {
                fieldIds.add(fieldId);
            } else {
                fieldIds.clear();
                fieldIds.add(fieldId);
            }

        }

        List<WorkflowTypeMenu> optionListItems = drawerOptionList.getOptionItems();
        WorkflowTypeMenu optionMenu;
        ArrayList<String> stringOfOptionsSelected = drawerOptionList.getStringOptionsSelected();
        for (int i = 0; i < optionListItems.size(); i++) {
            optionMenu = optionListItems.get(i);
            if (optionMenu.getId() == fieldId) {
                String label = optionMenu.getLabel();
                if (stringOfOptionsSelected.contains(label)) {
                    stringOfOptionsSelected.remove(label);
                } else {
                    if (isMultipleChoice) {
                        stringOfOptionsSelected.add(label);
                    } else {
                        stringOfOptionsSelected.clear();
                        stringOfOptionsSelected.add(label);
                    }

                }
                break;
            }
        }

    }

    protected void clearworklowTypeSelection() {
        RightDrawerOptionList typeOptionDrawerList = rightDrawerOptionsList
                .get(RIGHT_DRAWER_FILTER_TYPE_ITEM_ID);
        //we always have only one this is Workflow Type options.
        List<Integer> selectedArray = typeOptionDrawerList.getFieldIdOptionsSelected();
        if (selectedArray.isEmpty()) {
            return;
        }
        int selectedId = selectedArray.get(0); // compare to id in each optionItems items.

        List<WorkflowTypeMenu> optionItems = typeOptionDrawerList.getOptionItems();
        WorkflowTypeMenu menu;
        for (int i = 0; i < optionItems.size(); i++) {
            menu = optionItems.get(i);
            if (menu.getId() == selectedId) {
                menu.setSelected(false);
                return;
            }
        }
    }

    protected String getAllOptionsSelectedAsString() {
        WorkflowTypeMenu dynamicFieldInFilterList = filterDrawerList.get(filterListIndexSelected);
        RightDrawerOptionList drawerOptionList = getRightDrawerOptionList(
                dynamicFieldInFilterList.getId());
        if (drawerOptionList == null) {
            Log.d(TAG,
                    "updateRightDrawerOptionListWithSelected: Not able to find a RightDrawerOption");
            return "";
        }

        ArrayList<String> stringOfOptionsSelected = drawerOptionList.getStringOptionsSelected();
        StringBuilder resultString = new StringBuilder();
        String selectedItem;
        int selectedSize = stringOfOptionsSelected.size();
        for (int i = 0; i < selectedSize; i++) {
            selectedItem = stringOfOptionsSelected.get(i);
            resultString.append(selectedItem);
            if (i < selectedSize - 1) {
                resultString.append(",");
            }
        }
        return resultString.toString();
    }

    protected List<WorkflowTypeMenu> getOptionsListAtSelectedFilterIndex() {
        WorkflowTypeMenu filterMenu = filterDrawerList.get(filterListIndexSelected);
        RightDrawerOptionList drawerOptionList = getRightDrawerOptionList(filterMenu.getId());
        if (drawerOptionList == null || drawerOptionList.getOptionItems() == null) {
            return new ArrayList<>();
        }
        return drawerOptionList.getOptionItems();
    }

    private void handleTypeOptions(int fieldId, ArrayList<WorkflowTypeMenu> optionsList) {
        String title = findLabelTitle(fieldId);
        RightDrawerOptionList drawerOptionList = new RightDrawerOptionList();
        drawerOptionList.setId(fieldId);
        drawerOptionList.setOptionListTitle(title);
        drawerOptionList.setOptionListTitleRes(R.string.workflow_type);
        drawerOptionList.setOptionItems(optionsList);
        rightDrawerOptionsList.put(fieldId, drawerOptionList);
    }

    private int filterListIndexSelected;

    public int getFilterListIndexSelected() {
        return filterListIndexSelected;
    }

    protected OptionsList handleOptionListForWorkflowTypeFilters() {
        if (baseFilterOptionsList == null) {
            return null;
        }
        OptionsList optionsList = new OptionsList();
        optionsList.titleLabelRes = R.string.workflow_type;
        optionsList.optionsList = workflowTypeOptionsList;
        return optionsList;
    }

    protected OptionsList handleOptionListForBaseFilters() {
        if (baseFilterOptionsList == null) {
            return null;
        }
        OptionsList optionsList = new OptionsList();
        optionsList.titleLabelRes = R.string.base_filters;
        optionsList.optionsList = baseFilterOptionsList;
        return optionsList;
    }

    protected OptionsList handleOptionListForStatusFilters() {
        if (statusFilterOptionList == null) {
            return null;
        }
        OptionsList optionsList = new OptionsList();
        optionsList.titleLabelRes = R.string.status_filters;
        optionsList.optionsList = statusFilterOptionList;
        return optionsList;
    }

    protected OptionsList handleFilterListPositionSelected(int position) {
        WorkflowTypeMenu menuSelected = filterDrawerList.get(position);
        filterListIndexSelected = position;
        RightDrawerOptionList optionsObj = getRightDrawerOptionList(menuSelected.getId());
        if (optionsObj == null) {
            return null;
        }
        OptionsList optionsList = new OptionsList();
        optionsList.titleLabel = optionsObj.getOptionListTitle();
        optionsList.optionsList = optionsObj.getOptionItems();
        return optionsList;
    }

    protected void resetWorkflowTypeFilterSelectionToAll() {
        workflowTypeFilterIndexSelected = 0; //all index
        if (workflowTypeOptionsList.size() < 1) {
            return;
        }
        WorkflowTypeMenu filter = workflowTypeOptionsList.get(workflowTypeFilterIndexSelected);
        filter.setSelected(true);
    }

    protected void resetBaseFilterSelectionToAll() {
        baseFilterIndexSelected = 0; //all index
        if (baseFilterOptionsList.size() < 1) {
            return;
        }
        WorkflowTypeMenu baseFilter = baseFilterOptionsList.get(baseFilterIndexSelected);
        baseFilter.setSelected(true);
    }

    protected void resetStatusFilterSelectionToOpen() {
        statusFilterIndexSelected = 1; //open index
        if (statusFilterOptionList.size() < 1) {
            return;
        }
        WorkflowTypeMenu baseFilter = statusFilterOptionList.get(statusFilterIndexSelected);
        baseFilter.setSelected(true);
    }

    /**
     * Finds which base filter is selected in baseFilterOptionsList.
     *
     * @return Returns the base filter selected
     */
    protected int getBaseFilterSelectedId() {
        if (baseFilterOptionsList.size() < 1) {
            return BASE_FILTER_ALL_ID;
        }
        WorkflowTypeMenu baseFilter = baseFilterOptionsList.get(baseFilterIndexSelected);
        return baseFilter.getId();
    }

    /**
     * Finds which base filter is selected in statusFilterOptionList.
     *
     * @return Returns the base filter selected
     */
    protected int getStatusFilterSelectedId() {
        if (statusFilterOptionList.size() < 1) {
            return STATUS_FILTER_OPEN_ID;
        }
        WorkflowTypeMenu baseFilter = statusFilterOptionList.get(statusFilterIndexSelected);
        return baseFilter.getId();
    }

    /**
     * FilterSettings updates workflowTypeFilterIndexSelected, and clears any other items selected. Updates
     * workflowTypeOptionsList and marks as selected on the position given.
     *
     * @param position Index position to mark as selected in workflowTypeOptionsList.
     *
     * @return Returns a WorkflowTypeMenu as selected or not selected if it was previously selected.
     */
    protected WorkflowTypeMenu handleWorkflowTypeFilterPositionSelected(int position) {
        WorkflowTypeMenu filterSelected;
        if (position == workflowTypeFilterIndexSelected) {
            filterSelected = workflowTypeOptionsList.get(position);
            filterSelected.setSelected(false);
            workflowTypeFilterIndexSelected = 0;
            return filterSelected;
        }

        // Clear previously selected base filter.
        filterSelected = workflowTypeOptionsList.get(workflowTypeFilterIndexSelected);
        filterSelected.setSelected(false);

        // Setting new selected base filter.
        workflowTypeFilterIndexSelected = position;
        filterSelected = workflowTypeOptionsList.get(workflowTypeFilterIndexSelected);
        filterSelected.setSelected(true);

        return filterSelected;
    }

    /**
     * FilterSettings updates baseFilterIndexSelected, and clears any other items selected. Updates
     * baseFilterOptionsList and marks as selected on the position given.
     *
     * @param position Index position to mark as selected in baseFilterOptionsList.
     *
     * @return Returns a WorkflowTypeMenu as selected or not selected if it was previously selected.
     */
    protected WorkflowTypeMenu handleBaseFilterPositionSelected(int position) {
        WorkflowTypeMenu filterSelected;
        if (position == baseFilterIndexSelected) {
            filterSelected = baseFilterOptionsList.get(position);
            filterSelected.setSelected(false);
            baseFilterIndexSelected = 0;
            return filterSelected;
        }

        // Clear previously selected base filter.
        filterSelected = baseFilterOptionsList.get(baseFilterIndexSelected);
        filterSelected.setSelected(false);

        // Setting new selected base filter.
        baseFilterIndexSelected = position;
        filterSelected = baseFilterOptionsList.get(baseFilterIndexSelected);
        filterSelected.setSelected(true);

        return filterSelected;
    }

    /**
     * FilterSettings updates baseFilterIndexSelected, and clears any other items selected. Updates
     * baseFilterOptionsList and marks as selected on the position given.
     *
     * @param position Index position to mark as selected in baseFilterOptionsList.
     *
     * @return Returns a WorkflowTypeMenu as selected or not selected if it was previously selected.
     */
    protected WorkflowTypeMenu handleStatusFilterPositionSelected(int position) {
        WorkflowTypeMenu filterSelected;
        if (position == statusFilterIndexSelected) {
            filterSelected = statusFilterOptionList.get(position);
            filterSelected.setSelected(false);
            statusFilterIndexSelected = 0;
            return filterSelected;
        }

        // Clear previously selected base filter.
        filterSelected = statusFilterOptionList.get(statusFilterIndexSelected);
        filterSelected.setSelected(false);

        // Setting new selected base filter.
        statusFilterIndexSelected = position;
        filterSelected = statusFilterOptionList.get(statusFilterIndexSelected);
        filterSelected.setSelected(true);

        return filterSelected;
    }

    private String findLabelTitle(int inId) {
        WorkflowTypeMenu menu;
        for (int i = 0; i < filterDrawerList.size(); i++) {
            menu = filterDrawerList.get(i);
            if (menu.getId() == inId) {
                return menu.getLabel();
            }
        }
        return "";
    }

    private RightDrawerOptionList getRightDrawerOptionList(int filterListItemId) {
        WorkflowTypeMenu menu;
        RightDrawerOptionList optionsObj;
        for (int i = 0; i < filterDrawerList.size(); i++) {
            menu = filterDrawerList.get(i);
            // TODO mejorar se puede llamar directamente al rightDrawerOptionsList y pasar el filterListItemId
            if (menu.getId() == filterListItemId) {
                optionsObj = rightDrawerOptionsList.get(filterListItemId);
                return optionsObj;
            }
        }
        return null;
    }

}
