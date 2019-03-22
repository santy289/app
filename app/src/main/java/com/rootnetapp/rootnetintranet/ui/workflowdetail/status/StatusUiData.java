package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

public class StatusUiData {

    private boolean isOpen;
    private @StringRes int selectedText;
    private @ColorRes int selectedColor;

    protected StatusUiData(boolean isOpen, @StringRes int selectedText, @ColorRes int selectedColor) {
        this.isOpen = isOpen;
        this.selectedText = selectedText;
        this.selectedColor = selectedColor;
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

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }
}
