package com.rootnetapp.rootnetintranet.models.responses.user;

import com.rootnetapp.rootnetintranet.data.local.db.profile.Profile;
import com.squareup.moshi.Json;

import java.util.List;

public class ProfileResponse {
    @Json(name = "status")
    private String status;
    @Json(name = "code")
    private int code;
    @Json(name = "profiles")
    private List<Profile> profiles = null;
    @Json(name = "pager")
    private Pager pager;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }
}
