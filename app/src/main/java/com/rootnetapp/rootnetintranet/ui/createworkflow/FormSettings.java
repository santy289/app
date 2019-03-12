package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.models.createworkflow.BaseEntityJsonValue;
import com.rootnetapp.rootnetintranet.models.createworkflow.FileMetaData;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostCountryCodeAndValue;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostCurrency;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostPhone;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostSystemUser;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.CurrencyFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.FileFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.GeolocationFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.MultipleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.PhoneFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.geolocation.GeolocationMetaData;
import com.rootnetapp.rootnetintranet.models.createworkflow.geolocation.Value;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.Information;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.collection.ArrayMap;

public class FormSettings {

    private final ArrayList<String> names;
    private final ArrayList<Integer> ids;
    private int workflowTypeIdSelected;
    private String title;
    private String description;
    private long createdTimestamp;
    private final ArrayList<FormCreateProfile> profiles;
    private List<FormFieldsByWorkflowType> fields; // Full info of all fields.
    private final Moshi moshi;
    private List<BaseFormItem> formItems; //new
    private List<BaseFormItem> peopleInvolvedFormItems; //new
    private List<BaseFormItem> roleApproversFormItems; //these are also included in peopleInvolvedFormItems

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
    public static final String TYPE_GEOLOCATION = "geolocation";
    public static final String VALUE_EMAIL = "email";
    public static final String VALUE_INTEGER = "integer";
    public static final String VALUE_BOOLEAN = "boolean";
    public static final String VALUE_STRING = "string";
    public static final String VALUE_TEXT = "text";
    public static final String VALUE_LIST = "list";
    public static final String VALUE_DATE = "date";
    public static final String VALUE_ENTITY = "entity";
    public static final String VALUE_COORDS = "coords";

    private static final String TAG = "FormSettings";

    public FormSettings() {
        names = new ArrayList<>();
        ids = new ArrayList<>();
        profiles = new ArrayList<>();
        fields = new ArrayList<>();
        title = "";
        description = "";
        createdTimestamp = 0;
        moshi = new Moshi.Builder().build();
    }

    protected int getWorkflowTypeIdSelected() {
        return workflowTypeIdSelected;
    }

