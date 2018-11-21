package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import java.util.Date;

import androidx.annotation.Nullable;

public class DateFormItem extends BaseFormItem {

    private @Nullable Date value;
    private @Nullable Date minDate;
    private @Nullable Date maxDate;

    private DateFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        //todo add validation
        return false;
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

    public static class Builder {

        private String title;
        private int tag;
        private boolean isRequired;
        private Date value;
        private Date minDate;
        private Date maxDate;

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

        public DateFormItem build() {
            DateFormItem item = new DateFormItem();

            item.setTitle(title);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setValue(value);
            item.setMinDate(minDate);
            item.setMaxDate(maxDate);
            item.setViewType(FormItemViewType.DATE);

            return item;
        }
    }
}
