package com.rootnetapp.rootnetintranet.ui.createworkflow.enums;

import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemViewType.BOOLEAN;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemViewType.CURRENCY;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemViewType.DATE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemViewType.SINGLE_CHOICE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemViewType.TEXT_INPUT;

/**
 * Defines the {@link View} layout for the form item. Every form item should have defined a {@link
 * FormItemViewType}.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
        TEXT_INPUT,
        SINGLE_CHOICE,
        BOOLEAN,
        DATE,
        CURRENCY
})
public @interface FormItemViewType {

    int TEXT_INPUT = 1;
    int SINGLE_CHOICE = 2;
    int BOOLEAN = 3;
    int DATE = 4;
    int CURRENCY = 5;
}
