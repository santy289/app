package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

import java.util.Date;

import androidx.annotation.Nullable;

public class DateFormItem extends BaseFormItem {

    private static final String DEFAULT_FORMAT = "MMMM dd, yyyy";

    private @Nullable Date value;
    private @Nullable Date minDate;
    private @Nullable Date maxDate;
    private @Nullable String dateFormat;

    private DateFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        if (!isRequired()) return true;

        return isRequired() && getValue() != null;
    }

    @Override
    public String getStringValue() {
        return getValue() != null ? Utils.getDatePostFormat(getValue()) : null;
    }

    @Nullable
    public Date getValue() {
        return value;
    }

    public void setValue(@Nullable Date value) {
        this.value = value;
    }

    @Nullable
    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(@Nullable Date minDate) {
        this.minDate = minDate;
    }

    @Nullable
    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(@Nullable Date maxDate) {
        this.maxDate = maxDate;
    }

    public String getDateFormat() {
        if (dateFormat == null) return DEFAULT_FORMAT;
        return dateFormat;
    }

    public void setDateFormat(@Nullable String dateFormat) {
        this.dateFormat = dateFormat;
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
        private Date value;
        private Date minDate;
        private Date maxDate;
        private String dateFormat;

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

        public Builder setValue(Date value) {
            this.value = value;

            return this;
        }

        public Builder setMinDate(Date minDate) {
            this.minDate = minDate;

            return this;
        }

        public Builder setMaxDate(Date maxDate) {
            this.maxDate = maxDate;

            return this;
        }

        public Builder setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;

            return this;
        }

        public DateFormItem build() {
            DateFormItem item = new DateFormItem();

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
            item.setMinDate(minDate);
            item.setMaxDate(maxDate);
            item.setDateFormat(dateFormat);
            item.setViewType(FormItemViewType.DATE);

            return item;
        }
    }
}
