package com.rootnetapp.rootnetintranet.models.workflowlist;

import java.util.ArrayList;
import java.util.List;

public class RightDrawerOptionList {
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
