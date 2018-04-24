package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

/**
 * Created by root on 23/03/18.
 */

public class Step {

    @Json(name = "id")
    private int id;
    @Json(name = "title")
    private String title;
    @Json(name = "text")
    private String text;
    @Json(name = "order")
    private int order;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
