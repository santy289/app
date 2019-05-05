package com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters;

import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.SelectedLocation;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class Information {

    private String title;
    private @StringRes int resTitle;
    private String displayValue;
    private @StringRes int resDisplayValue;
    private boolean isMultiple;
    private List<Integer> listOfResStrings;
    private Object unformattedValue;
    private @InformationAdapter.ViewType int viewType;
    /**
     * Only used by {@link InformationAdapter.ViewType#GEOLOCATION}.
     */
    private @Nullable SelectedLocation selectedLocation;

    public Information() {
        this.title = "";
        this.displayValue = "";
        this.unformattedValue = null;
        this.isMultiple = false;
        this.listOfResStrings = new ArrayList<>();
    }

    public Information(String title, String displayValue) {
        this.title = title;
        this.displayValue = displayValue;
        this.unformattedValue = null;
        this.isMultiple = false;
        this.listOfResStrings = new ArrayList<>();
    }

    public Information(@StringRes int resTitle, String displayValue) {
        this.resTitle = resTitle;
        this.displayValue = displayValue;
        this.unformattedValue = null;
        this.isMultiple = false;
        this.listOfResStrings = new ArrayList<>();
    }

    public int getResTitle() {
        return resTitle;
    }

    public void setResTitle(int resTitle) {
        this.resTitle = resTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public Object getUnformattedValue() {
        return unformattedValue;
    }

    public void setUnformattedValue(Object unformattedValue) {
        this.unformattedValue = unformattedValue;
    }

    public int getResDisplayValue() {
        return resDisplayValue;
    }

    public void setResDisplayValue(int resDisplayValue) {
        this.resDisplayValue = resDisplayValue;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public List<Integer> getListOfResStrings() {
        return listOfResStrings;
    }

    public void setListOfResStrings(List<Integer> listOfResStrings) {
        this.listOfResStrings = listOfResStrings;
    }

    public void addResStringToResList(@StringRes int resString) {
        listOfResStrings.add(resString);
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Nullable
    public SelectedLocation getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(
            @Nullable SelectedLocation selectedLocation) {
        this.selectedLocation = selectedLocation;
    }
}