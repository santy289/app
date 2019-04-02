package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.PlaceDetailsResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.AutocompleteResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.Prediction;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch.NearbySearchResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch.Place;

import java.util.ArrayList;
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
    private MutableLiveData<LatLng> mMoveMapLiveData;
    private MutableLiveData<Boolean> mHideSuggestionsLiveData;
    private MutableLiveData<Boolean> mShowNoConnectionViewLiveData;

    private final CompositeDisposable mDisposables;

    private GeolocationRepository mRepository;
    private LatLng mSelectedLocation;
    private String mSelectedAddress;
    private String mApiKey;
    private boolean isSearchingForPlace;
    private boolean isConfirmQueued;
    private String mSearchQuery;
    private boolean isNearbySearch;

    protected GeolocationViewModel(GeolocationRepository repository) {
        this.mRepository = repository;

        mDisposables = new CompositeDisposable();
    }

    protected void init(String apiKey) {
        mApiKey = apiKey.replace("\"", "");
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

    /**
     * Sends the location information back to the Activity. If there is a nearby place search in
     * progress, enqueue the confirmation action until the search is completed.
     */
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

    /**
     * Sets whether there is a queued confirmation action.
     */
    private void enqueueConfirmLocation() {
        isConfirmQueued = true;
    }

    //region Nearby Search

    /**
     * Fetches the repository for the nearby places to the specified coordinates. This is used when
     * the user moves the map with gestures to select a new location based on the coordinates alone.
     * This fetches the closest places to those coordinates, defining the name of the selected
     * location.
     *
     * @param latLng coordinates to search places nearby.
     */
    private void searchNearbyPlaces(LatLng latLng) {
        isSearchingForPlace = true;
        Disposable disposable = mRepository.getNearbyPlaces(mApiKey, latLng)
                .subscribe(this::onSuccessNearbyPlaces, this::onFailureNearbyPlaces);

        mDisposables.add(disposable);
    }

    private void onSuccessNearbyPlaces(NearbySearchResponse nearbySearchResponse) {
        mShowLoadingLiveData.setValue(false);
        if (nearbySearchResponse.getPlaces().isEmpty()) {
            isSearchingForPlace = false;
            return;
        }

        Place first = nearbySearchResponse.getPlaces().get(0);

        //retrieve full address
        isNearbySearch = true;
        getPlaceDetails(first.getPlaceId());
    }

    private void onFailureNearbyPlaces(Throwable throwable) {
        mShowLoadingLiveData.setValue(false);
        isSearchingForPlace = false;

        mToastMessageLiveData.setValue(R.string.failure_connect);

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

    /**
     * Performs a search of places based on a query. Used with the autocomplete feature to display
     * suggestions as the user types.
     *
     * @param input search query.
     */
    private void searchAutocomplete(String input) {
        Disposable disposable = mRepository.getAutocompletePlaces(mApiKey, input)
                .subscribe(this::onSuccessAutocomplete, this::onFailureAutocomplete);

        mDisposables.add(disposable);
    }

    private void onSuccessAutocomplete(AutocompleteResponse autocompleteResponse) {
        mPredictionsLiveData.setValue(autocompleteResponse.getPredictions());
        mHideSuggestionsLiveData.setValue(false);
        mShowNoConnectionViewLiveData.setValue(false);
    }

    private void onFailureAutocomplete(Throwable throwable) {
        mPredictionsLiveData.setValue(new ArrayList<>());
        mHideSuggestionsLiveData.setValue(true);
        mShowNoConnectionViewLiveData.setValue(true);

        Log.d(TAG, "searchAutocomplete: failed: " + throwable.getMessage());
    }
    //endregion

    //region Place Details Search

    /**
     * Performs a search to fetch the details of a specific place. Used after the user selects an
     * autocomplete suggestion in order to retrieve the coordinates of the selected place.
     *
     * @param placeId {@link Place#id}.
     */
    protected void getPlaceDetails(String placeId) {
        if (!isNearbySearch) {
            mShowLoadingLiveData.setValue(true);
        }

        Disposable disposable = mRepository.getPlaceDetails(mApiKey, placeId)
                .subscribe(this::onSuccessPlaceDetails, this::onFailurePlaceDetails);

        mDisposables.add(disposable);
    }

    private void onSuccessPlaceDetails(PlaceDetailsResponse placeDetailsResponse) {
        isSearchingForPlace = false;

        mShowLoadingLiveData.setValue(false);
        mHideSuggestionsLiveData.setValue(true);
        mShowNoConnectionViewLiveData.setValue(false);

        Place place = placeDetailsResponse.getPlace();
        String address = place.getName();
        if (!TextUtils.isEmpty(place.getFormattedAddress())) {
            address += ", " + place.getFormattedAddress();
        }
        setSelectedAddress(address);

        if (isNearbySearch) {
            isNearbySearch = false;

            if (isConfirmQueued) {
                confirmLocation();
            }
            return;
        }

        LatLng latLng = new LatLng(
                place.getGeometry().getLocation().getLat(),
                place.getGeometry().getLocation().getLng()
        );
        mMoveMapLiveData.setValue(latLng);

        setSelectedLocation(latLng, false);
    }

    private void onFailurePlaceDetails(Throwable throwable) {
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

    protected LiveData<LatLng> getObservableMoveMap() {
        if (mMoveMapLiveData == null) {
            mMoveMapLiveData = new MutableLiveData<>();
        }
        return mMoveMapLiveData;
    }

    protected LiveData<Boolean> getObservableHideSuggestions() {
        if (mHideSuggestionsLiveData == null) {
            mHideSuggestionsLiveData = new MutableLiveData<>();
        }
        return mHideSuggestionsLiveData;
    }

    protected LiveData<Boolean> getObservableShowNoConnectionView() {
        if (mShowNoConnectionViewLiveData == null) {
            mShowNoConnectionViewLiveData = new MutableLiveData<>();
        }
        return mShowNoConnectionViewLiveData;
    }
}
