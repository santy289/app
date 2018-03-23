package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 23/03/18.
 */

public class Element {

    @Json(name = "id")
    private Integer id;
    @Json(name = "name")
    private String name;
    @Json(name = "listId")
    private Integer listId;
    @Json(name = "slug")
    private String slug;
    @Json(name = "level")
    private Integer level;
    @Json(name = "treeLeft")
    private Integer treeLeft;
    @Json(name = "treeRight")
    private Integer treeRight;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "updatedAt")
    private String updatedAt;
    @Json(name = "values")
    private List<Object> values = null;
    @Json(name = "children")
    private List<Object> children = null;

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

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getTreeLeft() {
        return treeLeft;
    }

    public void setTreeLeft(Integer treeLeft) {
        this.treeLeft = treeLeft;
    }

    public Integer getTreeRight() {
        return treeRight;
    }

    public void setTreeRight(Integer treeRight) {
        this.treeRight = treeRight;
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

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public List<Object> getChildren() {
        return children;
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }

}