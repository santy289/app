package com.rootnetapp.rootnetintranet.models.responses.downloadfile;


import com.squareup.moshi.Json;

public class DownloadFileResponse {

    @Json(name = "file")
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}