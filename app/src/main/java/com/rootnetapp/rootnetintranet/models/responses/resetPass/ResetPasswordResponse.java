package com.rootnetapp.rootnetintranet.models.responses.resetPass;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 12/03/2018.
 */

public class ResetPasswordResponse {

    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "message")
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
