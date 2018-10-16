package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.models.createworkflow.FileMetaData;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.models.createworkflow.PendingFileUpload;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostCountryCodeAndValue;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostSystemUser;
import com.rootnetapp.rootnetintranet.models.createworkflow.ProductFormList;
import com.rootnetapp.rootnetintranet.models.createworkflow.ProductJsonValue;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.role.Role;
import com.rootnetapp.rootnetintranet.models.responses.services.Service;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.w3c.dom.Text;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.riddhimanadib.formmaster.FormBuilder;

public class FormSettings {
    private ArrayList<String> names;
    private ArrayList<Integer> ids;
    private int indexWorkflowTypeSelected;
    private int workflowTypeIdSelected;
    private String title;
    private String description;
    private long createdTimestamp;
    private ArrayList<FormCreateProfile> profiles;
    private List<FormFieldsByWorkflowType> fields; // Full info of all fields.
    private ArrayList<ListField> formLists;
    private ArrayList<FieldData> fieldItems;
    private Moshi moshi;
    private String uploadFileName;
    private String uploadFileExtension;
    private PendingFileUpload pendingFileUpload;
    private FormBuilder formBuilder;

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_TEXT_AREA = "textarea";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_CHECKBOX = "checkbox";
    public static final String TYPE_SYSTEM_USERS = "system_users";
    public static final String TYPE_PROJECT = "project"; // empty response
    public static final String TYPE_ROLE = "role";
    public static final String TYPE_BIRTH_DATE = "birth_date";
    public static final String TYPE_ACCOUNT = "account"; // which account
    public static final String TYPE_LINK = "link";
    public static final String TYPE_CURRENCY = "currency";
    public static final String TYPE_PHONE = "phone";
    public static final String TYPE_SERVICE = "service";
    public static final String TYPE_PRODUCT = "product";
    public static final String TYPE_LIST = "list";
    public static final String TYPE_FILE = "file";
    public static final String VALUE_EMAIL = "email";
    public static final String VALUE_INTEGER = "integer";
    public static final String VALUE_BOOLEAN = "boolean";
    public static final String VALUE_STRING = "string";
    public static final String VALUE_TEXT = "text";
    public static final String VALUE_LIST = "list";
    public static final String VALUE_DATE = "date";
    public static final String VALUE_ENTITY = "entity";
    public static final String VALUE_COORD = "coords";

    public static final int FIELD_CODE_ID = -999;
    public static final int FIELD_CURRENCY_ID = -998;

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
        moshi = new Moshi.Builder().build();
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

    public String getUploadFileName() {
        return uploadFileName;
    }

    public FormBuilder getFormBuilder() {
        return formBuilder;
    }

