package com.rootnetapp.rootnetintranet.models.createworkflow;

import java.util.ArrayList;

public class ListField {
    public int id;
    public int listId; // used for searching this list
    public String listName;
    public String customLabel;
    public boolean isMultipleSelection;
    public ArrayList<ListFieldItemMeta> children;
    public int associatedWorkflowTypeId;

}
