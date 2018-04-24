package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 15/03/2018.
 */

public class StateCounter {

    @Json(name = "Requested to FR")
    private String requestedToFR;
    @Json(name = "Received from FR")
    private String receivedFromFR;
    @Json(name = "Sample Sent")
    private String sampleSent;
    @Json(name = "Approved")
    private String approved;
    @Json(name = "Estado Inicial")
    private String estadoInicial;

    public String getRequestedToFR() {
        return requestedToFR;
    }

    public void setRequestedToFR(String requestedToFR) {
        this.requestedToFR = requestedToFR;
    }

    public String getReceivedFromFR() {
        return receivedFromFR;
    }

    public void setReceivedFromFR(String receivedFromFR) {
        this.receivedFromFR = receivedFromFR;
    }

    public String getSampleSent() {
        return sampleSent;
    }

    public void setSampleSent(String sampleSent) {
        this.sampleSent = sampleSent;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

}