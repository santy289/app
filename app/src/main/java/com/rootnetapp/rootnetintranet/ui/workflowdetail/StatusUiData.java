package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import java.util.List;

public class StatusUiData {
    private List<Integer> stringResList;
    private List<Integer> colorResList;
    private int selectedIndex;

    protected StatusUiData(List<Integer> stringResList, List<Integer> colorResList){
        this.stringResList = stringResList;
        this.colorResList = colorResList;
    }

    public List<Integer> getStringResList() {
        return stringResList;
    }

    public void setStringResList(List<Integer> stringResList) {
        this.stringResList = stringResList;
    }

    public List<Integer> getColorResList() {
        return colorResList;
    }

    public void setColorResList(List<Integer> colorResList) {
        this.colorResList = colorResList;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
