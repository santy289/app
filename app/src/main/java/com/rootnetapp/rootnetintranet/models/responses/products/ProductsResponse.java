package com.rootnetapp.rootnetintranet.models.responses.products;

import com.rootnetapp.rootnetintranet.models.createworkflow.ProductFormList;
import com.squareup.moshi.Json;

import java.util.List;

public class ProductsResponse {

    @Json(name = "code")
    private Integer code;
    @Json(name = "status")
    private String status;
    @Json(name = "list")
    private List<ProductFormList> list = null;

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

    public List<ProductFormList> getList() {
        return list;
    }

    public void setList(List<ProductFormList> list) {
        this.list = list;
    }

}
