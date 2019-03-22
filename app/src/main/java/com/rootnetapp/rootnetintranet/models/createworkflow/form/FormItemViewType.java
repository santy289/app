package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.BOOLEAN;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.CURRENCY;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.DATE;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.DISPLAY;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.DOUBLE_MULTIPLE_CHOICE;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.FILE;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.GEOLOCATION;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.INTENT;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.MULTIPLE_CHOICE;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.PHONE;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.SINGLE_CHOICE;
import static com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType.TEXT_INPUT;

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
        CURRENCY,
        MULTIPLE_CHOICE,
        PHONE,
        FILE,
        INTENT,
        DOUBLE_MULTIPLE_CHOICE,
        GEOLOCATION,
        DISPLAY
})
public @interface FormItemViewType {

    int TEXT_INPUT = 1;
    int SINGLE_CHOICE = 2;
    int BOOLEAN = 3;
    int DATE = 4;
    int CURRENCY = 5;
    int MULTIPLE_CHOICE = 6;
    int PHONE = 7;
    int FILE = 8;
    int INTENT = 9;
    int DOUBLE_MULTIPLE_CHOICE = 10;
    int GEOLOCATION = 11;
    int DISPLAY = 12;
}
