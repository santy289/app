package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import java.util.Map;

public class WebViewData {
    private Map<String, String> headers;
    private String url;

    public WebViewData(Map<String, String> headers, String url) {
        this.headers = headers;
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
