package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.Field;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.role.Role;
import com.rootnetapp.rootnetintranet.models.responses.services.Service;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FormSettings {
    private ArrayList<String> names;
    private ArrayList<Integer> ids;
    private int indexWorkflowTypeSelected;
    private int workflowTypeIdSelected;
    private String title;
    private String description;
    private long createdTimestamp;
    private ArrayList<FormCreateProfile> profiles;
    private List<FormFieldsByWorkflowType> fields;
    private ArrayList<ListField> formLists;
    private ArrayList<FieldData> fieldItems;

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_TEXT_AREA = "textarea";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_CHECKBOX = "checkbox";
    public static final String TYPE_SYSTEM_USERS = "system_users";
    public static final String TYPE_PROJECT = "project";
    public static final String TYPE_ROLE = "role";
    public static final String TYPE_BIRTH_DATE = "birth_date";
    public static final String TYPE_ACCOUNT = "account";
    public static final String TYPE_LINK = "link";
    public static final String TYPE_CURRENCY = "currency";
    public static final String TYPE_PHONE = "phone";
    public static final String TYPE_SERVICE = "service";
    public static final String TYPE_PRODUCT = "product";
    public static final String TYPE_LIST = "list";
    public static final String VALUE_EMAIL = "email";
    public static final String VALUE_INTEGER = "integer";
    public static final String VALUE_BOOLEAN = "boolean";
    public static final String VALUE_STRING = "string";
    public static final String VALUE_TEXT = "text";
    public static final String VALUE_LIST = "list";
    public static final String VALUE_DATE = "date";
    public static final String VALUE_ENTITY = "entity";

    public static final String TAG = "FormSettings";

    public FormSettings() {
        names = new ArrayList<>();
        ids = new ArrayList<>();
        profiles = new ArrayList<>();
        fields = new ArrayList<>();
        formLists = new ArrayList<>();
        fieldItems = new ArrayList<>();
        indexWorkflowTypeSelected = 0;
        title = "";
        description = "";
        createdTimestamp = 0;
    }

    public int getWorkflowTypeIdSelected() {
        return workflowTypeIdSelected;
    }

    public void setWorkflowTypeIdSelected(int workflowTypeIdSelected) {
        this.workflowTypeIdSelected = workflowTypeIdSelected;
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

    public ArrayList<Integer> getProfileIds() {
        ArrayList<Integer> idList = new ArrayList<>();
        for (int i = 0; i < profiles.size(); i++) {
            idList.add(profiles.get(i).getId());
        }
        return idList;
    }

    public int getIndexWorkflowTypeSelected() {
        return indexWorkflowTypeSelected;
    }

    public void setIndexWorkflowTypeSelected(int indexWorkflowTypeSelected) {
        this.indexWorkflowTypeSelected = indexWorkflowTypeSelected;
    }

    public int findIdByTypeName(String name) {
        String typeName;
        int id = 0;
        for (int i = 0; i < names.size(); i++) {
            typeName = names.get(i);
            if (!typeName.equals(name)) {
                continue;
            }
            id = ids.get(i);
            break;
        }
        return id;
    }

    public WorkflowMetas formatMetaData(WorkflowMetas metaData, FieldData fieldData) {
        int fieldId = metaData.getWorkflowTypeFieldId();
        TypeInfo typeInfo = findFieldDataById(fieldId);
        if (typeInfo == null) {
            return metaData;
        }
        format(metaData, typeInfo, fieldData);
        return metaData;
    }

    private void format(WorkflowMetas metaData, TypeInfo typeInfo, FieldData fieldData) {
        String value = metaData.getUnformattedValue();
        switch (typeInfo.getValueType()) {
            case FormSettings.VALUE_BOOLEAN:
                handleBoolean(typeInfo, metaData, value);
                break;
            case FormSettings.VALUE_DATE:
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault());
                String metaDateString = "";
                try {
                    Date convertedDate = dateFormat.parse(value);
                    SimpleDateFormat serverFormat = new SimpleDateFormat(
                            "yyyy-dd-MM'T'HH:mm:ss.SSSZ",
                            Locale.getDefault());
                    metaDateString = serverFormat.format(convertedDate);
                } catch (ParseException e) {
                    Log.d(TAG, "StringDateToTimestamp: e = " + e.getMessage());
                }
                metaData.setValue(metaDateString);
                break;
            case FormSettings.VALUE_EMAIL:
                metaData.setValue(value);
                break;
            case FormSettings.VALUE_INTEGER:
                if (typeInfo.getType().equals(TYPE_TEXT)) {
                    metaData.setValue(value);
                }
                metaData.setValue("");
                // TODO if currency do something.
                // TODO if it is a file.
                break;
            case FormSettings.VALUE_ENTITY:
                handleList(fieldData, metaData, value);
                break;
            case FormSettings.VALUE_LIST:
                handleList(fieldData, metaData, value);
                break;
            case FormSettings.VALUE_STRING:
                metaData.setValue(value);
                break;
            case FormSettings.VALUE_TEXT:
                metaData.setValue(value);
                break;
            default:
                Log.d(TAG, "format: invalid type. Not Known.");
                metaData.setValue("");
                break;
        }
    }

    private void handleBoolean(TypeInfo typeInfo, WorkflowMetas metaData, String value) {
        if (typeInfo.getType().equals(TYPE_CHECKBOX)) {
            if (value.equals("Sí") || value.equals("Yes")) {
                metaData.setValue("true");
            } else {
                metaData.setValue("false");
            }
        }
    }

    private void handleList(FieldData fieldData, WorkflowMetas metaData, String value) {
        ArrayList<ListFieldItemMeta> list = fieldData.list;
        if (list == null) {
            return;
        }

        int id;
        if (fieldData.isMultipleSelection) {
            ArrayList<String> matches = new ArrayList<>();
            String selection;
            List<String> values = Arrays.asList(value.split("\\s*,\\s*"));
            for (int i = 0; i < values.size(); i++) {
                selection = values.get(i);
                id = findIdByValue(list, selection);
                if (id == 0) {
                    continue;
                }
                matches.add(String.valueOf(id));
            }
            String formattedValue = "";
            String match;
            int size = matches.size();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (int i = 0; i < size; i++) {
                match = matches.get(i);
                stringBuilder.append(match);
                if (i < size - 1) {
                    stringBuilder.append(",");
                }
            }
            formattedValue = stringBuilder.append("]").toString();
            metaData.setValue(formattedValue);
        } else {
            id = findIdByValue(list, value);
            metaData.setValue(String.valueOf(id));
        }
    }

    private int findIdByValue(ArrayList<ListFieldItemMeta> list, String value) {
        ListFieldItemMeta item;
        for (int j = 0; j < list.size(); j++) {
            item = list.get(j);
            if (value.equals(item.name)) {
                return item.id;
            }
        }
        return 0;
    }

    private TypeInfo findFieldDataById(int id) {
        FormFieldsByWorkflowType field;
        TypeInfo typeInfo;
        for (int i = 0; i < fields.size(); i++) {
            field = fields.get(i);
            if (id != field.id) {
                continue;
            }
            typeInfo = field.getFieldConfigObject().getTypeInfo();
            return typeInfo;
        }
        return null;
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

    public List<FormFieldsByWorkflowType> getFields() {
        return fields;
    }

    public void setFields(List<FormFieldsByWorkflowType> fields) {
        this.fields = fields;
    }

    public ArrayList<ListField> getFormLists() {
        return formLists;
    }

    public void setFormLists(ArrayList<ListField> formLists) {
        this.formLists = formLists;
    }

    public ListField addListToForm(ListItem newList, String customLabel, int customFieldId, String type) {
        ListField listField = new ListField();
        listField.id = newList.getId();
        listField.listId = newList.getListId();
        listField.listName = newList.getName();
        listField.customLabel = customLabel;
        listField.customFieldId = customFieldId;
        listField.listType = type;

        ArrayList<ListFieldItemMeta> tempList = new ArrayList<>();
        List<ListItem> incomingList = newList.getChildren();
        for (int i = 0; i < incomingList.size(); i++) {
            ListItem newItem = incomingList.get(i);
            ListFieldItemMeta item = new ListFieldItemMeta(
                    newItem.getId(),
                    newItem.getName(),
                    newItem.getListId()
            );
            tempList.add(item);
        }

        listField.children = tempList;
        formLists.add(listField);
        return listField;
    }

    public ListField addServiceListToForm(List<Service> incomingList, String customLabel, int customFieldId, String type) {
        ListField listField = new ListField();
        listField.customFieldId = customFieldId;
        listField.listType = type;
        listField.customLabel = customLabel;

        ArrayList<ListFieldItemMeta> tempList = new ArrayList<>();
        for (int i = 0; i < incomingList.size(); i++) {
            Service newItem = incomingList.get(i);
            ListFieldItemMeta item = new ListFieldItemMeta(
                    newItem.getId(),
                    newItem.getName()
            );
            tempList.add(item);
        }
        listField.children = tempList;
        formLists.add(listField);
        return listField;
    }

    public ListField addRolesLisToForm(List<Role> incomingList, String customLabel, int customFieldId, String type) {
        ListField listField = new ListField();
        listField.customFieldId = customFieldId;
        listField.listType = type;
        listField.customLabel = customLabel;
        ArrayList<ListFieldItemMeta> tempList = new ArrayList<>();
        for (int i = 0; i < incomingList.size(); i++) {
            Role newItem = incomingList.get(i);
            ListFieldItemMeta item = new ListFieldItemMeta(
                    newItem.getId(),
                    newItem.getName()
            );
            tempList.add(item);
        }
        listField.children = tempList;
        formLists.add(listField);
        return listField;
    }

    public ArrayList<FieldData> getFieldItems() {
        return fieldItems;
    }

    public void setFieldItems(ArrayList<FieldData> fieldItems) {
        this.fieldItems = fieldItems;
    }

    public void addFieldDataItem(FieldData fieldData) {
        fieldItems.add(fieldData);
    }

    public void clearFormFieldData() {
        if(1 >= fieldItems.size()){
            return;
        }
        fieldItems.subList(1, fieldItems.size()).clear();

        if (1 >= fields.size()) {
            return;
        }
        fields.subList(1, fields.size()).clear();
    }
}
