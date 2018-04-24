package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

/**
 * Created by root on 03/04/18.
 */

public class Information{

    private String title;
    private String content;

    public Information(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}