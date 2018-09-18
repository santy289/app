package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.content.Context;

import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;

import java.util.ArrayList;

public class FormSettings {
    private ArrayList<String> names;
    private ArrayList<Integer> ids;
    private int indexWorkflowTypeSelected;
    private String title;
    private String description;
    private long createdTimestamp;
    private ArrayList<FormCreateProfile> profiles;

    public FormSettings() {
        names = new ArrayList<>();
        ids = new ArrayList<>();
        profiles = new ArrayList<>();
        indexWorkflowTypeSelected = 0;
        title = "";
        description = "";
        createdTimestamp = 0;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setName(String name) {
        names.add(name);
    }

    public ArrayList<Integer> getIds() {
        return ids;
    }

    public void setId(Integer id) {
        ids.add(id);
    }

    public ArrayList<FormCreateProfile> getProfiles() {
        return profiles;
    }

    public void setProfile(FormCreateProfile profile) {
        profiles.add(profile);
    }

    public ArrayList<String> getProfileNames() {
        ArrayList<String> fullNames = new ArrayList<>();
        for (int i = 0; i < profiles.size(); i++) {
            fullNames.add(profiles.get(i).fullName);
        }
        return fullNames;
    }

    public int getIndexWorkflowTypeSelected() {
        return indexWorkflowTypeSelected;
    }

    public void setIndexWorkflowTypeSelected(int indexWorkflowTypeSelected) {
        this.indexWorkflowTypeSelected = indexWorkflowTypeSelected;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
