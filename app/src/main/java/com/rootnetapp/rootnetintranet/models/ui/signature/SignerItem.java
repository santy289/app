package com.rootnetapp.rootnetintranet.models.ui.signature;

public class SignerItem {
    private String avatarUrl;
    private String name;
    private boolean isSigned;
    private String userType;
    private String role;
    private String date;

    public SignerItem(String avatarUrl, String name, boolean isSigned, String userType, String role, String date) {
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.isSigned = isSigned;
        this.userType = userType;
        this.role = role;
        this.date = date;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public String getUserType() {
        return userType;
    }

    public String getRole() {
        return role;
    }

    public String getDate() {
        return date;
    }
}
