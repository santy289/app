package com.rootnetapp.rootnetintranet.ui.workflowdetail.files;

import java.io.File;

class FileUiData {

    private File file;
    private String mimeType;

    public FileUiData(File file, String mimeType) {
        this.file = file;
        this.mimeType = mimeType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
