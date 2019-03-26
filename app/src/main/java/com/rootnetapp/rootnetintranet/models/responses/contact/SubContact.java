package com.rootnetapp.rootnetintranet.models.responses.contact;

import com.squareup.moshi.Json;

public class SubContact {

    @Json(name = "id")
    private Integer id;
    @Json(name = "active")
    private Boolean active;
    @Json(name = "contact_id")
    private Integer contactId;
    @Json(name = "categorized")
    private Boolean categorized;
    @Json(name = "email")
    private String email;
    @Json(name = "profile_full_name")
    private String profileFullName;
    @Json(name = "name")
    private String name;
    @Json(name = "last_name")
    private String lastName;
    @Json(name = "full_name")
    private String fullName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public Boolean getCategorized() {
        return categorized;
    }

    public void setCategorized(Boolean categorized) {
        this.categorized = categorized;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileFullName() {
        return profileFullName;
    }

    public void setProfileFullName(String profileFullName) {
        this.profileFullName = profileFullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}