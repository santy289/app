package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import com.google.android.gms.maps.model.LatLng;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

public class GeolocationFormItem extends BaseFormItem {

    private LatLng value;
    private String name;
    private OnButtonClickedListener onButtonClickedListener;

    private GeolocationFormItem() {
        //Constructor is private for Builder pattern
    }

    @Override
    public boolean isValid() {
        if (!isRequired()) return true;

        return isRequired() && getValue() != null;
    }

    @Override
    public String getStringValue() {
        return getName();
    }

    public LatLng getValue() {
        return value;
    }

    public void setValue(LatLng value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OnButtonClickedListener getOnButtonClickedListener() {
        return onButtonClickedListener;
    }

    public void setOnButtonClickedListener(OnButtonClickedListener onButtonClickedListener) {
        this.onButtonClickedListener = onButtonClickedListener;
    }

    public void clearLocationValues() {
        setValue(null);
        setName(null);
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
        private LatLng value;

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

        public Builder setValue(LatLng value) {
            this.value = value;

            return this;
        }

        public GeolocationFormItem build() {
            GeolocationFormItem item = new GeolocationFormItem();

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
            item.setViewType(FormItemViewType.GEOLOCATION);

            return item;
        }
    }

    public interface OnButtonClickedListener {

        void onButtonClicked();
    }
}
