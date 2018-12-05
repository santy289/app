package com.rootnetapp.rootnetintranet.models.createworkflow.form;

public class BooleanFormItem extends BaseFormItem {

    private boolean value;

    private BooleanFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getStringValue() {
        return getValue() ? "true" : "false";
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public static class Builder {

        private String title;
        private int titleRes;
        private int tag;
        private boolean isRequired;
        private boolean isEscaped;
        private boolean isEnabled = true;
        private boolean isVisible = true;
        private boolean value;

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

        public Builder setValue(boolean value) {
            this.value = value;

            return this;
        }

        public BooleanFormItem build() {
            BooleanFormItem item = new BooleanFormItem();

            item.setTitle(title);
            item.setTitleRes(titleRes);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setEnabled(isEnabled);
            item.setVisible(isVisible);
            item.setValue(value);
            item.setViewType(FormItemViewType.BOOLEAN);

            return item;
        }
    }
}
