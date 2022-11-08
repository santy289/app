package com.rootnetapp.rootnetintranet.ui.projectFragment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class ProjectDataResult {
    private int id;
    @SerializedName("title")
    private static String title;
    private int author_id;
    @SerializedName("created_at")
    private static Date created_at;
    @SerializedName("updated_at")
    private static Date updated_at;
    private boolean status;
    @SerializedName("key_code")
    private static String key_code;
    private ArrayList<Team> team;
    private String project_type_key;
    private static ProjectType project_type;
    private ArrayList<Meta> metas;

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        ProjectDataResult.title = title;
    }

    public static ProjectType getProject_type() {
        return project_type;
    }

    public static void setProject_type(ProjectType project_type) {
        ProjectDataResult.project_type = project_type;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public static Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        ProjectDataResult.created_at = created_at;
    }

    public static Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        ProjectDataResult.updated_at = updated_at;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static String getKey_code() {
        return key_code;
    }

    public void setKey_code(String key_code) {
        ProjectDataResult.key_code = key_code;
    }

    public ArrayList<Team> getTeam() {
        return team;
    }

    public void setTeam(ArrayList<Team> team) {
        this.team = team;
    }

    public String getProject_type_key() {
        return project_type_key;
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
