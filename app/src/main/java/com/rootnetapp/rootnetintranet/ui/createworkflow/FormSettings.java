package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.content.Context;

import java.util.ArrayList;

public class FormSettings {
    ArrayList<String> names;
    ArrayList<Integer> ids;

    public FormSettings() {
        names = new ArrayList<>();
        ids = new ArrayList<>();
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
}
