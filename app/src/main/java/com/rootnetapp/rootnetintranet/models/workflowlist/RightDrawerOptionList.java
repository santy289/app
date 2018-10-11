package com.rootnetapp.rootnetintranet.models.workflowlist;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FieldData;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.WorkflowTypeSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RightDrawerOptionList extends ListField {
    private int id;
    private String optionListTitle;
    private int optionListTitleRes;
    private String labelValueSelected;
    private int valueInt;

    private ArrayList<String> stringOptionsSelected;
    private ArrayList<Integer> intOptionsSelected;
    private ArrayList<Integer> fieldIdOptionsSelected;

    private String valueString;
    private List<WorkflowTypeMenu> optionItems;

    // TODO get rid of fieldData later.
    private FieldData fieldData;
    private FieldConfig fieldConfig;

    public RightDrawerOptionList() {
        id = 0;
        optionListTitle = "";
        optionListTitleRes = 0;
        labelValueSelected = "";
        valueInt = 0;
        valueString = "";
        optionItems = new ArrayList<>();
        stringOptionsSelected = new ArrayList<>();
        intOptionsSelected = new ArrayList<>();
        fieldIdOptionsSelected = new ArrayList<>();
    }

    public void updateValuesWith(ListField listField) {
        this.children = listField.children;
        this.customFieldId = listField.customFieldId;
        this.id = this.customFieldId;
        this.customLabel = listField.customLabel;
        this.optionListTitle = listField.customLabel;
        this.isMultipleSelection = listField.isMultipleSelection;
        this.associatedWorkflowTypeId = listField.associatedWorkflowTypeId;
        updateOptionItemsList(this.children);
    }

    public FieldConfig getFieldConfig() {
        return fieldConfig;
    }

    public void setFieldConfig(FieldConfig fieldConfig) {
        this.fieldConfig = fieldConfig;
    }

    public FieldData getFieldData() {
        return fieldData;
    }

    public void setFieldData(FieldData fieldData) {
        this.fieldData = fieldData;
    }

    public ArrayList<String> getStringOptionsSelected() {
        return stringOptionsSelected;
    }

    public void addStringOptionsSelected(String stringOptionSelected) {
        this.stringOptionsSelected.add(stringOptionSelected);
    }

    public ArrayList<Integer> getIntOptionsSelected() {
        return intOptionsSelected;
    }

    public void addIntOptionsSelected(Integer intOptionSelected) {
        this.intOptionsSelected.add(intOptionSelected);
    }

    public ArrayList<Integer> getFieldIdOptionsSelected() {
        return fieldIdOptionsSelected;
    }

    public void addFieldIdOptionSelected(Integer fieldIdOptionSelected) {
        this.fieldIdOptionsSelected.add(fieldIdOptionSelected);
    }

    public void updateOptionItemsList(ArrayList<ListFieldItemMeta> children) {
        ListFieldItemMeta child;
        WorkflowTypeMenu menu;
        for (int i = 0; i < children.size(); i++) {
            child = children.get(i);
            menu = new WorkflowTypeMenu();
            menu.setId(child.id);
            menu.setLabel(child.name);
            menu.setWorkflowTypeId(this.associatedWorkflowTypeId);
            menu.setRowType(WorkflowTypeSpinnerAdapter.TYPE);
            optionItems.add(menu);
        }
    }

    public void getChildrenAsOptionItems() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOptionListTitleRes() {
        return optionListTitleRes;
    }

    public void setOptionListTitleRes(int optionListTitleRes) {
        this.optionListTitleRes = optionListTitleRes;
    }

    public String getOptionListTitle() {
        return optionListTitle;
    }

    public void setOptionListTitle(String optionListTitle) {
        this.optionListTitle = optionListTitle;
    }

    public String getLabelValueSelected() {
        return labelValueSelected;
    }

    public void setLabelValueSelected(String labelValueSelected) {
        this.labelValueSelected = labelValueSelected;
    }

    public int getValueInt() {
        return valueInt;
    }

    public void setValueInt(int valueInt) {
        this.valueInt = valueInt;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public List<WorkflowTypeMenu> getOptionItems() {
        return optionItems;
    }

    public void setOptionItems(List<WorkflowTypeMenu> optionItems) {
        this.optionItems = optionItems;
    }
}
