package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class SelectedLocation implements Parcelable {

    private LatLng latLng;
    private String name;

    public SelectedLocation(LatLng latLng, String name) {
        this.latLng = latLng;
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected SelectedLocation(Parcel in) {
        latLng = (LatLng) in.readValue(LatLng.class.getClassLoader());
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(latLng);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SelectedLocation> CREATOR = new Parcelable.Creator<SelectedLocation>() {
        @Override
        public SelectedLocation createFromParcel(Parcel in) {
            return new SelectedLocation(in);
        }

        @Override
        public SelectedLocation[] newArray(int size) {
            return new SelectedLocation[size];
        }
    };
}