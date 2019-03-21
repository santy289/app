package com.rootnetapp.rootnetintranet.models.responses.user;

import com.squareup.moshi.Json;

import java.util.List;
import java.util.Map;


public class LoggedUser {

    @Json(name = "id")
    private Integer id;
    @Json(name = "user_id")
    private Integer userId;
    @Json(name = "username")
    private String username;
    @Json(name = "email")
    private String email;
    @Json(name = "enabled")
    private Boolean enabled;
    @Json(name = "full_name")
    private String fullName;
    @Json(name = "firstname")
    private String firstname;
    @Json(name = "lastname")
    private String lastname;
    @Json(name = "phone_number")
    private String phoneNumber;
    @Json(name = "picture")
    private String picture;
    @Json(name = "locale")
    private String locale;
    @Json(name = "enabledProducts")
    private List<String> enabledProducts = null;
    @Json(name = "defaultLanding")
    private String defaultLanding;
    @Json(name = "person_id")
    private Integer personId;
    @Json(name = "person_slug")
    private String personSlug;
    @Json(name = "community_roles")
    private List<Integer> communityRoles = null;
    @Json(name = "department")
    private List<Department> department = null;
    @Json(name = "groups")
    private List<Integer> groups = null;
    @Json(name = "new_permissions")
    private Map<String, Object> permissions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<String> getEnabledProducts() {
        return enabledProducts;
    }

    public void setEnabledProducts(List<String> enabledProducts) {
        this.enabledProducts = enabledProducts;
    }

    public String getDefaultLanding() {
        return defaultLanding;
    }

    public void setDefaultLanding(String defaultLanding) {
        this.defaultLanding = defaultLanding;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPersonSlug() {
        return personSlug;
    }

    public void setPersonSlug(String personSlug) {
        this.personSlug = personSlug;
    }

    public List<Integer> getCommunityRoles() {
        return communityRoles;
    }

    public void setCommunityRoles(List<Integer> communityRoles) {
        this.communityRoles = communityRoles;
    }

    public List<Department> getDepartment() {
        return department;
    }

    public void setDepartment(List<Department> department) {
        this.department = department;
    }

    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }

    public Map<String, Object> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Object> permissions) {
        this.permissions = permissions;
    }
}
