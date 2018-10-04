package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.util.ArrayMap;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.RightDrawerOptionList;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;

import java.util.ArrayList;
import java.util.List;

public class FilterSettings {
    private boolean isCheckedMyPending;
    private boolean isCheckedStatus;
    private int typeIdPositionInArray;
    private int workflowTypeId;
    private String searchText;

    public static final int RIGHT_DRAWER_FILTER_TYPE_ITEM_ID = -10;
    private final String TAG = "FILTER_SETTINGS";

    private ArrayMap<Integer, RightDrawerOptionList> rightDrawerOptionsList;
    private List<WorkflowTypeMenu> filterDrawerList;


    public FilterSettings() {
        this.isCheckedMyPending = false;
        this.isCheckedStatus = true;
        this.typeIdPositionInArray = 0;
        this.workflowTypeId = 0;
        this.searchText = "";
        this.rightDrawerOptionsList = new ArrayMap<>();
        this.filterDrawerList = new ArrayList<>();
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

    public ArrayMap<Integer, RightDrawerOptionList> getRightDrawerOptionsList() {
        return rightDrawerOptionsList;
    }

    public void setRightDrawerOptionsList(ArrayMap<Integer, RightDrawerOptionList> rightDrawerOptionsList) {
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

    private int filterListIndexSelected;
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

    private void handleTypeOptions(int fieldId, ArrayList<WorkflowTypeMenu> optionsList) {
        // TODO find label Title for RightDrawerOptionList object.
        String title = findLabelTitle(fieldId);
        RightDrawerOptionList drawerOptionList = new RightDrawerOptionList();
        drawerOptionList.setId(fieldId);
        drawerOptionList.setOptionListTitle(title);
        drawerOptionList.setOptionListTitleRes(R.string.workflow_type);
        drawerOptionList.setOptionItems(optionsList);
        rightDrawerOptionsList.put(fieldId, drawerOptionList);
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
            if (menu.getId() == filterListItemId) {
                optionsObj = rightDrawerOptionsList.get(filterListItemId);
                return  optionsObj;
            }
        }
        return null;
    }

}
