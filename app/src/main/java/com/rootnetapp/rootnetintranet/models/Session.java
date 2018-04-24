package com.rootnetapp.rootnetintranet.models;

import com.squareup.moshi.Json;

public class Session {

    @Json(name = "access_token")
    private String accessToken;

    @Json(name = "last_login")
    private String lastLogin;

    @Json(name = "domain")
    private String domain;

	/*@Json(name = "user")
    private User user;*/

    public Session(String accessToken, String lastLogin, String domain) {
        this.accessToken = accessToken;
        this.lastLogin = lastLogin;
        this.domain = domain;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

	/*public void setUser(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}*/

    @Override
    public String toString() {
        return
                "Data{" +
                        "access_token = '" + accessToken + '\'' +
                        ",last_login = '" + lastLogin + '\'' +
                        ",domain = '" + domain + '\'' +
                        "}";
    }
}