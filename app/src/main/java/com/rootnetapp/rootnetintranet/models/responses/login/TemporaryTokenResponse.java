package com.rootnetapp.rootnetintranet.models.responses.login;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 10/03/2018.
 */

public class TemporaryTokenResponse {

    @Json(name = "temporary_session_token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
