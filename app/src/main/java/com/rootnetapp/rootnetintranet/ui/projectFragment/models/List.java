package com.rootnetapp.rootnetintranet.ui.projectFragment.models;

import java.util.ArrayList;
import java.util.Date;

public class List {
    public int id;
    public String title;
    public int author_id;
    public Date created_at;
    public Date updated_at;
    public boolean status;
    public String key_code;
    public ArrayList<Team> team;
    public String project_type_key;
    public ProjectType project_type;
    public ArrayList<Meta> metas;
}