    public void setFormBuilder(FormBuilder formBuilder) {
        this.formBuilder = formBuilder;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String getUploadFileExtension() {
        return uploadFileExtension;
    }

    public void setUploadFileExtension(String uploadFileExtension) {
        this.uploadFileExtension = uploadFileExtension;
    }

    public PendingFileUpload getPendingFileUpload() {
        return pendingFileUpload;
    }

    public void setPendingFileUpload(PendingFileUpload pendingFileUpload) {
        this.pendingFileUpload = pendingFileUpload;
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

    public int countryCode;
    public boolean hasValidCountryCode() {
        if (countryCode > 0) {
            return true;
        }
        return false;
    }

    public int countryCurrency;
    public boolean hasValidCountryCurrency() {
        if (countryCurrency > 0) {
            return true;
        }
        return false;
    }

    public void setCountryCode(String fieldValue, FieldData fieldData) {
        if (TextUtils.isEmpty(fieldValue)) {
            return;
        }

        List<ListFieldItemMeta> codeList = fieldData.list;
        ListFieldItemMeta codeMeta;
        for (int i = 0; i < codeList.size(); i++) {
            codeMeta = codeList.get(i);
            if (!codeMeta.name.equals(fieldValue)) {
                continue;
            }
            countryCode = codeMeta.id;
        }
     }

    public void setCurrencyType(String fieldValue, FieldData fieldData) {
        if (TextUtils.isEmpty(fieldValue)) {
            return;
        }

        List<ListFieldItemMeta> codeList = fieldData.list;
        ListFieldItemMeta codeMeta;
        for (int i = 0; i < codeList.size(); i++) {
            codeMeta = codeList.get(i);
            if (!codeMeta.name.equals(fieldValue)) {
                continue;
            }
            countryCurrency = codeMeta.id;
        }
    }

    public FormCreateProfile getProfileBy(String userName) {
        FormCreateProfile profile;
        for (int i = 0; i < profiles.size(); i++) {
            profile = profiles.get(i);
            if (profile.username.equals(userName)) {
                return profile;
            }
        }
        return null;
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

    public WorkflowMetas formatMetaData(WorkflowMetas metaData, FieldData fieldData, FieldConfig fieldConfig) {
       TypeInfo typeInfo = fieldConfig.getTypeInfo();
        if (typeInfo == null) {
            return metaData;
        }
        boolean rememberRealValue = fieldData.isMultipleSelection;
        fieldData.isMultipleSelection = true;
        format(metaData, typeInfo, fieldData);
        fieldData.isMultipleSelection = rememberRealValue;
        return metaData;
    }



    private void format(WorkflowMetas metaData, TypeInfo typeInfo, FieldData fieldData) {
        String value = metaData.getUnformattedValue();
        if (TextUtils.isEmpty(value)) {
            return;
        }


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
                            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
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
                    break;
                }
                if (typeInfo.getType().equals(TYPE_CURRENCY)) {
                    String json = getJsonForCurrencyType(value);
                    metaData.setValue(json);
                    break;
                }

                if (typeInfo.getType().equals(TYPE_FILE)) {
                    String json = getFileMetaJson();
                    metaData.setValue(json);
                    break;
                }

                metaData.setValue("");
                break;
            case FormSettings.VALUE_ENTITY:
                handleList(fieldData, metaData, value);
                break;
            case FormSettings.VALUE_LIST:
                if (typeInfo.getType().equals(TYPE_SYSTEM_USERS)) {
                    String json = getJsonStringForSystemUserType(value);
                    metaData.setValue(json);
                    break;
                }
                if (typeInfo.getType().equals(TYPE_PRODUCT)) {
                    String json = getProductJson(value, fieldData, metaData);
                    metaData.setValue(json);
                    break;
                }
                handleList(fieldData, metaData, value);
                break;
            case FormSettings.VALUE_STRING:
                if (typeInfo.getType().equals(TYPE_PHONE)) {
                    String json = getJsonForPhoneType(value);
                    metaData.setValue(json);
                    break;
                }
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

        // Do not escape this fields
        if (!fieldData.escape
                || typeInfo.getType().equals(TYPE_SYSTEM_USERS)
                || typeInfo.getType().equals(TYPE_PHONE)
                || typeInfo.getType().equals(TYPE_CURRENCY)
                || typeInfo.getType().equals(TYPE_PRODUCT)) {
            return;
        }

        // Escape these fields.
        String metaValue = metaData.getValue();
        if (TextUtils.isEmpty(metaValue) || metaValue.equals("0") || metaValue.equals("[]")) {
            return;
        }
        Gson gson = new Gson();
        metaData.setValue(gson.toJson(metaValue));
    }

    private String getProductJson(String value, FieldData fieldData, WorkflowMetas workflowMetas) {
        ArrayList<ListFieldItemMeta> list = fieldData.list;
        ListFieldItemMeta item;
        int id = 0;
        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            if (item.name.equals(value)) {
               id = item.id;
               break;
            }
        }

        if (id == 0) {
            return "";
        }

        ProductJsonValue productJsonValue = new ProductJsonValue();
        productJsonValue.setValue(String.valueOf(id));
        productJsonValue.setWorkflowTypeFieldId(workflowMetas.getWorkflowTypeFieldId());

        JsonAdapter<ProductJsonValue> jsonAdapter = moshi.adapter(ProductJsonValue.class);
        String jsonString = jsonAdapter.toJson(productJsonValue);

        return jsonString;

    }

    private String getFileMetaJson() {
        String name = pendingFileUpload.fileName;
        int id = pendingFileUpload.fileId;
        if (id == 0 || TextUtils.isEmpty(name)) {
            return "";
        }

        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.name = name;
        fileMetaData.value = pendingFileUpload.fileId;
        JsonAdapter<FileMetaData> jsonAdapter = moshi.adapter(FileMetaData.class);
        String jsonString = jsonAdapter.toJson(fileMetaData);
        return jsonString;
    }

    private String getJsonForCurrencyType(String currencyNumber) {
        if (TextUtils.isEmpty(currencyNumber) || !hasValidCountryCurrency()) {
            return "";
        }

        PostCountryCodeAndValue postCountryCodeAndValue = new PostCountryCodeAndValue();
        postCountryCodeAndValue.countryId = countryCurrency;
        postCountryCodeAndValue.value = Integer.valueOf(currencyNumber);

        JsonAdapter<PostCountryCodeAndValue> jsonAdapter = moshi.adapter(PostCountryCodeAndValue.class);
        String jsonString = jsonAdapter.toJson(postCountryCodeAndValue);
        return jsonString;
    }

    private String getJsonForPhoneType(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || !hasValidCountryCode()) {
            return "";
        }

        PostCountryCodeAndValue postCountryCodeAndValue = new PostCountryCodeAndValue();
        postCountryCodeAndValue.countryId = countryCode;
        postCountryCodeAndValue.value = Integer.valueOf(phoneNumber);

        JsonAdapter<PostCountryCodeAndValue> jsonAdapter = moshi.adapter(PostCountryCodeAndValue.class);
        String jsonString = jsonAdapter.toJson(postCountryCodeAndValue);
        return jsonString;
    }

