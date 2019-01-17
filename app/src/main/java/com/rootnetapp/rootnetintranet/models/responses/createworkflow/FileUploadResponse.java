package com.rootnetapp.rootnetintranet.models.responses.createworkflow;

import com.squareup.moshi.Json;

public class FileUploadResponse {

    public static final String FILE_ENTITY = "file";

    @Json(name = "fileId")
    private int fileId;

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
}
