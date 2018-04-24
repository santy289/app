package com.rootnetapp.rootnetintranet.models.responses.comments;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 04/04/18.
 */

public class UserInfo {

    @Json(name = "user_id")
    private int userId;
    @Json(name = "full_name")
    private String fullName;
    @Json(name = "picture")
    private String picture;
    @Json(name = "phone_number")
    private String phoneNumber;
    @Json(name = "enabled_products")
    private List<String> enabledProducts = null;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getEnabledProducts() {
        return enabledProducts;
    }

    public void setEnabledProducts(List<String> enabledProducts) {
        this.enabledProducts = enabledProducts;
    }

}