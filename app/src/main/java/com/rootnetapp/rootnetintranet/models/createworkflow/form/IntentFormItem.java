package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

public class IntentFormItem extends BaseFormItem {

    private boolean isCompleted;
    private String btnActionText;
    private int btnActionTextRes;
    private OnButtonClickedListener onButtonClickedListener;

    private IntentFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        if (!isRequired()) return true;

        return isRequired() && isCompleted();
    }

    @Override
    public String getStringValue() {
        return isCompleted() ? "true" : "false";
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getButtonActionText() {
        return btnActionText;
    }

    public void setButtonActionText(String btnActionText) {
        this.btnActionText = btnActionText;
    }

    public int getButtonActionTextRes() {
        return btnActionTextRes;
    }

    public void setButtonActionTextRes(int btnActionTextRes) {
        this.btnActionTextRes = btnActionTextRes;
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
        private boolean isCompleted;
        private String btnActionText;
        private int btnActionTextRes;

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

        public Builder setCompleted(Boolean isCompleted) {
            this.isCompleted = isCompleted;

            return this;
        }

        public Builder setButtonActionText(String btnActionText) {
            this.btnActionText = btnActionText;

            return this;
        }

        public Builder setButtonActionTextRes(int btnActionTextRes) {
            this.btnActionTextRes = btnActionTextRes;

            return this;
        }

        public IntentFormItem build() {
            IntentFormItem item = new IntentFormItem();

            item.setTitle(title);
            item.setTitleRes(titleRes);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setEnabled(isEnabled);
            item.setVisible(isVisible);
            item.setTypeInfo(typeInfo);
            item.setMachineName(machineName);
            item.setCompleted(isCompleted);
            item.setButtonActionText(btnActionText);
            item.setButtonActionTextRes(btnActionTextRes);
            item.setViewType(FormItemViewType.INTENT);

            return item;
        }
    }

    public interface OnButtonClickedListener {

        void onButtonClicked();
    }
}
