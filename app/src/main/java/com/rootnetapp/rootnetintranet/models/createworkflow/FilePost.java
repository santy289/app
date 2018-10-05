package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

public class FilePost {
    @Json(name = "file")
    private FilePostDetail file;

    public FilePostDetail getFile() {
        return file;
    }

    public void setFile(FilePostDetail file) {
        this.file = file;
    }
}
