package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import androidx.annotation.StringRes;

public class StatusUiData {

    private boolean isOpen;
    private @StringRes int selectedText;

    protected StatusUiData(boolean isOpen, @StringRes int selectedText) {
        this.isOpen = isOpen;
        this.selectedText = selectedText;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(int selectedText) {
        this.selectedText = selectedText;
    }
}
