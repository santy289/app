package com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail;

import android.arch.persistence.room.ColumnInfo;

public class ProfileInvolved {
    @ColumnInfo(name = "full_name")
    public String fullName;

    @ColumnInfo(name = "picture")
    public String picture;
}
