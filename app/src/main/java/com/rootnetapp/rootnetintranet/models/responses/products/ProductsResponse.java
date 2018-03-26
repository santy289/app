package com.rootnetapp.rootnetintranet.models.responses.products;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 26/03/18.
 */

public class ProductsResponse {

    @Json(name = "code")
    private Integer code;
    @Json(name = "status")
    private String status;
    @Json(name = "list")
    private List<Product> list = null;

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

    public List<Product> getList() {
        return list;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }

}
