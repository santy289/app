package com.rootnetapp.rootnetintranet.models.workflowlist;

import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.WorkflowTypeSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;
// TODO extends ListField
public class RightDrawerOptionList extends ListField {
    private int id;
    private String optionListTitle;
    private int optionListTitleRes;
    private String labelValueSelected;
    private int valueInt;
    private String valueString;
    private List<WorkflowTypeMenu> optionItems;

    public RightDrawerOptionList() {
        id = 0;
        optionListTitle = "";
        optionListTitleRes = 0;
        labelValueSelected = "";
        valueInt = 0;
        valueString = "";
        optionItems = new ArrayList<>();
    }

    public void updateValuesWith(ListField listField) {
        this.children = listField.children;
        this.customFieldId = listField.customFieldId;
        this.id = this.customFieldId;
        this.customLabel = listField.customLabel;
        this.optionListTitle = listField.customLabel;
        this.isMultipleSelection = listField.isMultipleSelection;
        updateOptionItemsList(this.children);
    }

    public void updateOptionItemsList(ArrayList<ListFieldItemMeta> children) {
        ListFieldItemMeta child;
        WorkflowTypeMenu menu;
        for (int i = 0; i < children.size(); i++) {
            child = children.get(i);
            menu = new WorkflowTypeMenu();
            menu.setId(child.id);
            menu.setLabel(child.name);
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
