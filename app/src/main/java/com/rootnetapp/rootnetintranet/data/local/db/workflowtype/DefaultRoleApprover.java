package com.rootnetapp.rootnetintranet.data.local.db.workflowtype;

import com.squareup.moshi.Json;

public class DefaultRoleApprover {

    @Json(name = "role_id")
    private Integer roleId;
    @Json(name = "profile_id")
    private Integer profileId;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }
}