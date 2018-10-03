package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

public class CategoryListResponse {
    @Json(name = "categoryList")
    private int categoryList;

    public int getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(int categoryList) {
        this.categoryList = categoryList;
    }
}
