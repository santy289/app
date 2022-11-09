package com.rootnetapp.rootnetintranet.ui.projectFragment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class ProjectDataResult {
    private int id;
    private String title;
    private int author_id;
    private Date created_at;
    private Date updated_at;
    private boolean status;
    private  String key_code;
    private ArrayList<Team> team;
    private String project_type_key;
    private ProjectType project_type;
    private ArrayList<Meta> metas;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProjectType getProject_type() {
        return this.project_type;
    }

    public void setProject_type(ProjectType project_type) {
        this.project_type = project_type;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthor_id() {
        return this.author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public Date getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public  String getKey_code() {
        return this.key_code;
    }

    public void setKey_code(String key_code) {
        this.key_code = key_code;
    }

    public ArrayList<Team> getTeam() {
        return this.team;
    }

    public void setTeam(ArrayList<Team> team) {
        this.team = team;
    }

    public String getProject_type_key() {
        return this.project_type_key;
    }

    public void setProject_type_key(String project_type_key) {
        this.project_type_key = project_type_key;
    }

    public ArrayList<Meta> getMetas() {
        return metas;
    }

    public void setMetas(ArrayList<Meta> metas) {
        this.metas = metas;
    }
}
