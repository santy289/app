package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
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
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.role.Role;
import com.rootnetapp.rootnetintranet.models.responses.services.Service;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.Information;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.collection.ArrayMap;
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
    private Moshi moshi;
    private String uploadFileName;
    private String uploadFileExtension;
    private PendingFileUpload pendingFileUpload;
    private FormBuilder formBuilder;
    private List<BaseFormItem> formItems; //new

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

    protected void formatMetaData(WorkflowMetas metaData, BaseFormItem formItem) {
        TypeInfo typeInfo = formItem.getTypeInfo();
        if (typeInfo == null) {
            return;
        }

        format(metaData, typeInfo, formItem);
    }

    public WorkflowMetas formatMetaData(WorkflowMetas metaData, BaseFormItem formItem,
                                        FieldConfig fieldConfig) {
        TypeInfo typeInfo = fieldConfig.getTypeInfo();
        if (typeInfo == null) {
            return metaData;
        }
        //todo check
        /*boolean rememberRealValue = fieldData.isMultipleSelection;
        fieldData.isMultipleSelection = true;*/
        format(metaData, typeInfo, formItem);
//        fieldData.isMultipleSelection = rememberRealValue;
        return metaData;
    }

    private void format(WorkflowMetas metaData, TypeInfo typeInfo, BaseFormItem formItem) {
        String value = metaData.getUnformattedValue();
        if (TextUtils.isEmpty(value)) {
            return;
        }

        switch (typeInfo.getValueType()) {
            case FormSettings.VALUE_BOOLEAN:
                handleBoolean(typeInfo, metaData, value);
                break;
            case FormSettings.VALUE_DATE:
                metaData.setValue(value);
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
                handleSingleSelection((SingleChoiceFormItem) formItem, metaData, value);
                break;
            case FormSettings.VALUE_LIST:
                if (typeInfo.getType().equals(TYPE_SYSTEM_USERS)) {
                    String json = getJsonStringForSystemUserType(value);
                    metaData.setValue(json);
                    break;
                }

                if (typeInfo.getType().equals(TYPE_PRODUCT)) {
                    String json = getProductJson(value, (SingleChoiceFormItem) formItem, metaData);
                    metaData.setValue(json);
                    break;
                }

                if (typeInfo.getType().equals(TYPE_SERVICE)) {
                    // TODO handle service.

                }

                handleSingleSelection((SingleChoiceFormItem) formItem, metaData, value);
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
            case FormSettings.TYPE_LINK:
                metaData.setValue(value);
                break;
            default:
                Log.d(TAG, "format: invalid type. Not Known.");
                metaData.setValue("");
                break;
        }

        // Do not escape this fields
        if (!formItem.isEscaped()
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

    private String getProductJson(String value, SingleChoiceFormItem formItem,
                                  WorkflowMetas workflowMetas) {
        List<Option> list = formItem.getOptions();
        Option item;
        int id = 0;
        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            if (item.getName().equals(value)) {
                id = item.getId();
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

        JsonAdapter<PostCountryCodeAndValue> jsonAdapter = moshi
                .adapter(PostCountryCodeAndValue.class);
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

        JsonAdapter<PostCountryCodeAndValue> jsonAdapter = moshi
                .adapter(PostCountryCodeAndValue.class);
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
            if (value.equals("Sí") || value.equals("Yes") || value.equals("true")) {
                metaData.setValue("true");
            } else {
                metaData.setValue("false");
            }
        }
    }

    private void handleSingleSelection(SingleChoiceFormItem formItem, WorkflowMetas metaData,
                                       String value) {
        List<Option> list = formItem.getOptions();
        if (list == null) {
            return;
        }

        int id;
        //todo handle multiple selection
        /*if (fieldData.isMultipleSelection) {
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
        }*/

        id = findIdByValue(list, value);
        metaData.setValue(String.valueOf(id));
    }

    private int findIdByValue(List<Option> options, String value) {
        Option item;
        for (int j = 0; j < options.size(); j++) {
            item = options.get(j);
            if (value.equals(item.getName())) {
                return item.getId();
            }
        }
        return 0;
    }

    public List<BaseFormItem> getFormItems() {
        if (formItems == null) {
            formItems = new ArrayList<>();
        }
        return formItems;
    }

    public void setFormItems(List<BaseFormItem> formItems) {
        this.formItems = formItems;
    }

    /**
     * Interface for a ViewModel that we help FormSettings in completed an Information object by
     * requesting to a Repository for data in the network. Eventually this function will also
     * continue updating the Workflow information section.
     */
    public interface FormSettingsViewModelDelegate {

        public void findInNetwork(Object value, Information information, FieldConfig fieldConfig);
    }

    public Information formatStringToObject(Meta meta, FieldConfig fieldConfig) {
        if (TextUtils.isEmpty(meta.getValue())) {
            return null;
        }

        Information information = new Information();
        information.setTitle(meta.getWorkflowTypeFieldName());
        TypeInfo typeInfo = fieldConfig.getTypeInfo();

        /*
        if multiple is true then value will be an array and probably ids, so we need to go back to the
        view model and request to an endpoint the name of the ids given in value.
        */
        switch (typeInfo.getValueType()) {
            case FormSettings.VALUE_BOOLEAN:
                if (fieldConfig.getMultiple()) {
                    return null;
                }

                String value = meta.getValue();

                if (Boolean.valueOf(value)) {
                    information.setResDisplayValue(R.string.yes);
                } else {
                    information.setResDisplayValue(R.string.no);
                }
                return information;
            case FormSettings.VALUE_DATE:
                if (fieldConfig.getMultiple()) {
                    return null;
                }

                String date = (String) meta.getDisplayValue(); // now returns 10/25/2018
                date = Utils
                        .getFormattedDate(date, "dd/MM/yyyy", Utils.STANDARD_DATE_DISPLAY_FORMAT);

                information.setDisplayValue(date);
                return information;
            case FormSettings.VALUE_EMAIL:
                if (fieldConfig.getMultiple()) {
                    return null;
                }
                if (!(meta.getDisplayValue() instanceof String)) {
                    information.setDisplayValue("");
                    return information;
                }

                information.setDisplayValue((String) meta.getDisplayValue());
                return information;
            case FormSettings.VALUE_INTEGER:

                if (fieldConfig.getMultiple()) {
                    return null;
                }

                if (typeInfo.getType().equals(TYPE_TEXT)) {
                    if (!(meta.getDisplayValue() instanceof String)) {
                        information.setDisplayValue("");
                        return information;
                    }
                    information.setDisplayValue((String) meta.getDisplayValue());
                    return information;
                }

                if (typeInfo.getType().equals(TYPE_CURRENCY)) {
                    PostCountryCodeAndValue currency;
                    JsonAdapter<PostCountryCodeAndValue> jsonAdapter = moshi
                            .adapter(PostCountryCodeAndValue.class);
                    try {
                        currency = jsonAdapter.fromJson(meta.getValue());
                        information.setDisplayValue(String.valueOf(currency.value));
                        return information;
                    } catch (IOException e) {
                        e.printStackTrace();
                        information.setDisplayValue("");
                        return information;
                    } catch (JsonDataException e) {
                        e.printStackTrace();
                        information.setDisplayValue("");
                        return information;
                    }
                }

                if (typeInfo.getType().equals(TYPE_FILE)) {
                    // TODO handle file.
                    return null;
                }

                return null;
            case FormSettings.VALUE_ENTITY:
                if (typeInfo.getType().equals(TYPE_ROLE)) {
                    String displayValue = getLabelFrom(meta);
                    information.setDisplayValue(displayValue);
                    return information;
                }

                return null;
            case FormSettings.VALUE_LIST:
                if (typeInfo.getType().equals(TYPE_SYSTEM_USERS)) {
                    // TODO implement system user field
//                    if (fieldConfig.getMultiple()) {
//                        // {"id":50,"username":"jhonny Garzon","status":true,"email":"jgarzon600@gmail.com"}
//                    } else {
//
//                    }

                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<PostSystemUser> jsonAdapter = moshi.adapter(PostSystemUser.class);
                    try {
                        PostSystemUser systemUser = jsonAdapter.fromJson(meta.getValue());
                        information.setDisplayValue(systemUser.username);
                        return information;
                    } catch (IOException e) {
                        e.printStackTrace();
                        information.setDisplayValue("");
                        return information;
                    }
                }

                String displayValue = getLabelFrom(meta);
                information.setDisplayValue(displayValue);
                return information;
            case FormSettings.VALUE_STRING:
                // Until now phone type can only be single and not multiple
                if (typeInfo.getType().equals(TYPE_PHONE)) {
                    PostCountryCodeAndValue phone;
                    JsonAdapter<PostCountryCodeAndValue> jsonAdapter = moshi
                            .adapter(PostCountryCodeAndValue.class);
                    try {
                        phone = jsonAdapter.fromJson(meta.getValue());
                        information.setDisplayValue(String.valueOf(phone.value));
                        return information;
                    } catch (IOException e) {
                        e.printStackTrace();
                        information.setDisplayValue("");
                        return information;
                    } catch (JsonDataException e) {
                        e.printStackTrace();
                        information.setDisplayValue("");
                        return information;
                    }
                }

                if (!(meta.getDisplayValue() instanceof String)) {
                    information.setDisplayValue("");
                    return information;
                }

                information.setDisplayValue((String) meta.getDisplayValue());
                return information;
            case FormSettings.VALUE_TEXT:
                if (!(meta.getDisplayValue() instanceof String)) {
                    information.setDisplayValue("");
                    return information;
                }

                information.setDisplayValue((String) meta.getDisplayValue());
                return information;
            default:
                Log.d(TAG, "format: invalid type. Not Known.");
                information.setDisplayValue("");
                return information;
        }
    }

    private String getLabelFrom(Meta meta) {
        ArrayList<String> displayValue;
        try {
            displayValue = (ArrayList<String>) meta.getDisplayValue();
        } catch (ClassCastException e) {
            Log.d(TAG, "formatStringToObject: Value List casting problems");
            e.printStackTrace();
            return "";
        }

        if (displayValue.size() == 0) {
            return "";
        }

        if (displayValue.size() == 1) {
            return displayValue.get(0);
        }

        String label;
        StringBuilder sb = new StringBuilder();
        int size = displayValue.size();
        for (int i = 0; i < size; i++) {
            label = displayValue.get(i);
            sb.append(label);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
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

    public ListField addListToForm(ListItem newList, String customLabel, int customFieldId,
                                   String type) {
        //todo check

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
//        formLists.add(listField);
        return listField;
    }

    public ListField addServiceListToForm(List<Service> incomingList, String customLabel,
                                          int customFieldId, String type) {
        //todo check
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
//        formLists.add(listField);
        return listField;
    }

    public ListField addRolesLisToForm(List<Role> incomingList, String customLabel,
                                       int customFieldId, String type) {
        //todo check
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
//        formLists.add(listField);
        return listField;
    }

    public ListField addProductLisToForm(List<ProductFormList> incomingList, String customLabel,
                                         int customFieldId, String type) {
        //todo check
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
//        formLists.add(listField);
        return listField;
    }

    final static String MACHINE_NAME_TITLE = "wf_title";
    final static String MACHINE_NAME_KEY = "wf_key";
    final static String MACHINE_NAME_DESCRIPTION = "wf_description";
    final static String MACHINE_NAME_START_DATE = "wf_start_date";
    final static String MACHINE_NAME_END_DATE = "wf_end_date";
    final static String MACHINE_NAME_STATUS = "wf_status";
    final static String MACHINE_NAME_CURRENT_STATUS = "wf_current_status";
    final static String MACHINE_NAME_OWNER = "wf_owner";
    final static String MACHINE_NAME_TYPE = "wf_type";
    final static String MACHINE_NAME_REMAINING_TIME = "wf_remaining_time";

    public ArrayList<Integer> idsForBaseFields = new ArrayList<>();

    private ArrayMap<String, Integer> baseMachineNamesAndIds = new ArrayMap<>();

    public String postTitle = "";

    protected ArrayMap<String, Integer> getBaseMachineNamesAndIds() {
        return baseMachineNamesAndIds;
    }

    protected List<BaseFormItem> getFormItemsToPost() {

        List<BaseFormItem> formItemsToPost = new ArrayList<>(getFormItems());
        BaseFormItem formItem;
        int tag;
        String machineName;
        ArrayList<Integer> toRemove = new ArrayList<>();

        //todo improve this algorithm
        for (int i = 0; i < formItemsToPost.size(); i++) {
            formItem = formItemsToPost.get(i);
            tag = formItem.getTag();
            if (tag == CreateWorkflowViewModel.TAG_WORKFLOW_TYPE) {
                toRemove.add(tag);
                continue;
            }
            machineName = formItem.getMachineName();
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
                    machineName.equals(MACHINE_NAME_TYPE) ||
                    machineName.equals(MACHINE_NAME_REMAINING_TIME)) {
                toRemove.add(tag);
            }
        }
        int removeTag;
        int id;
        for (int i = 0; i < toRemove.size(); i++) {
            removeTag = toRemove.get(i);
            for (int j = 0; j < formItemsToPost.size(); j++) {
                id = formItemsToPost.get(j).getTag();
                if (removeTag == id) {
                    formItemsToPost.remove(j);
                    break;
                }
            }
        }

        return formItemsToPost;
    }

    protected void clearFormItems() {
        pendingFileUpload = null;

        List<BaseFormItem> formItems = getFormItems();

        if (formItems.size() <= 1) {
            return;
        }
        formItems.subList(1, formItems.size()).clear();

        if (formItems.size() <= 1) {
            return;
        }

        fields.subList(1, fields.size()).clear();
    }

    /**
     * @return the first item that is not valid, index-based. Null if all of the items are valid.
     */
    protected BaseFormItem findFirstInvalidItem() {
        for (BaseFormItem item : getFormItems()) {
            if (!item.isValid()) return item;
        }

        return null;
    }

    /**
     * Goes through the items list in order to find a specific form item based on its tag.
     *
     * @param tag the tag to find
     *
     * @return the form item matching the tag. Null if none was found.
     */
    protected BaseFormItem findItem(int tag) {
        for (BaseFormItem item : getFormItems()) {
            if (item.getTag() == tag) return item;
        }

        return null;
    }

    protected BaseFormItem findItem(String machineName) {
        for (int i = 0; i < fields.size(); i++) {
            FormFieldsByWorkflowType field = fields.get(i);
            if (field.getFieldConfigObject().getMachineName().equals(machineName)) {
                return findItem(field.getId());
            }
        }

        return null;
    }
}
