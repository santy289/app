package com.rootnetapp.rootnetintranet.ui.createworkflow;

import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemType;
import com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemViewType;

import java.util.ArrayList;

public class FieldData {

    public String label = "";
    public int resLabel;
    public boolean required;
    public boolean isMultipleSelection;
    public int tag;
    public boolean escape;
    public ArrayList<ListFieldItemMeta> list;

    public @FormItemViewType int viewType;
    public @FormItemType int type;

    public FieldData() {
        //todo remove this init
        type = FormItemType.DEFAULT;
    }

    public FieldData(@FormItemViewType int viewType) {
        type = FormItemType.DEFAULT;
        this.viewType = viewType;
    }
}
