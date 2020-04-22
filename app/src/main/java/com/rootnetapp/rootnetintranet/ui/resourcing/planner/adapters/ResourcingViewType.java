package com.rootnetapp.rootnetintranet.ui.resourcing.planner.adapters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

import static com.rootnetapp.rootnetintranet.ui.resourcing.planner.adapters.ResourcingViewType.CONTENT;
import static com.rootnetapp.rootnetintranet.ui.resourcing.planner.adapters.ResourcingViewType.HEADER;

/**
 * Defines the possible timeline actions.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
        HEADER,
        CONTENT,
})
public @interface ResourcingViewType {
    int HEADER = 1;
    int CONTENT = 2;
}