    private String getJsonStringForSystemUserType(String username) {
        FormCreateProfile profile = getProfileBy(username);
        if (profile == null) {
            return "";
        }

        PostSystemUser postSystemUser = new PostSystemUser();
        postSystemUser.id = profile.getId();
        postSystemUser.username = profile.getUsername();
        postSystemUser.email = profile.getEmail();

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<PostSystemUser> jsonAdapter = moshi.adapter(PostSystemUser.class);
        String jsonString = jsonAdapter.toJson(postSystemUser);
        return jsonString;
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

    public TypeInfo findFieldDataById(int id) {
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

    public ListField addProductLisToForm(List<ProductFormList> incomingList, String customLabel, int customFieldId, String type) {
        ListField listField = new ListField();
        listField.customFieldId = customFieldId;
        listField.listType = type;
        listField.customLabel = customLabel;
        ProductFormList newItem;
        ArrayList<ListFieldItemMeta> tempList = new ArrayList<>();
        for (int i = 0; i < incomingList.size(); i++) {
            newItem = incomingList.get(i);
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

    final static String MACHINE_NAME_TITLE = "wf_title";
    final static String MACHINE_NAME_KEY = "wf_key";
    final static String MACHINE_NAME_DESCRIPTION = "wf_description";
    final static String MACHINE_NAME_START_DATE = "wf_start_date";
    final static String MACHINE_NAME_END_DATE = "wf_end_date";
    final static String MACHINE_NAME_STATUS = "wf_status";
    final static String MACHINE_NAME_CURRENT_STATUS = "wf_current_status";
    final static String MACHINE_NAME_OWNER = "wf_owner";
    final static String MACHIEN_NAME_TYPE = "wf_type";
    final static String MACHINE_NAME_REMAINING_TIME = "wf_remaining_time";

    public ArrayList<Integer> idsForBaseFields = new ArrayList<>();

    private ArrayMap<String, Integer> baseMachineNamesAndIds = new ArrayMap<>();

    public String postTitle = "";

    public ArrayMap<String, Integer> getBaseMachineNamesAndIds() {
        return baseMachineNamesAndIds;
    }

    public ArrayList<FieldData> getFieldItemsForPost() {
        ArrayList<FieldData> postFieldData = (ArrayList<FieldData>) fieldItems.clone();
        FieldData fieldData;
        int tag;
        String machineName;
        ArrayList<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < postFieldData.size(); i++) {
            fieldData = postFieldData.get(i);
            tag = fieldData.tag;
            if (tag == CreateWorkflowViewModel.TAG_WORKFLOW_TYPE) {
                toRemove.add(tag);
                continue;
            }
            machineName = findMachineNameBy(tag);
            if (TextUtils.isEmpty(machineName)) {
                continue;
            }
            if (machineName.equals(MACHINE_NAME_TITLE)) {
                baseMachineNamesAndIds.put(MACHINE_NAME_TITLE, tag);
                toRemove.add(tag);
                continue;
            }
            if (machineName.equals(MACHINE_NAME_DESCRIPTION)) {
                baseMachineNamesAndIds.put(MACHINE_NAME_DESCRIPTION, tag);
                toRemove.add(tag);
                continue;
            }
            if (machineName.equals(MACHINE_NAME_START_DATE)) {
                baseMachineNamesAndIds.put(MACHINE_NAME_START_DATE, tag);
                toRemove.add(tag);
                continue;
            }
            if (machineName.equals(MACHINE_NAME_KEY) ||
                    machineName.equals(MACHINE_NAME_END_DATE) ||
                    machineName.equals(MACHINE_NAME_STATUS) ||
                    machineName.equals(MACHINE_NAME_CURRENT_STATUS) ||
                    machineName.equals(MACHINE_NAME_OWNER) ||
                    machineName.equals(MACHIEN_NAME_TYPE) ||
                    machineName.equals(MACHINE_NAME_REMAINING_TIME)) {
                toRemove.add(tag);
            }
        }
        int removeTag;
        int id;
        for (int i = 0; i < toRemove.size(); i++) {
            removeTag = toRemove.get(i);
            for (int j = 0; j < postFieldData.size(); j++) {
               id = postFieldData.get(j).tag;
               if (removeTag == id) {
                   postFieldData.remove(j);
                   break;
               }
            }
        }

        return postFieldData;

    }

    private String findMachineNameBy(int id) {
        FormFieldsByWorkflowType field;
        for (int i = 0; i < fields.size(); i++) {
            field = fields.get(i);
            if (field.getId() == id) {
                return field.getFieldConfigObject().getMachineName();
            }
        }
        return "";
    }

    public void setFieldItems(ArrayList<FieldData> fieldItems) {
        this.fieldItems = fieldItems;
    }

    public void addFieldDataItem(FieldData fieldData) {
        fieldItems.add(fieldData);
    }

    public void clearFormFieldData() {
        pendingFileUpload = null;
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