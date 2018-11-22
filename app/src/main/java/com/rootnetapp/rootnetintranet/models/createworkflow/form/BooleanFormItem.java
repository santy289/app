package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import androidx.annotation.Nullable;

public class BooleanFormItem extends BaseFormItem {

    private @Nullable Boolean value;

    private BooleanFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        //todo add validation
        return false;
    }

    @Nullable
    public Boolean getValue() {
        return value;
    }

    public void setValue(@Nullable Boolean value) {
        this.value = value;
    }

    public static class Builder {

        private String title;
        private int tag;
        private boolean isRequired;
        private boolean isEscaped;
        private Boolean value;

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

        public Builder setValue(Boolean value) {
            this.value = value;

            return this;
        }

        public BooleanFormItem build() {
            BooleanFormItem item = new BooleanFormItem();

            item.setTitle(title);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setValue(value);
            item.setViewType(FormItemViewType.BOOLEAN);

            return item;
        }
    }
}
