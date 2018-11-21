package com.rootnetapp.rootnetintranet.ui.createworkflow.enums;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemType.DEFAULT;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemType.EMAIL;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemType.PHONE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemType.TEXT;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemType.TEXT_AREA;

/**
 * Appliable to every {@link FormItemViewType} that might have different params, such as {@link
 * FormItemViewType#TEXT_INPUT}.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
        DEFAULT,
        TEXT,
        TEXT_AREA,
        PHONE,
        EMAIL
})
public @interface FormItemType {

    int DEFAULT = 0;
    int TEXT = 1;
    int TEXT_AREA = 2;
    int PHONE = 3;
    int EMAIL = 4;
}
