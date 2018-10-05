package com.rootnetapp.rootnetintranet.models.responses.services;

import com.squareup.moshi.Json;

public class Service {

    @Json(name = "id")
    private int id;
    @Json(name = "name")
    private String name;
    @Json(name = "status")
    private boolean status;
    @Json(name = "price")
    private int price;
    @Json(name = "product_category_id")
    private int productCategoryId;
    @Json(name = "category")
    private String category;
    @Json(name = "type")
    private int type;
    @Json(name = "metadatas")
    private java.util.List<Object> metadatas = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public java.util.List<Object> getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(java.util.List<Object> metadatas) {
        this.metadatas = metadatas;
    }

}