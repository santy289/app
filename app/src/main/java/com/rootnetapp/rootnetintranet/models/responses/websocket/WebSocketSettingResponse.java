package com.rootnetapp.rootnetintranet.models.responses.websocket;

import com.squareup.moshi.Json;

public class WebSocketSettingResponse {
    @Json(name = "status")
    private String status;

    @Json(name = "data")
    private DataResponse data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataResponse getData() {
        return data;
    }

    public void setData(DataResponse data) {
        this.data = data;
    }
}
