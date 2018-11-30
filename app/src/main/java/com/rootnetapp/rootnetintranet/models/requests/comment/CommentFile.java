package com.rootnetapp.rootnetintranet.models.requests.comment;

import com.squareup.moshi.Json;

import java.io.File;

/**
 * Created by root on 05/04/18.
 */


public class CommentFile {

    @Json(name = "file")
    private String file;
    @Json(name = "type")
    private String type;
    @Json(name = "name")
    private String name;
    @Json(name = "size")
    private int size;

    private File localFile;

    public CommentFile(String file, String type, String name, int size) {
        this.file = file;
        this.type = type;
        this.name = name;
        this.size = size;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public File getLocalFile() {
        return localFile;
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }
}
