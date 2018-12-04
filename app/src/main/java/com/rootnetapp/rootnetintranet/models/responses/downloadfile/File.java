package com.rootnetapp.rootnetintranet.models.responses.downloadfile;

import com.squareup.moshi.Json;

public class File {

    @Json(name = "scheme")
    private String scheme;
    @Json(name = "path")
    private String path;
    @Json(name = "filename")
    private String filename;
    @Json(name = "mime")
    private String mime;
    @Json(name = "size")
    private int size;
    @Json(name = "changed")
    private int changed;
    @Json(name = "modified")
    private int modified;
    @Json(name = "created")
    private int created;
    @Json(name = "content")
    private String content;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getChanged() {
        return changed;
    }

    public void setChanged(int changed) {
        this.changed = changed;
    }

    public int getModified() {
        return modified;
    }

    public void setModified(int modified) {
        this.modified = modified;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}