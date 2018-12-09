package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

import java.util.List;

import androidx.annotation.Nullable;

public class CurrencyFormItem extends BaseFormItem {

    private @Nullable Double value;
    private @Nullable String selectedOption;
    private List<String> options;

    private CurrencyFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        //todo add validation
        return false;
    }

    @Override
    public String getStringValue() {
        //todo string value
        return null;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    @Nullable
    public Double getValue() {
        return value;
    }

    public void setValue(@Nullable Double value) {
        this.value = value;
    }

    @Nullable
    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(@Nullable String selectedOption) {
        this.selectedOption = selectedOption;
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
        private Double value;
        private String selectedOption;
        private List<String> options;

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

        public Builder setValue(Double value) {
            this.value = value;

            return this;
        }

        public Builder setSelectedOption(String selectedOption) {
            this.selectedOption = selectedOption;

            return this;
        }

        public Builder setOptions(List<String> options) {
            this.options = options;

            return this;
        }

        public CurrencyFormItem build() {
            CurrencyFormItem item = new CurrencyFormItem();

            item.setTitle(title);
            item.setTitleRes(titleRes);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setEnabled(isEnabled);
            item.setVisible(isVisible);
            item.setTypeInfo(typeInfo);
            item.setMachineName(machineName);
            item.setValue(value);
            item.setSelectedOption(selectedOption);
            item.setOptions(options);
            item.setViewType(FormItemViewType.CURRENCY);

            return item;
        }
    }
}
