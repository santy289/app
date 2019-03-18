package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.squareup.moshi.Json;

public class PersonRelated {

    @Json(name = "id")
    private Integer id;
    @Json(name = "name")
    private String name;
    @Json(name = "is_profile_involved")
    private Boolean isProfileInvolved;
    @Json(name = "is_specific_approver")
    private Boolean isSpecificApprover;
    @Json(name = "is_owner")
    private Boolean isOwner;
    @Json(name = "is_approver")
    private Boolean isApprover;

    private String picture;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isProfileInvolved() {
        if (isProfileInvolved == null) return false;

        return isProfileInvolved;
    }

    public void setProfileInvolved(Boolean isProfileInvolved) {
        this.isProfileInvolved = isProfileInvolved;
    }

    public Boolean isSpecificApprover() {
        if (isSpecificApprover == null) return false;

        return isSpecificApprover;
    }

    public void setSpecificApprover(Boolean isSpecificApprover) {
        this.isSpecificApprover = isSpecificApprover;
    }

    public Boolean isOwner() {
        if (isOwner == null) return false;

        return isOwner;
    }

    public void setOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public Boolean isApprover() {
        if (isApprover == null) return false;

        return isApprover;
    }

    public void setApprover(Boolean isApprover) {
        this.isApprover = isApprover;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}