package com.rootnetapp.rootnetintranet.models.responses.comments;

import com.squareup.moshi.Json;

public class CommentDeleteResponse {

    @Json(name = "code")
    private Integer code;
    @Json(name = "message")
    private Boolean message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean getMessage() {
        return message;
    }

    public void setMessage(Boolean message) {
        this.message = message;
    }

}