    protected void setWorkflowTypeIdSelected(int workflowTypeIdSelected) {
        this.workflowTypeIdSelected = workflowTypeIdSelected;
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

    protected FormCreateProfile getProfileBy(String userName) {
        FormCreateProfile profile;
        for (int i = 0; i < profiles.size(); i++) {
            profile = profiles.get(i);
            if (profile.username.equals(userName)) {
                return profile;
            }
        }
        return null;
    }

    protected int findIdByTypeName(String name) {
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

    private void format(WorkflowMetas metaData, TypeInfo typeInfo, BaseFormItem formItem) {
        String value = metaData.getUnformattedValue();
        //we allow the FileFormItem even though the value is null because of the editing mode, when the user tries to delete a file.
        if (TextUtils.isEmpty(value) && !(formItem instanceof FileFormItem)) {
            return;
        }

        boolean isMultiple = formItem instanceof MultipleChoiceFormItem;

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

                if (typeInfo.getType()
                        .equals(TYPE_CURRENCY) && formItem instanceof CurrencyFormItem) {
                    String json = getJsonForCurrencyType((CurrencyFormItem) formItem);
                    metaData.setValue(json);
                    break;
                }

                if (typeInfo.getType().equals(TYPE_FILE) && formItem instanceof FileFormItem) {
                    String json = getFileMetaJson((FileFormItem) formItem);
                    metaData.setValue(json);
                    break;
                }

                metaData.setValue("");
                break;
            case FormSettings.VALUE_ENTITY:
                handleSingleSelection((SingleChoiceFormItem) formItem, metaData);
                break;
            case FormSettings.VALUE_LIST:
                if (typeInfo.getType().equals(TYPE_SYSTEM_USERS)) {
                    String json = isMultiple
                            ? getJsonStringForSystemUserTypeList((MultipleChoiceFormItem) formItem)
                            : getJsonStringForSystemUserType((SingleChoiceFormItem) formItem);
                    metaData.setValue(json);
                    break;
                }

                if (typeInfo.getType().equals(TYPE_PRODUCT)) {
                    String json = isMultiple
                            ? getEntityListJson((MultipleChoiceFormItem) formItem, metaData)
                            : getProductJson((SingleChoiceFormItem) formItem, metaData);
                    metaData.setValue(json);
                    break;
                }

                if (isMultiple) {
                    handleMultipleSelection((MultipleChoiceFormItem) formItem, metaData);
                } else {
                    handleSingleSelection((SingleChoiceFormItem) formItem, metaData);
                }
                break;
            case FormSettings.VALUE_STRING:
                if (typeInfo.getType().equals(TYPE_PHONE) && formItem instanceof PhoneFormItem) {
                    String json = getJsonForPhoneType((PhoneFormItem) formItem);
                    metaData.setValue(json);
                    break;
                }

                metaData.setValue(value);
                break;
            case FormSettings.VALUE_TEXT:
            case FormSettings.TYPE_LINK:
                metaData.setValue(value);
                break;
            case FormSettings.VALUE_COORDS:
                if (formItem instanceof GeolocationFormItem) {
                    String json = getGeolocationMetaJson((GeolocationFormItem) formItem);
                    metaData.setValue(json);
                }
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
                || typeInfo.getType().equals(TYPE_PRODUCT)
                || typeInfo.getType().equals(TYPE_GEOLOCATION)
                || typeInfo.getType().equals(TYPE_FILE)) {
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

    private String getProductJson(SingleChoiceFormItem formItem,
                                  WorkflowMetas workflowMetas) {
        Option value = formItem.getValue();
        if (value == null) return "";

        int id = value.getId();
        if (id == 0) return "";

        BaseEntityJsonValue baseEntityJsonValue = new BaseEntityJsonValue();
        baseEntityJsonValue.setValue(String.valueOf(id));
        baseEntityJsonValue.setWorkflowTypeFieldId(workflowMetas.getWorkflowTypeFieldId());

        JsonAdapter<BaseEntityJsonValue> jsonAdapter = moshi.adapter(BaseEntityJsonValue.class);

        return jsonAdapter.toJson(baseEntityJsonValue);
    }

    /**
     * Generates the JSON object in String format for the base entities {@link
     * MultipleChoiceFormItem} that will be sent to the server.
     *
     * @param formItem item to serialize
     *
     * @return JSON string
     */
    private String getEntityListJson(MultipleChoiceFormItem formItem,
                                     WorkflowMetas workflowMetas) {
        List<Option> list = formItem.getOptions();
        if (list == null) {
            return "";
        }

        List<BaseEntityJsonValue> baseEntityJsonValueList = new ArrayList<>();
        for (int i = 0; i < formItem.getValues().size(); i++) {
            Option value = (Option) formItem.getValues().get(i);

            BaseEntityJsonValue baseEntityJsonValue = new BaseEntityJsonValue();
            baseEntityJsonValue.setValue(String.valueOf(value.getId()));
            baseEntityJsonValue.setWorkflowTypeFieldId(workflowMetas.getWorkflowTypeFieldId());

            baseEntityJsonValueList.add(baseEntityJsonValue);
        }

        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, BaseEntityJsonValue.class);
        JsonAdapter<List<BaseEntityJsonValue>> jsonAdapter = moshi.adapter(type);
        return jsonAdapter.toJson(baseEntityJsonValueList);
    }

    private String getFileMetaJson(FileFormItem formItem) {
        String name = formItem.getFileName();
        int id = formItem.getFileId();
        if (id == 0 || TextUtils.isEmpty(name)) {
            return "\"\"";
        }

        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.name = name;
        fileMetaData.value = id;
        JsonAdapter<FileMetaData> jsonAdapter = moshi.adapter(FileMetaData.class);
        return jsonAdapter.toJson(fileMetaData);
    }

    private String getGeolocationMetaJson(GeolocationFormItem formItem) {
        String name = formItem.getName();
        if (TextUtils.isEmpty(name) || formItem.getValue() == null) {
            return "\"\"";
        }

        GeolocationMetaData geolocationMetaData = new GeolocationMetaData();

        Value value = new Value();
        value.setAddress(formItem.getName());

        List<Double> latLng = new ArrayList<>();
        latLng.add(formItem.getValue().latitude);
        latLng.add(formItem.getValue().longitude);
        value.setLatLng(latLng);

        geolocationMetaData.setValue(value);

        JsonAdapter<GeolocationMetaData> jsonAdapter = moshi.adapter(GeolocationMetaData.class);
        return jsonAdapter.toJson(geolocationMetaData);
    }

    private String getJsonForCurrencyType(CurrencyFormItem formItem) {
        Double currencyValue = formItem.getValue();
        if (currencyValue == null || currencyValue == 0) return "";

        Option selectedOption = formItem.getSelectedOption();
        if (selectedOption == null) return "";

        int currencyCode = selectedOption.getId();

        PostCurrency postCurrency = new PostCurrency();
        postCurrency.countryId = currencyCode;
        postCurrency.value = currencyValue;

        JsonAdapter<PostCurrency> jsonAdapter = moshi.adapter(PostCurrency.class);
        return jsonAdapter.toJson(postCurrency);
    }

    private String getJsonForPhoneType(PhoneFormItem formItem) {
        String phoneValue = formItem.getValue();
        if (TextUtils.isEmpty(phoneValue)) return "";

        Option selectedOption = formItem.getSelectedOption();
        if (selectedOption == null) return "";

        int countryCode = selectedOption.getId();

        PostPhone postPhone = new PostPhone();
        postPhone.countryId = countryCode;
        postPhone.value = phoneValue;

        JsonAdapter<PostPhone> jsonAdapter = moshi.adapter(PostPhone.class);
        return jsonAdapter.toJson(postPhone);
    }

    private String getJsonStringForSystemUserType(SingleChoiceFormItem formItem) {
        Option value = formItem.getValue();
        if (value == null) return "";

        String username = value.getName();
        FormCreateProfile profile = getProfileBy(username);
        if (profile == null) return "";

        PostSystemUser postSystemUser = new PostSystemUser();
        postSystemUser.id = profile.getId();
        postSystemUser.username = profile.getUsername();
        postSystemUser.email = profile.getEmail();

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<PostSystemUser> jsonAdapter = moshi.adapter(PostSystemUser.class);
        return jsonAdapter.toJson(postSystemUser);
    }

    /**
     * Generates the JSON object in String format for the System Users {@link
     * MultipleChoiceFormItem} that will be sent to the server.
     *
     * @param formItem item to serialize
     *
     * @return JSON string
     */
    private String getJsonStringForSystemUserTypeList(MultipleChoiceFormItem formItem) {
        List<Option> list = formItem.getOptions();
        if (list == null) {
            return "";
        }

        List<PostSystemUser> postSystemUserList = new ArrayList<>();
        for (int i = 0; i < formItem.getValues().size(); i++) {
            Option value = (Option) formItem.getValues().get(i);

            FormCreateProfile profile = getProfileBy(value.getName());
            if (profile == null) {
                continue;
            }

            PostSystemUser postSystemUser = new PostSystemUser();
            postSystemUser.id = profile.getId();
            postSystemUser.username = profile.getUsername();
            postSystemUser.email = profile.getEmail();

            postSystemUserList.add(postSystemUser);
        }

        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, PostSystemUser.class);
        JsonAdapter<List<PostSystemUser>> jsonAdapter = moshi.adapter(type);
        return jsonAdapter.toJson(postSystemUserList);
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

    private void handleSingleSelection(SingleChoiceFormItem formItem, WorkflowMetas metaData) {
        Option value = formItem.getValue();
        if (value == null) return;

        int id = value.getId();
        if (id == 0) return;

        metaData.setValue(String.valueOf(id));
    }

    /**
     * Formats and creates the meta data value for the {@link MultipleChoiceFormItem}s.
     *
     * @param formItem item to format
     * @param metaData resulting meta data
     */
    private void handleMultipleSelection(MultipleChoiceFormItem formItem, WorkflowMetas metaData) {
        List<Option> list = formItem.getOptions();
        if (list == null) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < formItem.getValues().size(); i++) {
            Option value = (Option) formItem.getValues().get(i);
            int id = value.getId();
            if (id == 0) {
                continue;
            }

            stringBuilder.append(String.valueOf(id));
            if (i < formItem.getValues().size() - 1) {
                stringBuilder.append(",");
            }
        }
        String formattedValue = stringBuilder.append("]").toString();
        metaData.setValue(formattedValue);
    }

    /**
     * Parses the multiple selection raw values and return a list of selected values.
     *
     * @param rawValue raw value
     */
    public List<String> parseMultipleSelectionRawValue(String rawValue) {
        if (rawValue == null || rawValue.isEmpty() || !rawValue.contains("[")
                || !rawValue.contains("]")) {
            return null;
        }

        rawValue = rawValue.replace("[", "").replace("]", "");
        String[] split = rawValue.split(",");

        return Arrays.asList(split);
    }

    protected List<BaseFormItem> getFormItems() {
        if (formItems == null) {
            formItems = new ArrayList<>();
        }
        return formItems;
    }

    protected void setFormItems(List<BaseFormItem> formItems) {
        this.formItems = formItems;
    }

    protected List<BaseFormItem> getPeopleInvolvedFormItems() {
        if (peopleInvolvedFormItems == null) {
            peopleInvolvedFormItems = new ArrayList<>();
        }
        return peopleInvolvedFormItems;
    }

    protected void setPeopleInvolvedFormItems(List<BaseFormItem> formItems) {
        this.peopleInvolvedFormItems = formItems;
    }

    protected List<BaseFormItem> getRoleApproversFormItems() {
        if (roleApproversFormItems == null) {
            roleApproversFormItems = new ArrayList<>();
        }
        return roleApproversFormItems;
    }

    protected void setRoleApproversFormItems(List<BaseFormItem> formItems) {
        this.roleApproversFormItems = formItems;
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
                    JsonAdapter<FileMetaData> jsonAdapter = moshi.adapter(FileMetaData.class);
                    try {
                        FileMetaData fileMetaData = jsonAdapter.fromJson(meta.getValue());
                        information.setDisplayValue(fileMetaData.name);
                        return information;
                    } catch (IOException | JsonDataException e) {
                        e.printStackTrace();
                        information.setDisplayValue("");
                        return information;
                    }
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

            case FormSettings.VALUE_COORDS:
                JsonAdapter<GeolocationMetaData> jsonAdapter = moshi
                        .adapter(GeolocationMetaData.class);
                try {
                    GeolocationMetaData geolocationMetaData = jsonAdapter.fromJson(meta.getValue());
                    if (geolocationMetaData.getValue() == null) {
                        information.setDisplayValue("");
                    } else {
                        information.setDisplayValue(geolocationMetaData.getValue().getAddress());
                    }
                    return information;
                } catch (IOException | JsonDataException e) {
                    e.printStackTrace();
                    information.setDisplayValue("");
                    return information;
                }
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

    private final ArrayMap<String, Integer> baseMachineNamesAndIds = new ArrayMap<>();

    public String postTitle = "";

    protected ArrayMap<String, Integer> getBaseMachineNamesAndIds() {
        return baseMachineNamesAndIds;
    }

    protected List<BaseFormItem> getFormItemsToPost() {
        //todo check people involved
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
        getPeopleInvolvedFormItems().clear();

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
        //todo check people involved
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

        for (BaseFormItem item : getPeopleInvolvedFormItems()) {
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

    protected Option findOption(List<Option> options, String stringValue) {
        for (Option option : options) {
            if (stringValue.equals(option.getName())) return option;
        }
        return null;
    }

    protected Option findOption(List<Option> options, int id) {
        for (Option option : options) {
            if (id == option.getId()) return option;
        }
        return null;
    }
}
