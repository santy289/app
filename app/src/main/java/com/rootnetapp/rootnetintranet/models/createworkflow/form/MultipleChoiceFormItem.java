package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceFormItem extends BaseFormItem {

    private List<Option> values;
    private List<Option> options;

    private MultipleChoiceFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        if (!isRequired()) return true;

        return isRequired() && getValues() != null;

    }

    @Override
    public String getStringValue() {
        if (getValues().isEmpty()) return "";

        //create a csv string value
        char separator = ',';
        StringBuilder stringBuilder = new StringBuilder();
        for (Option value : getValues()) {
            stringBuilder.append(value.toString());
            stringBuilder.append(separator);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1); //delete last separator

        return stringBuilder.toString();
    }

    public List<Option> getValues() {
        if (values == null) values = new ArrayList<>();

        return values;
    }

    public void setValues(List<Option> values) {
        this.values = values;
    }

    public void addValue(Option value){
        getValues().add(value);
    }

    public void removeValue(Option value){
        getValues().remove(value);
    }

    public void removeValue(int position){
        getValues().remove(position);
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public static class Builder {

        private String title;
        private int titleRes;
        private int tag;
        private boolean isRequired;
        private boolean isEscaped;
        private boolean isEnabled = true;
        private boolean isVisible = true;
        private TypeInfo typeInfo;
        private String machineName;
        private List<Option> values;
        private List<Option> options;

        public Builder setTitle(String title) {
            this.title = title;

            return this;
        }

        public Builder setTitleRes(int titleRes) {
            this.titleRes = titleRes;

            return this;
        }

        public Builder setTag(int tag) {
            this.tag = tag;

            return this;
        }

        public Builder setRequired(boolean isRequired) {
            this.isRequired = isRequired;

            return this;
        }

        public Builder setEscaped(boolean isEscaped) {
            this.isEscaped = isEscaped;

            return this;
        }

        public Builder setEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;

            return this;
        }

        public Builder setVisible(boolean isVisible) {
            this.isVisible = isVisible;

            return this;
        }

        public Builder setTypeInfo(TypeInfo typeInfo) {
            this.typeInfo = typeInfo;

            return this;
        }

        public Builder setMachineName(String machineName) {
            this.machineName = machineName;

            return this;
        }

        public Builder setValues(List<Option> values) {
            this.values = values;

            return this;
        }

        public Builder setOptions(List<Option> options) {
            this.options = options;

            return this;
        }

        public MultipleChoiceFormItem build() {
            MultipleChoiceFormItem item = new MultipleChoiceFormItem();

            item.setTitle(title);
            item.setTitleRes(titleRes);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setEnabled(isEnabled);
            item.setVisible(isVisible);
            item.setTypeInfo(typeInfo);
            item.setMachineName(machineName);
            item.setValues(values);
            item.setOptions(options);
            item.setViewType(FormItemViewType.MULTIPLE_CHOICE);

            return item;
        }
    }
}
