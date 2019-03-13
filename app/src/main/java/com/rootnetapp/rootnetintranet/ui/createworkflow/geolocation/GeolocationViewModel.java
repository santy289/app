package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.PlaceDetailsResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.AutocompleteResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.Prediction;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch.NearbySearchResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch.Place;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class GeolocationViewModel extends ViewModel {

    protected static final int REQUEST_LOCATION_PERMISSIONS = 41;
    protected static final long MIN_TIME = 0;
    protected static final float MIN_DISTANCE = 1000;
    protected static final float DEFAULT_ZOOM = 17;

    protected static final double PANAMA_DEFAULT_LAT = 9.180245;
    protected static final double PANAMA_DEFAULT_LNG = -79.3721478;
    protected static final float PANAMA_ZOOM_OUT = 10;

    public static final String EXTRA_REQUESTED_LOCATION = "ExtraRequestedLocation";
    public static final String EXTRA_SHOW_LOCATION = "ExtraShowLocation";

    private static final String TAG = "GeolocationViewModel";

    private MutableLiveData<Integer> mToastMessageLiveData;
    private MutableLiveData<Boolean> mShowLoadingLiveData;
    private MutableLiveData<Boolean> mLocationPermissionsGrantedLiveData;
    private MutableLiveData<Boolean> mEnableConfirmButtonLiveData;
    private MutableLiveData<String> mSelectedAddressLiveData;
    private MutableLiveData<SelectedLocation> mConfirmLocationLiveData;
    private MutableLiveData<List<Prediction>> mPredictionsLiveData;
    private MutableLiveData<Place> mPlaceDetailsLiveData;
    private MutableLiveData<Boolean> mHideSuggestionsLiveData;

    private final CompositeDisposable mDisposables;

    private GeolocationRepository mRepository;
    private LatLng mSelectedLocation;
    private String mSelectedAddress;
    private String mApiKey;
    private boolean isSearchingForPlace;
    private boolean isConfirmQueued;
    private String mSearchQuery;

    protected GeolocationViewModel(GeolocationRepository repository) {
        this.mRepository = repository;

        mDisposables = new CompositeDisposable();
    }

    protected void init(String apiKey) {
        mApiKey = apiKey;
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    protected LatLng getSelectedLocation() {
        return mSelectedLocation;
    }

    protected void setSelectedLocation(LatLng selectedLocation, boolean hasSelectedFromMap) {
        if (selectedLocation != null) {
            if (hasSelectedFromMap) searchNearbyPlaces(selectedLocation);
            mEnableConfirmButtonLiveData.setValue(true);
        }

        this.mSelectedLocation = selectedLocation;
    }

    protected String getSelectedAddress() {
        return mSelectedAddress;
    }

    protected void setSelectedAddress(String selectedAddress) {
        mSelectedAddressLiveData.setValue(selectedAddress);

        this.mSelectedAddress = selectedAddress;
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

    protected void confirmLocation() {
        if (isSearchingForPlace) {
            //wait until the nearby search is completed
            mShowLoadingLiveData.setValue(true);
            enqueueConfirmLocation();
            return;
        }

        isConfirmQueued = false;

        SelectedLocation selectedLocation = new SelectedLocation(getSelectedLocation(),
                getSelectedAddress());
        mConfirmLocationLiveData.setValue(selectedLocation);
    }

    private void enqueueConfirmLocation() {
        isConfirmQueued = true;
    }

    //region Nearby Search
    private void searchNearbyPlaces(LatLng latLng) {
        isSearchingForPlace = true;
        Disposable disposable = mRepository.getNearbyPlaces(mApiKey, latLng)
                .subscribe(this::onSuccessNearbyPlaces, this::onFailureNearbyPlaces);

        mDisposables.add(disposable);
    }

    private void onSuccessNearbyPlaces(NearbySearchResponse nearbySearchResponse) {
        isSearchingForPlace = false;

        if (nearbySearchResponse.getPlaces().isEmpty()) return;

        Place first = nearbySearchResponse.getPlaces().get(0);
        setSelectedAddress(first.getName());

        if (isConfirmQueued) {
            confirmLocation();
        }
    }

    private void onFailureNearbyPlaces(Throwable throwable) {
        //todo better error handling
        isSearchingForPlace = false;

        Log.d(TAG, "searchNearbyPlaces: failed: " + throwable.getMessage());
    }
    //endregion

    //region Autocomplete Search
    protected String getSearchQuery() {
        return mSearchQuery;
    }

    protected void setSearchQuery(String searchQuery) {
        searchAutocomplete(searchQuery);

        this.mSearchQuery = searchQuery;
    }

    private void searchAutocomplete(String input) {
        Disposable disposable = mRepository.getAutocompletePlaces(mApiKey, input)
                .subscribe(this::onSuccessAutocomplete, this::onFailureAutocomplete);

        mDisposables.add(disposable);
    }

    private void onSuccessAutocomplete(AutocompleteResponse autocompleteResponse) {
        mPredictionsLiveData.setValue(autocompleteResponse.getPredictions());
    }

    private void onFailureAutocomplete(Throwable throwable) {

        Log.d(TAG, "searchAutocomplete: failed: " + throwable.getMessage());
    }
    //endregion

    //region Autocomplete Search
    protected void getPlaceDetails(String placeId) {
        mShowLoadingLiveData.setValue(true);

        Disposable disposable = mRepository.getPlaceDetails(mApiKey, placeId)
                .subscribe(this::onSuccessPlaceDetails, this::onFailurePlaceDetails);

        mDisposables.add(disposable);
    }

    private void onSuccessPlaceDetails(PlaceDetailsResponse placeDetailsResponse) {
        mShowLoadingLiveData.setValue(false);
        mHideSuggestionsLiveData.setValue(true);

        mPlaceDetailsLiveData.setValue(placeDetailsResponse.getPlace());
    }

    private void onFailurePlaceDetails(Throwable throwable) {
        //todo better error handling
        mShowLoadingLiveData.setValue(false);

        mToastMessageLiveData.setValue(R.string.failure_connect);

        Log.d(TAG, "getPlaceDetails: failed: " + throwable.getMessage());
    }
    //endregion

    protected LiveData<Integer> getObservableToastMessage() {
        if (mToastMessageLiveData == null) {
            mToastMessageLiveData = new MutableLiveData<>();
        }
        return mToastMessageLiveData;
    }

    protected LiveData<Boolean> getObservableShowLoading() {
        if (mShowLoadingLiveData == null) {
            mShowLoadingLiveData = new MutableLiveData<>();
        }
        return mShowLoadingLiveData;
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

    protected LiveData<String> getObservableSelectedAddress() {
        if (mSelectedAddressLiveData == null) {
            mSelectedAddressLiveData = new MutableLiveData<>();
        }
        return mSelectedAddressLiveData;
    }

    protected LiveData<SelectedLocation> getObservableConfirmLocation() {
        if (mConfirmLocationLiveData == null) {
            mConfirmLocationLiveData = new MutableLiveData<>();
        }
        return mConfirmLocationLiveData;
    }

    protected LiveData<List<Prediction>> getObservablePredictions() {
        if (mPredictionsLiveData == null) {
            mPredictionsLiveData = new MutableLiveData<>();
        }
        return mPredictionsLiveData;
    }

    protected LiveData<Place> getObservablePlaceDetails() {
        if (mPlaceDetailsLiveData == null) {
            mPlaceDetailsLiveData = new MutableLiveData<>();
        }
        return mPlaceDetailsLiveData;
    }

    protected LiveData<Boolean> getObservableHideSuggestions() {
        if (mHideSuggestionsLiveData == null) {
            mHideSuggestionsLiveData = new MutableLiveData<>();
        }
        return mHideSuggestionsLiveData;
    }
}
