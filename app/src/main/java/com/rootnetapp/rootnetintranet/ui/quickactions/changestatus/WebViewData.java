package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;

import java.util.Map;

public class WebViewData {

    private Map<String, String> headers;
    private String url;
    private ClientResponse clientResponse;

    public WebViewData(Map<String, String> headers, String url, ClientResponse clientResponse) {
        this.headers = headers;
        this.url = url;
        this.clientResponse = clientResponse;
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

    public ClientResponse getClientResponse() {
        return clientResponse;
    }

    public void setClientResponse(
            ClientResponse clientResponse) {
        this.clientResponse = clientResponse;
    }
}
