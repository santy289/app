package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

public class StatusUiData {

    private @DrawableRes int selectedIcon;
    private @ColorRes int selectedColor;

    protected StatusUiData(@DrawableRes int selectedIcon, @ColorRes int selectedColor) {
        this.selectedIcon = selectedIcon;
        this.selectedColor = selectedColor;
    }

    public int getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(int selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }
}
