package com.rootnetapp.rootnetintranet.ui.quickactions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        QuickAction.EDIT_WORKFLOW,
        QuickAction.APPROVE_WORKFLOW,
        QuickAction.CHANGE_STATUS,
        QuickAction.COMMENT,
        QuickAction.DIGITAL_SIGNATURE,
})
public @interface QuickAction {
    int EDIT_WORKFLOW = 1;
    int APPROVE_WORKFLOW = 2;
    int CHANGE_STATUS = 3;
    int COMMENT = 4;
    int DIGITAL_SIGNATURE = 5;
}