package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.RightDrawerOptionList;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FieldData;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerFiltersAdapter;

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

    protected void updateFilterListItemSelected(WorkflowTypeMenu withMenuItem) {
        setWorkflowTypeId(withMenuItem.getWorkflowTypeId());
        WorkflowTypeMenu filterItemMenu = filterDrawerList.get(filterListIndexSelected);
        String stringsSelected = getAllOptionsSelectedAsString(withMenuItem);
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

    protected void updateRightDrawerOptionListWithSelected(WorkflowTypeMenu menu, boolean isMultipleChoice) {
        WorkflowTypeMenu dynamicFieldInFilterList = filterDrawerList.get(filterListIndexSelected);
        RightDrawerOptionList drawerOptionList = getRightDrawerOptionList(dynamicFieldInFilterList.getId());
        if (drawerOptionList == null) {
            Log.d(TAG, "updateRightDrawerOptionListWithSelected: Not able to find a RightDrawerOption");
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

        // TODO check if we need this block of code again, it was used only to comply with getMeta string
        // from FormSettings. The idea was to generate the same input "some, some2, some3" using
        // commas in between the values but now we are generating our own String using getAllItemSelectedasString()
        // function. Remove this if we don't need it.
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
        RightDrawerOptionList typeOptionDrawerList = rightDrawerOptionsList.get(RIGHT_DRAWER_FILTER_TYPE_ITEM_ID);
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


    protected String getAllOptionsSelectedAsString(WorkflowTypeMenu menu) {
        WorkflowTypeMenu dynamicFieldInFilterList = filterDrawerList.get(filterListIndexSelected);
        RightDrawerOptionList drawerOptionList = getRightDrawerOptionList(dynamicFieldInFilterList.getId());
        if (drawerOptionList == null) {
            Log.d(TAG, "updateRightDrawerOptionListWithSelected: Not able to find a RightDrawerOption");
            return "";
        }

        Integer fieldId = menu.getId();
        List<WorkflowTypeMenu> optionListItems = drawerOptionList.getOptionItems();
        WorkflowTypeMenu optionMenu;
        ArrayList<String> stringOfOptionsSelected = drawerOptionList.getStringOptionsSelected();
//        for (int i = 0; i < optionListItems.size(); i++) {
//            optionMenu = optionListItems.get(i);
//            if (optionMenu.getId() == fieldId) {
//                String label = optionMenu.getLabel();
//                if (stringOfOptionsSelected.contains(label)) {
//                    stringOfOptionsSelected.remove(label);
//                } else {
//                    stringOfOptionsSelected.add(label);
//                }
//                break;
//            }
//        }
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


    protected int[] arrayOfIdsSelected() {
        String result = "";
        WorkflowTypeMenu dynamicFieldInFilterList = filterDrawerList.get(filterListIndexSelected);
        RightDrawerOptionList drawerOptionList = getRightDrawerOptionList(dynamicFieldInFilterList.getId());

        ArrayList<Integer> ids = drawerOptionList.getFieldIdOptionsSelected();
        if (ids.size() < 1) {
            return null;
        }

        int[] selectedIds = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            selectedIds[i] = ids.get(i);
        }
        return selectedIds;
    }

    protected String getAllItemIdsSelectedAsString() {
        if (rightDrawerOptionsList.isEmpty()) {
            return "";
        }

        StringBuilder responseString = new StringBuilder();
        responseString.append("{");
        RightDrawerOptionList dynamicFieldOptions;
        int fieldOptionId, fieldId, idOptionSelected, selectedSize2;
        ArrayList<Integer> optionSelectedIds;
        int optionsSize = rightDrawerOptionsList.size();
        for (int i = 0; i < optionsSize; i++) {
            dynamicFieldOptions = rightDrawerOptionsList.valueAt(i);
            fieldOptionId = dynamicFieldOptions.getId();
            if (fieldOptionId == RIGHT_DRAWER_FILTER_TYPE_ITEM_ID) {
                continue;
            }
            optionSelectedIds = dynamicFieldOptions.getFieldIdOptionsSelected();
            if (optionSelectedIds.size() == 0) {
                continue;
            }

            if (optionSelectedIds.size() == 1) {
                if (i > 1) {
                    responseString.append(",");
                }
                fieldId = rightDrawerOptionsList.keyAt(i);
                responseString
                        .append("\"")
                        .append(fieldId)
                        .append("\":");
                responseString.append(optionSelectedIds.get(0));
                continue;
            }
            if (i > 1) {
                responseString.append(",");
            }
            fieldId = rightDrawerOptionsList.keyAt(i);
            responseString
                    .append("\"")
                    .append(fieldId)
                    .append("\":")
                    .append("[");
            selectedSize2 = optionSelectedIds.size();
            for (int j = 0; j < optionSelectedIds.size(); j++) {
                idOptionSelected = optionSelectedIds.get(j);
                responseString.append(idOptionSelected);
                if (j < selectedSize2 - 1) {
                    responseString.append(",");
                }
            }
            responseString.append("]");
        }
        responseString.append("}");
        return responseString.toString();
    }



    protected String getAllValuesSelectedInOptionList() {
        WorkflowTypeMenu dynamicFieldInFilterList = filterDrawerList.get(filterListIndexSelected);
        RightDrawerOptionList drawerOptionList = getRightDrawerOptionList(dynamicFieldInFilterList.getId());
        ArrayList<String> stringOfOptionsSelected = drawerOptionList.getStringOptionsSelected();
        if (stringOfOptionsSelected.size() < 1) {
            return "";
        }
        if (stringOfOptionsSelected.size() == 1) {
            return stringOfOptionsSelected.get(0);
        }
        StringBuilder stringBuilder = new StringBuilder();
        int size = stringOfOptionsSelected.size();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(stringOfOptionsSelected.get(i));
            if (i < size - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    protected FieldData getFieldDataFromSelectedOptionList() {
        WorkflowTypeMenu dynamicFieldInFilterList = filterDrawerList.get(filterListIndexSelected);
        RightDrawerOptionList drawerOptionList = getRightDrawerOptionList(dynamicFieldInFilterList.getId());
        if (drawerOptionList == null) {
            return null;
        }
        FieldData fieldData = drawerOptionList.getFieldData();
        if (fieldData == null) {
            return null;
        }
        return fieldData;
    }


    protected FieldConfig getFieldConfigFromDrawerOptionList() {
        WorkflowTypeMenu dynamicFieldInFilterList = filterDrawerList.get(filterListIndexSelected);
        RightDrawerOptionList drawerOptionList = getRightDrawerOptionList(dynamicFieldInFilterList.getId());
        if (drawerOptionList == null) {
            return null;
        }
        FieldConfig fieldConfig = drawerOptionList.getFieldConfig();
        if (fieldConfig == null) {
            return null;
        }
        return fieldConfig;
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
        // TODO find label Title for RightDrawerOptionList object.
        String title = findLabelTitle(fieldId);
        RightDrawerOptionList drawerOptionList = new RightDrawerOptionList();
        drawerOptionList.setId(fieldId);
        drawerOptionList.setOptionListTitle(title);
        drawerOptionList.setOptionListTitleRes(R.string.workflow_type);
        drawerOptionList.setOptionItems(optionsList);
        rightDrawerOptionsList.put(fieldId, drawerOptionList);
    }

    protected void updateFilterListWithDynamicField(ListField listField, FieldData fieldData, FieldConfig fieldConfig) {
        // TODO use
        //saveOptionsListFor(int filterFieldId, ArrayList<WorkflowTypeMenu> optionsList) {

//        example
//        WorkflowTypeMenu noSelection = new WorkflowTypeMenu(
//                0,
//                "NO SELECTION",
//                WorkflowTypeSpinnerAdapter.NO_SELECTION
//        );
//        spinnerMenuArray.add(0, noSelection);
//        filterSettings.saveOptionsListFor(FilterSettings.RIGHT_DRAWER_FILTER_TYPE_ITEM_ID, spinnerMenuArray);

        RightDrawerOptionList drawerOptionList = new RightDrawerOptionList();
        drawerOptionList.updateValuesWith(listField);
        drawerOptionList.setFieldData(fieldData);
        drawerOptionList.setFieldConfig(fieldConfig);
        rightDrawerOptionsList.put(listField.customFieldId, drawerOptionList);

        WorkflowTypeMenu filterMenuItem = new WorkflowTypeMenu(
                listField.customFieldId,
                listField.customLabel,
                "",
                RightDrawerFiltersAdapter.TYPE,
                0
        );
        addFilterListMenu(filterMenuItem);
    }


    private int filterListIndexSelected;

    public int getFilterListIndexSelected() {
        return filterListIndexSelected;
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
                return  optionsObj;
            }
        }
        return null;
    }

}
