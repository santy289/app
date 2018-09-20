package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.rootnetapp.rootnetintranet.models.responses.services.Service;

import java.util.ArrayList;

public class ListField {
    // id generated when creating that list
    public int id;
    // ListId is the id that identifies which list is in a field for the form.
    public int listId;
    // Id for the field created for our custom workflow. id used for post.
    public int customFieldId;
    public String listName;
    public String customLabel;
    public boolean isMultipleSelection;
    public ArrayList<ListFieldItemMeta> children;
    public int associatedWorkflowTypeId;
    public String listType;

}
