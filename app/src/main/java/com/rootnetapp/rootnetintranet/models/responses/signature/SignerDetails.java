package com.rootnetapp.rootnetintranet.models.responses.signature;

import com.squareup.moshi.Json;

public class SignerDetails {
    private int id;
    private String firstName;
    private String lastName;
    private boolean isExternalUser;
    private String email;
    private String role;
    @Json(name="full_name")
    private String fullName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isExternalUser() {
        return isExternalUser;
    }

    public void setExternalUser(boolean externalUser) {
        isExternalUser = externalUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
