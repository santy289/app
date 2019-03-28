package com.rootnetapp.rootnetintranet.models.responses.project;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class ProjectResponse {
    @Json(name = "list")
    private List<Project> projects;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }
}
