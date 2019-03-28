package com.rootnetapp.rootnetintranet.models.responses.business;

import com.squareup.moshi.Json;

import java.util.List;

public class BusinessOpportunitiesResponse {

    @Json(name = "code")
    private Integer code;
    @Json(name = "status")
    private String status;
    @Json(name = "list")
    private List<BusinessOpportunity> businessOpportunities = null;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BusinessOpportunity> getList() {
        return businessOpportunities;
    }

    public void setList(List<BusinessOpportunity> list) {
        this.businessOpportunities = list;
    }

}