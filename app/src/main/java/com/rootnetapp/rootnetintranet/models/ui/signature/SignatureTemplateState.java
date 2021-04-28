package com.rootnetapp.rootnetintranet.models.ui.signature;

import androidx.annotation.StringRes;

import java.util.ArrayList;

/**
 * This is the state of the template selection region in the Digital Signature tab in Workflow Details.
 */
public class SignatureTemplateState {
    private boolean isTemplateMenuEnable;
    private boolean isTemplateActionEnable;
    private ArrayList<String> templateMenuItems;
    @StringRes
    private int templateActionTitleResId;

    public SignatureTemplateState(boolean isTemplateMenuEnable,
                                  boolean isTemplateActionEnable,
                                  int templateActionTitleResId,
                                  ArrayList<String> templateMenuItems) {
        this.isTemplateMenuEnable = isTemplateMenuEnable;
        this.isTemplateActionEnable = isTemplateActionEnable;
        this.templateMenuItems = templateMenuItems;
        this.templateActionTitleResId = templateActionTitleResId;
    }

    public boolean isTemplateMenuEnable() {
        return isTemplateMenuEnable;
    }

    public boolean isTemplateActionEnable() {
        return isTemplateActionEnable;
    }

    public ArrayList<String> getTemplateMenuItems() {
        return templateMenuItems;
    }

    public int getTemplateActionTitleResId() {
        return templateActionTitleResId;
    }
}
