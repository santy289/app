package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import java.util.List;

import androidx.annotation.Nullable;

public class SingleChoiceFormItem extends BaseFormItem {

    private @Nullable String value;
    private List<String> options;

    private SingleChoiceFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        //todo add validation
        return false;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public static class Builder {

        private String title;
        private int tag;
        private boolean isRequired;
        private boolean isEscaped;
        private String value;
        private List<String> options;

        public Builder setTitle(String title) {
            this.title = title;

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

        public Builder setValue(String value) {
            this.value = value;

            return this;
        }

        public Builder setOptions(List<String> options) {
            this.options = options;

            return this;
        }

        public SingleChoiceFormItem build() {
            SingleChoiceFormItem item = new SingleChoiceFormItem();

            item.setTitle(title);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setValue(value);
            item.setOptions(options);
            item.setViewType(FormItemViewType.SINGLE_CHOICE);

            return item;
        }
    }
}
