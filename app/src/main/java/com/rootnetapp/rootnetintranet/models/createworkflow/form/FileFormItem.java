package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

public class FileFormItem extends BaseFormItem {

    private String value;
    private OnButtonClickedListener onButtonClickedListener;

    private FileFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        //todo check
        return true;
    }

    @Override
    public String getStringValue() {
        //todo check
        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public OnButtonClickedListener getOnButtonClickedListener() {
        return onButtonClickedListener;
    }

    public void setOnButtonClickedListener(OnButtonClickedListener onButtonClickedListener) {
        this.onButtonClickedListener = onButtonClickedListener;
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
        private String value;

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

        public Builder setValue(String value) {
            this.value = value;

            return this;
        }

        public FileFormItem build() {
            FileFormItem item = new FileFormItem();

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
            item.setViewType(FormItemViewType.FILE);

            return item;
        }
    }

    public interface OnButtonClickedListener {

        void onButtonClicked();
    }
}
