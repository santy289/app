package com.rootnetapp.rootnetintranet.models.responses.workflows.presets;

import com.squareup.moshi.Json;

public class PresetFile {

    @Json(name = "Id")
    private int id;
    @Json(name = "FosUserId")
    private int fosUserId;
    @Json(name = "Path")
    private String path;
    @Json(name = "Status")
    private boolean status;
    @Json(name = "FileName")
    private String fileName;
    @Json(name = "FileMime")
    private String fileMime;
    @Json(name = "FileSize")
    private int fileSize;
    @Json(name = "Entity")
    private String entity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFosUserId() {
        return fosUserId;
    }

    public void setFosUserId(int fosUserId) {
        this.fosUserId = fosUserId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMime() {
        return fileMime;
    }

    public void setFileMime(String fileMime) {
        this.fileMime = fileMime;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

}