package com.rootnetapp.rootnetintranet.models.responses.resourcing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        BookingType.PROJECT,
        BookingType.ROLE,
})
public @interface BookingType {

    String PROJECT = "Project";
    String ROLE = "Role";
}