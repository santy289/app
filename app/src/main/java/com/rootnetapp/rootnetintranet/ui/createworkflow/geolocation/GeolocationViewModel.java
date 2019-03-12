package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import android.content.pm.PackageManager;

import com.google.android.gms.maps.model.LatLng;
import com.rootnetapp.rootnetintranet.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class GeolocationViewModel extends ViewModel {

    protected static final int REQUEST_LOCATION_PERMISSIONS = 41;
    protected static final long MIN_TIME = 0;
    protected static final float MIN_DISTANCE = 1000;
    protected static final float DEFAULT_ZOOM = 15;

    protected static final double PANAMA_DEFAULT_LAT = 9.180245;
    protected static final double PANAMA_DEFAULT_LNG = -79.3721478;
    protected static final float PANAMA_ZOOM_OUT = 10;

    public static final String EXTRA_REQUESTED_LOCATION = "ExtraRequestedLocation";
    public static final String EXTRA_SHOW_LOCATION = "ExtraShowLocation";
    public static final String EXTRA_ACTIVITY_TITLE = "ExtraActivityTitle";

    private MutableLiveData<Integer> mToastMessageLiveData;
    private MutableLiveData<Boolean> mLocationPermissionsGrantedLiveData;
    private MutableLiveData<Boolean> mEnableConfirmButtonLiveData;

    private GeolocationRepository repository;
    private LatLng mSelectedLocation;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected GeolocationViewModel(GeolocationRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    protected LatLng getSelectedLocation() {
        return mSelectedLocation;
    }

    protected void setSelectedLocation(LatLng selectedLocation) {
        if (selectedLocation != null) mEnableConfirmButtonLiveData.setValue(true);

        this.mSelectedLocation = selectedLocation;
    }

    /**
     * Checks if the requested permissions were granted and then proceed to export the PDF file.
     *
     * @param requestCode  to identify the request
     * @param grantResults array containing the request results.
     */
    protected void handleRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS: {
                // check for both permissions
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permissions granted
                    mLocationPermissionsGrantedLiveData.setValue(true);

                } else {
                    // at least one permission was denied
                    mLocationPermissionsGrantedLiveData.setValue(false);
                    mToastMessageLiveData.setValue(
                            R.string.workflow_detail_activity_permissions_not_granted);
                }
            }
        }
    }

    protected LiveData<Integer> getObservableToastMessage() {
        if (mToastMessageLiveData == null) {
            mToastMessageLiveData = new MutableLiveData<>();
        }
        return mToastMessageLiveData;
    }

    protected LiveData<Boolean> getObservableLocationPermissionsGranted() {
        if (mLocationPermissionsGrantedLiveData == null) {
            mLocationPermissionsGrantedLiveData = new MutableLiveData<>();
        }
        return mLocationPermissionsGrantedLiveData;
    }

    protected LiveData<Boolean> getObservableEnableConfirmButton() {
        if (mEnableConfirmButtonLiveData == null) {
            mEnableConfirmButtonLiveData = new MutableLiveData<>();
        }
        return mEnableConfirmButtonLiveData;
    }
}
