package com.rootnetapp.rootnetintranet.ui.workflowdetail.flowchart;

import java.util.List;
import java.util.Locale;

class WebViewData {

    private String url;
    private List<String> localStorageScripts;

    WebViewData(String url, List<String> localStorageScripts) {
        this.url = url;
        this.localStorageScripts = localStorageScripts;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getLocalStorageScripts() {
        return localStorageScripts;
    }

    public void setLocalStorageScripts(List<String> localStorageScripts) {
        this.localStorageScripts = localStorageScripts;
    }

    public String getReloadScript() {
        return String.format(Locale.US, "window.location = '%s';", getUrl());
    }
}
