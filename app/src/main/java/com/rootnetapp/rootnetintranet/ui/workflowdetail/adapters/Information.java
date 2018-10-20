package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

public class Information{

    private String title;

    @StringRes
    private int resTitle;

    private String displayValue;

    @StringRes
    private int resDisplayValue;

    private boolean isMultiple;

    private List<Integer> listOfResStrings;

    private Object unformattedValue;

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
}