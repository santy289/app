package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

public class TextInputFormItem extends BaseFormItem {

    private @Nullable String value;
    private @Nullable String hint;
    private @Nullable String regex;
    private @Nullable String errorMessage;
    private @InputType String inputType;

    private TextInputFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        boolean isFilled = getValue() != null && !getValue().isEmpty();

        boolean matchesRegex = true;
        if (getRegex() != null && isFilled) {
            matchesRegex = Pattern.compile(getRegex()).matcher(getValue()).matches();
        }

        if (isFilled && matchesRegex) return true;

        return !isFilled && !isRequired();

    }

    @Override
    public String getStringValue() {
        return getValue();
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public String getHint() {
        return hint;
    }

    public void setHint(@Nullable String hint) {
        this.hint = hint;
    }

    @Nullable
    public String getRegex() {
        return regex;
    }

    public void setRegex(@Nullable String regex) {
        this.regex = regex;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public static class Builder {

        private String title;
        private int titleRes;
        private int tag;
        private boolean isRequired;
        private boolean isEscaped;
        private String value;
        private String hint;
        private String regex;
        private String errorMessage;
        private @InputType String inputType;

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

        public Builder setValue(String value) {
            this.value = value;

            return this;
        }

        public Builder setHint(String hint) {
            this.hint = hint;

            return this;
        }

        public Builder setRegex(String regex) {
            this.regex = regex;

            return this;
        }

        public Builder setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;

            return this;
        }

        public Builder setInputType(@InputType String inputType) {
            this.inputType = inputType;

            return this;
        }

        public TextInputFormItem build() {
            TextInputFormItem item = new TextInputFormItem();

            item.setTitle(title);
            item.setTitleRes(titleRes);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setValue(value);
            item.setHint(hint);
            item.setRegex(regex);
            item.setErrorMessage(errorMessage);
            item.setInputType(inputType);
            item.setViewType(FormItemViewType.TEXT_INPUT);

            return item;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            InputType.TEXT,
            InputType.TEXT_AREA,
            InputType.PHONE,
            InputType.EMAIL,
            InputType.NUMBER,
            InputType.DECIMAL
    })
    public @interface InputType {

        String TEXT = "text";
        String TEXT_AREA = "string";
        String PHONE = "phone";
        String EMAIL = "email";
        String NUMBER = "integer";
        String DECIMAL = "decimal";
    }
}
