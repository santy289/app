package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

public class TextInputFormItem extends BaseFormItem {

    private @Nullable String value;
    private @Nullable String hint;
    private @Nullable String regex;
    private @Nullable String regexErrorMessage;
    private @InputType int inputType;

    private TextInputFormItem() {
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
    public String getRegexErrorMessage() {
        return regexErrorMessage;
    }

    public void setRegexErrorMessage(@Nullable String regexErrorMessage) {
        this.regexErrorMessage = regexErrorMessage;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public static class Builder {

        private String title;
        private int tag;
        private boolean isRequired;
        private String value;
        private String hint;
        private String regex;
        private String regexErrorMessage;
        private @InputType int inputType;

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

        public Builder setRegexErrorMessage(String regexErrorMessage) {
            this.regexErrorMessage = regexErrorMessage;

            return this;
        }

        public Builder setInputType(@InputType int inputType) {
            this.inputType = inputType;

            return this;
        }

        public TextInputFormItem build() {
            TextInputFormItem item = new TextInputFormItem();

            item.setTitle(title);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setValue(value);
            item.setHint(hint);
            item.setRegex(regex);
            item.setRegexErrorMessage(regexErrorMessage);
            item.setInputType(inputType);
            item.setViewType(FormItemViewType.TEXT_INPUT);

            return item;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            InputType.TEXT,
            InputType.TEXT_AREA,
            InputType.PHONE,
            InputType.EMAIL
    })
    public @interface InputType {

        int TEXT = 1;
        int TEXT_AREA = 2;
        int PHONE = 3;
        int EMAIL = 4;
    }
}
