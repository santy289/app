package com.rootnetapp.rootnetintranet.data.local.db.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rootnetapp.rootnetintranet.models.responses.user.Department;
import com.squareup.moshi.Json;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

class UserConverters {
    @TypeConverter
    public static List<Department> stringToDepartments(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Department>>() {}.getType();
        List<Department> departments = gson.fromJson(json, type);
        return departments;
    }

    @TypeConverter
    public static String departmentsToString(List<Department> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Department>>() {}.getType();
        String json = gson.toJson(list, type);
        return json;
    }

    @TypeConverter
    public static List<String> stringToList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> strings = gson.fromJson(json, type);
        return strings;
    }

    @TypeConverter
    public static String listToString(List<String> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        String json = gson.toJson(list, type);
        return json;
    }

    @TypeConverter
    public static List<Integer> stringToInteger(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>() {}.getType();
        List<Integer> integers = gson.fromJson(json, type);
        return integers;
    }

    @TypeConverter
    public static String integerToString(List<Integer> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>() {}.getType();
        String json = gson.toJson(list, type);
        return json;
    }

}

@Entity
@TypeConverters(UserConverters.class)
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

    @ColumnInfo(name = "enabledProducts")
    @Json(name = "enabledProducts")
    private List<String> enabledProducts = null;

    @ColumnInfo(name = "department")
    @Json(name = "department")
    private List<Department> department = null;

    @ColumnInfo(name = "groups")
    @Json(name = "groups")
    private List<Integer> groups = null;

    @Ignore
    @Json(name = "new_permissions")
    private Object permissions;

    /*
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

    public List<String> getEnabledProducts() {
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
    }/*

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }*/

    public Object getPermissions() {
        return permissions;
    }

    public void setPermissions(Object permissions) {
        this.permissions = permissions;
    }
}
