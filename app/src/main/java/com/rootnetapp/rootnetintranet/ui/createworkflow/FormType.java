package com.rootnetapp.rootnetintranet.ui.createworkflow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormType.CREATE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormType.DYNAMIC_FILTERS;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormType.EDIT;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormType.STANDARD_FILTERS;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        CREATE,
        EDIT,
        STANDARD_FILTERS,
        DYNAMIC_FILTERS,
})
public @interface FormType {

    int CREATE = 1;
    int EDIT = 2;
    int STANDARD_FILTERS = 3;
    int DYNAMIC_FILTERS = 4;
}
