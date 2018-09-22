package com.rootnetapp.rootnetintranet.models.responses.domain;

import com.squareup.moshi.Json;

public class ClientResponse {

    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "client")
    private Client client;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
