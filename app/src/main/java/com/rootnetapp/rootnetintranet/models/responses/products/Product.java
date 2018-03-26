package com.rootnetapp.rootnetintranet.models.responses.products;

import com.squareup.moshi.Json;

/**
 * Created by root on 26/03/18.
 */

public class Product {

    @Json(name = "id")
    private Integer id;
    @Json(name = "name")
    private String name;
    @Json(name = "status")
    private Boolean status;
    @Json(name = "price")
    private double price;
    @Json(name = "product_category_id")
    private Integer productCategoryId;
    @Json(name = "category")
    private String category;
    @Json(name = "type")
    private Integer type;
    @Json(name = "metadatas")
    private java.util.List<Metadata> metadatas = null;
    @Json(name = "descripcion1")
    private String descripcion1;
    @Json(name = "sku2")
    private String sku2;
    @Json(name = "subdesc3")
    private String subdesc3;
    @Json(name = "test-mta4")
    private String testMta4;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public java.util.List<Metadata> getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(java.util.List<Metadata> metadatas) {
        this.metadatas = metadatas;
    }

    public String getDescripcion1() {
        return descripcion1;
    }

    public void setDescripcion1(String descripcion1) {
        this.descripcion1 = descripcion1;
    }

    public String getSku2() {
        return sku2;
    }

    public void setSku2(String sku2) {
        this.sku2 = sku2;
    }

    public String getSubdesc3() {
        return subdesc3;
    }

    public void setSubdesc3(String subdesc3) {
        this.subdesc3 = subdesc3;
    }

    public String getTestMta4() {
        return testMta4;
    }

    public void setTestMta4(String testMta4) {
        this.testMta4 = testMta4;
    }
}
