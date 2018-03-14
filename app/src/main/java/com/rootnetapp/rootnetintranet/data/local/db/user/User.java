package com.rootnetapp.rootnetintranet.data.local.db.user;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 14/03/2018.
 */

@Entity
public class User {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @Json(name = "id")
    private int id;

    @ColumnInfo(name = "userId")
    @Json(name = "user_id")
    private int userId;

    @ColumnInfo(name = "username")
    @Json(name = "username")
    private String username;

    @ColumnInfo(name = "email")
    @Json(name = "email")
    private String email;

    @ColumnInfo(name = "enabled")
    @Json(name = "enabled")
    private boolean enabled;

    @ColumnInfo(name = "fullName")
    @Json(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "phoneNumber")
    @Json(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "picture")
    @Json(name = "picture")
    private String picture;

    @ColumnInfo(name = "locale")
    @Json(name = "locale")
    private String locale;

    /*@Json(name = "enabledProducts")
    private List<String> enabledProducts = null;

    @Json(name = "department")
    private List<Department> department = null;

    @Json(name = "groups")
    private List<Integer> groups = null;

    @Json(name = "roles")
    private Roles roles;*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    /*public List<String> getEnabledProducts() {
        return enabledProducts;
    }

    public void setEnabledProducts(List<String> enabledProducts) {
        this.enabledProducts = enabledProducts;
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

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }*/
}
