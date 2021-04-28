package com.rootnetapp.rootnetintranet.models.responses.signature;

import com.squareup.moshi.Json;

public class SignatureFileResponseContent {
    @Json(name = "file_name")
    private String fileName;

    private String content;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
