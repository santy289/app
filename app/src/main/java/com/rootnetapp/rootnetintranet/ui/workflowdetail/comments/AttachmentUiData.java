package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import java.io.File;

class AttachmentUiData {

    private File file;
    private String mimeType;

    public AttachmentUiData(File file, String mimeType) {
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
