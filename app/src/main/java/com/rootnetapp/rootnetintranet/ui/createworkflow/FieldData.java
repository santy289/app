package com.rootnetapp.rootnetintranet.ui.createworkflow;

import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;

import java.util.ArrayList;

public class FieldData {
    public String label = "";
    public int resLabel;
    public boolean required;
    public boolean isMultipleSelection;
    public int tag;
    public boolean escape;
    public ArrayList<ListFieldItemMeta> list;
}
