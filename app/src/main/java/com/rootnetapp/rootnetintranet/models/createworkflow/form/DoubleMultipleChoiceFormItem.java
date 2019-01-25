package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

import java.util.ArrayList;
import java.util.List;

public class DoubleMultipleChoiceFormItem extends BaseFormItem {

    private List<BaseOption> values;
    private List<Option> firstOptions;
    private List<Option> secondOptions;

    private DoubleMultipleChoiceFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        if (!isRequired()) return true;

        return isRequired() && getValues() != null;

    }

    @Override
    public String getStringValue() {
        if (getValues().isEmpty()) return "";

        //create a csv string value
        char separator = ',';
        StringBuilder stringBuilder = new StringBuilder();
        for (BaseOption value : getValues()) {
            stringBuilder.append(value.toString());
            stringBuilder.append(separator);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1); //delete last separator

        return stringBuilder.toString();
    }

    public List<BaseOption> getValues() {
        if (values == null) values = new ArrayList<>();

        return values;
    }

    public void setValues(List<BaseOption> values) {
        this.values = values;
    }

    public void addValue(DoubleOption value){
        getValues().add(value);
    }

    public void removeValue(DoubleOption value){
        getValues().remove(value);
    }

    public void removeValue(int position){
        getValues().remove(position);
    }

    public List<Option> getFirstOptions() {
        return firstOptions;
    }

    public void setFirstOptions(
            List<Option> firstOptions) {
        this.firstOptions = firstOptions;
    }

    public List<Option> getSecondOptions() {
        return secondOptions;
    }

    public void setSecondOptions(
            List<Option> secondOptions) {
        this.secondOptions = secondOptions;
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
        private List<BaseOption> values;
        private List<Option> firstOptions;
        private List<Option> secondOptions;

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

        public Builder setValues(List<BaseOption> values) {
            this.values = values;

            return this;
        }

        public Builder setFirstOptions(List<Option> firstOptions) {
            this.firstOptions = firstOptions;

            return this;
        }

        public Builder setSecondOptions(List<Option> secondOptions) {
            this.secondOptions = secondOptions;

            return this;
        }

        public DoubleMultipleChoiceFormItem build() {
            DoubleMultipleChoiceFormItem item = new DoubleMultipleChoiceFormItem();

            item.setTitle(title);
            item.setTitleRes(titleRes);
            item.setTag(tag);
            item.setRequired(isRequired);
            item.setEscaped(isEscaped);
            item.setEnabled(isEnabled);
            item.setVisible(isVisible);
            item.setTypeInfo(typeInfo);
            item.setMachineName(machineName);
            item.setValues(values);
            item.setFirstOptions(firstOptions);
            item.setSecondOptions(secondOptions);
            item.setViewType(FormItemViewType.DOUBLE_MULTIPLE_CHOICE);

            return item;
        }
    }
}
