package com.rootnetapp.rootnetintranet.models.responses.project;

import com.squareup.moshi.Json;

import java.util.ArrayList;

public class ProjectResponse {
    @Json(name = "projects")
    private ArrayList<Project> projects;

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }
}
