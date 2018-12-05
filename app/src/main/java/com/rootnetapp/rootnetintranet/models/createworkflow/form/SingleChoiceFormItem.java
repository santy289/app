package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import java.util.List;

import androidx.annotation.Nullable;

public class SingleChoiceFormItem extends BaseFormItem {

    private @Nullable Option value;
    private List<Option> options;
    private OnSelectedListener onSelectedListener;

    private SingleChoiceFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        if (!isRequired()) return true;

        return isRequired() && getValue() != null;

    }

    @Override
    public String getStringValue() {
        return getValue() != null ? getValue().getName() : null;
    }

    @Nullable
    public Option getValue() {
        return value;
    }

    public void setValue(@Nullable Option value) {
        this.value = value;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public OnSelectedListener getOnSelectedListener() {
        return onSelectedListener;
    }

    public void setOnSelectedListener(
            OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public static class Builder {

        private String title;
        private int titleRes;
        private int tag;
        private boolean isRequired;
        private boolean isEscaped;
        private boolean isEnabled = true;
        private boolean isVisible = true;
        private Option value;
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

        public Builder setValue(Option value) {
            this.value = value;

            return this;
        }

        public Builder setOptions(List<Option> options) {
            this.options = options;

            return this;
        }

        public SingleChoiceFormItem build() {
            SingleChoiceFormItem item = new SingleChoiceFormItem();

            item.setTitle(title);
            item.setTitleRes(titleRes);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setEnabled(isEnabled);
            item.setVisible(isVisible);
            item.setValue(value);
            item.setOptions(options);
            item.setViewType(FormItemViewType.SINGLE_CHOICE);

            return item;
        }
    }

    public interface OnSelectedListener {

        void onSelected(SingleChoiceFormItem item);
    }
}
