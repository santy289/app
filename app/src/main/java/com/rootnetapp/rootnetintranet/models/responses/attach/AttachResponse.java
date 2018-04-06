package com.rootnetapp.rootnetintranet.models.responses.attach;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 05/04/18.
 */

public class AttachResponse {

    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "list")
    private List<UploadedFile> list = null;

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

    public List<UploadedFile> getList() {
        return list;
    }

    public void setList(List<UploadedFile> list) {
        this.list = list;
    }

}
