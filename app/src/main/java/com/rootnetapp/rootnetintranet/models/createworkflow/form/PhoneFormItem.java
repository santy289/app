package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

import java.util.List;

import androidx.annotation.Nullable;

public class PhoneFormItem extends BaseFormItem {

    private @Nullable String value;
    private @Nullable Option selectedOption;
    private List<Option> options;

    private PhoneFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        //todo validate phone regex
        if (!isRequired()) return true;

        return isRequired() && getSelectedOption() != null && getValue() != null;
    }

    @Override
    public String getStringValue() {
        return getSelectedOption() == null || value == null ? "" : getSelectedOption().getName() + " " + getValue();
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @Nullable
    public String getValue() {
        return value == null ? null : value.trim(); //remove whitespaces at the start and at the end
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public Option getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(@Nullable Option selectedOption) {
        this.selectedOption = selectedOption;
    }

    public static class Builder {

        private String title;
        private int titleRes;
        private int tag;
        private int fieldId;
        private boolean isRequired;
        private boolean isEscaped;
        private boolean isEnabled = true;
        private boolean isVisible = true;
        private TypeInfo typeInfo;
        private String machineName;
        private String value;
        private Option selectedOption;
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

        public Builder setFieldId(int fieldId) {
            this.fieldId = fieldId;

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

        public Builder setValue(String value) {
            this.value = value;

            return this;
        }

        public Builder setSelectedOption(Option selectedOption) {
            this.selectedOption = selectedOption;

            return this;
        }

        public Builder setOptions(List<Option> options) {
            this.options = options;

            return this;
        }

        public PhoneFormItem build() {
            PhoneFormItem item = new PhoneFormItem();

            item.setTitle(title);
            item.setTitleRes(titleRes);
            item.setTag(tag);
            item.setFieldId(fieldId);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setEnabled(isEnabled);
            item.setVisible(isVisible);
            item.setTypeInfo(typeInfo);
            item.setMachineName(machineName);
            item.setValue(value);
            item.setSelectedOption(selectedOption);
            item.setOptions(options);
            item.setViewType(FormItemViewType.PHONE);

            return item;
        }
    }
}
