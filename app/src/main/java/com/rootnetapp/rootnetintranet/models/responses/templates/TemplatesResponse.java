package com.rootnetapp.rootnetintranet.models.responses.templates;

import com.squareup.moshi.Json;

/**
 * Created by root on 04/04/18.
 */

public class TemplatesResponse {

    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "templates")
    private Templates templates;

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

    public Templates getTemplates() {
        return templates;
    }

    public void setTemplates(Templates templates) {
        this.templates = templates;
    }

}
