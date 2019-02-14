package com.rootnetapp.rootnetintranet.ui.createworkflow;

import java.io.File;

class DownloadedFileUiData {
    private File file;
    private String mimeType;

    public DownloadedFileUiData(File file, String mimeType) {
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
