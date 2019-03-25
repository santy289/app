package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import android.text.TextWatcher;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

import java.util.List;

import androidx.annotation.Nullable;

public class AutocompleteFormItem extends BaseFormItem {

    private @Nullable Option value;
    private @Nullable String query;
    private List<Option> options;
    private OnQueryListener onQueryListener;
    private TextWatcher textWatcher;

    private AutocompleteFormItem() {
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

    @Nullable
    public String getQuery() {
        return query;
    }

    public void setQuery(@Nullable String query) {
        this.query = query;
    }

    public OnQueryListener getOnQueryListener() {
        return onQueryListener;
    }

    public void setOnQueryListener(OnQueryListener onSelectedListener) {
        this.onQueryListener = onSelectedListener;
    }

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
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

        public Builder setTypeInfo(TypeInfo typeInfo) {
            this.typeInfo = typeInfo;

            return this;
        }

        public Builder setMachineName(String machineName) {
            this.machineName = machineName;

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

        public AutocompleteFormItem build() {
            AutocompleteFormItem item = new AutocompleteFormItem();

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
            item.setOptions(options);
            item.setViewType(FormItemViewType.AUTOCOMPLETE);

            return item;
        }
    }

    public interface OnQueryListener {

        void onQuery(AutocompleteFormItem item);
    }
}
