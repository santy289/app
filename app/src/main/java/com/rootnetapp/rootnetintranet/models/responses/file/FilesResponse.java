package com.rootnetapp.rootnetintranet.models.responses.file;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 04/04/18.
 */

public class FilesResponse {

    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "list")
    private List<DocumentsFile> list = null;

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

    public List<DocumentsFile> getList() {
        return list;
    }

    public void setList(List<DocumentsFile> list) {
        this.list = list;
    }

}