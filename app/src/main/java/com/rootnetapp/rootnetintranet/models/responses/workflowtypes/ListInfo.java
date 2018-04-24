package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 23/03/18.
 */

public class ListInfo {

    @Json(name = "id")
    private Integer id;
    @Json(name = "name")
    private String name;
    @Json(name = "in_menu")
    private Boolean inMenu;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "updatedAt")
    private String updatedAt;
    @Json(name = "elements")
    private List<Element> elements = null;

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

    public Boolean getInMenu() {
        return inMenu;
    }

    public void setInMenu(Boolean inMenu) {
        this.inMenu = inMenu;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

}
