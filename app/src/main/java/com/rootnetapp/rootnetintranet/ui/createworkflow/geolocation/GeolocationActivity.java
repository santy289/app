package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityGeolocationBinding;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.Prediction;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class GeolocationActivity extends AppCompatActivity implements GeolocationActivityInterface,
        OnMapReadyCallback,
        LocationListener {

    private static final String TAG = "GeolocationActivity";

    @Inject
    GeolocationViewModelFactory geolocationViewModelFactory;
    private GeolocationViewModel viewModel;
    private ActivityGeolocationBinding mBinding;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private SuggestionsAdapter mSuggestionsAdapter;
    private TextWatcher mTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_geolocation);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        viewModel = ViewModelProviders
                .of(this, geolocationViewModelFactory)
                .get(GeolocationViewModel.class);

        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String googleMapsApiKey = prefs.getString(PreferenceKeys.PREF_GOOGLE_MAPS_API_KEY, "");
        viewModel.init(googleMapsApiKey);

        subscribe();
        setOnClickListeners();
        setupInputSearch();
        setupSuggestionsRecycler();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void subscribe() {
        viewModel.getObservableToastMessage().observe(this, this::showToastMessage);
        viewModel.getObservableShowLoading().observe(this, this::showLoading);
        viewModel.getObservableLocationPermissionsGranted()
                .observe(this, this::handleLocationPermissionsGranted);
        viewModel.getObservableEnableConfirmButton().observe(this, this::enableConfirmButton);
        viewModel.getObservableSelectedAddress().observe(this, this::updateSelectedAddressUi);
        viewModel.getObservableConfirmLocation().observe(this, this::returnLocation);
        viewModel.getObservablePredictions().observe(this, this::updateSuggestionsData);
        viewModel.getObservableMoveMap().observe(this, this::moveMap);
        viewModel.getObservableHideSuggestions().observe(this, this::hideSuggestions);
        viewModel.getObservableShowNoConnectionView().observe(this, this::showNoConnectionView);
    }

    private void setOnClickListeners() {
        mBinding.btnConfirm.setOnClickListener(v -> confirmLocation());
        mBinding.btnNavigate
                .setOnClickListener(v -> navigateToLocation(viewModel.getSelectedLocation()));
        mBinding.btnClear.setOnClickListener(v -> mBinding.etSearch.setText(null));
    }

    private void setupInputSearch() {
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            private Handler handler = new Handler(Looper.getMainLooper());
            private Runnable workRunnable;
            private final int DELAY = 500;

            @Override
            public void afterTextChanged(Editable s) {
                //wait until the user is done typing
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> {
                    //only perform the search if on user input
                    if (mBinding.etSearch.hasFocus()) {
                        String input = String.valueOf(s);
                        viewModel.setSearchQuery(input);
                    }
                };
                handler.postDelayed(workRunnable, DELAY);
            }
        };

        mBinding.etSearch.addTextChangedListener(mTextWatcher);
    }

    private void setupSuggestionsRecycler() {
        mSuggestionsAdapter = new SuggestionsAdapter(this, new ArrayList<>());
        mBinding.rvSuggestions.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvSuggestions.setAdapter(mSuggestionsAdapter);
    }

    @UiThread
    private void enableConfirmButton(boolean enable) {
        mBinding.btnConfirm.setEnabled(enable);
    }

    @UiThread
    private void hideConfirmButton(boolean hide) {
        mBinding.btnConfirm.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @UiThread
    private void hideNavigateButton(boolean hide) {
        mBinding.btnNavigate.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @UiThread
    private void hideCenterMarker(boolean hide) {
        mBinding.ivCenterMarker.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @UiThread
    private void hideSearchInput(boolean hide) {
        mBinding.lytSearchInput.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @UiThread
    private void updateSelectedAddressUi(String name) {
        mBinding.etSearch.setText(name);
    }

    @UiThread
    private void updateSuggestionsData(List<Prediction> predictionList) {
        hideSuggestions(predictionList.isEmpty());

        mSuggestionsAdapter.setData(predictionList);
    }

    @UiThread
    private void hideSuggestions(boolean hide) {
        mBinding.rvSuggestions.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @UiThread
    private void showNoConnectionView(boolean show) {
        mBinding.includeNoConnectionView.lytNoConnectionView
                .setVisibility(show ? View.VISIBLE : View.GONE);
    }

    //region Map

    /**
     * Manipulates the map once available. This callback is triggered when the map is ready to be
     * used. This is where we can add markers or lines, add listeners or move the camera. In this
     * case, we just add a marker near Panama. If Google Play services is not installed on the
     * device, the user will be prompted to install it inside the SupportMapFragment. This method
     * will only be triggered once the user has installed Google Play services and returned to the
     * app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SelectedLocation showLocation = getIntent()
                .getParcelableExtra(GeolocationViewModel.EXTRA_SHOW_LOCATION);
        if (showLocation != null) {
            //view only, location already selected
            viewModel.setSelectedLocation(showLocation.getLatLng(), false);

            moveMap(showLocation.getLatLng());

            //show a real marker and hide the centered marker image
            addMarker(showLocation.getLatLng(), showLocation.getName());

            hideConfirmButton(true);
            hideCenterMarker(true);
            hideSearchInput(true);
            hideSuggestions(true);

            hideNavigateButton(false);
            return;
        }

        // Move the camera to Panama as a default location
        moveMap(new LatLng(GeolocationViewModel.PANAMA_DEFAULT_LAT,
                GeolocationViewModel.PANAMA_DEFAULT_LNG), GeolocationViewModel.PANAMA_ZOOM_OUT);

        checkLocationPermissions();

        mMap.setOnCameraIdleListener(() -> {
            //get location at the center by calling
            LatLng centerLatLng = mMap.getCameraPosition().target;
            viewModel.setSelectedLocation(centerLatLng, true);
        });
    }

    /**
     * Adds a custom marker to the map at the specified location.
     *
     * @param latLng marker location.
     * @param title  marker title.
     */
    private void addMarker(LatLng latLng, String title) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(Utils.bitmapDescriptorFromVector(this, R.drawable.ic_location_pin_black_36dp,
                        R.color.colorPrimary))
                .title(title);
        mMap.addMarker(markerOptions);
    }

    /**
     * Calls the {@link #moveMap(LatLng, float)} method with a default zoom of {@link
     * GeolocationViewModel#DEFAULT_ZOOM}.
     *
     * @param newLocation location to move the camera to.
     */
    private void moveMap(LatLng newLocation) {
        moveMap(newLocation, GeolocationViewModel.DEFAULT_ZOOM);
    }

    /**
     * Moves the camera to a specific location. This should be used when the selected location is
     * modified.
     *
     * @param newLocation location to move the camera to.
     * @param zoom        zoom level.
     */
    private void moveMap(LatLng newLocation, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(newLocation, zoom);
        mMap.animateCamera(cameraUpdate);
    }

    /**
     * Checks the location permissions before enabling the map location features. Also, try to
     * retrieve the user's current location using a network and a GPS provider.
     */
    private void enableCurrentLocationFeatures() {
        //enable the my location button
        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //get the current location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
            showToastMessage(R.string.geolocation_activity_cannot_retrieve_location);
        } else {
            // First get location from Network Provider
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        GeolocationViewModel.MIN_TIME,
                        GeolocationViewModel.MIN_DISTANCE,
                        this);
                Log.d(TAG, "Network Location");
                if (locationManager != null) {
                    Location currentLocation = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (currentLocation != null) {
                        moveMap(new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude()));
                        return;
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        GeolocationViewModel.MIN_TIME,
                        GeolocationViewModel.MIN_DISTANCE,
                        this);
                Log.d(TAG, "GPS Location");
                if (locationManager != null) {
                    Location currentLocation = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (currentLocation != null) {
                        moveMap(new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude()));
                    }
                }
            }
        }
    }
    //endregion

    //region Location Permissions

    /**
     * Checks if the user has already granted the location permissions in order to enable the
     * location features. If not, request the permissions.
     */
    private void checkLocationPermissions() {
        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.ACCESS_COARSE_LOCATION},
                    GeolocationViewModel.REQUEST_LOCATION_PERMISSIONS);
            return;
        }

        enableCurrentLocationFeatures();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        viewModel.handleRequestPermissionsResult(requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handleLocationPermissionsGranted(boolean permissionsGranted) {
        if (permissionsGranted) enableCurrentLocationFeatures();
    }
    //endregion

    //region Get Current Location

    /**
     * Should be called only once when the user's current location is retrieved.
     *
     * @param location current location.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, location.toString());

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveMap(latLng);

        //remove updates, we only need the location once
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
    //endregion

    //region Confirm Location

    /**
     * Called when the user taps the submit button.
     */
    private void confirmLocation() {
        viewModel.confirmLocation();
    }

    /**
     * Invoked by the view model's live data, making sure that the last nearby search is completed.
     *
     * @param selectedLocation user's selected location coordinates and place name.
     */
    private void returnLocation(SelectedLocation selectedLocation) {
        if (selectedLocation.getName() == null) {
            selectedLocation.setName(getString(R.string.geolocation_activity_unnamed));
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra(GeolocationViewModel.EXTRA_REQUESTED_LOCATION, selectedLocation);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
    //endregion

    /**
     * Called by the {@link SuggestionsAdapter} item click when the user selects a prediction.
     *
     * @param prediction selected prediction by the user.
     */
    @Override
    public void selectSuggestion(Prediction prediction) {
        hideSoftInputKeyboard();
        mBinding.etSearch.clearFocus();

        viewModel.getPlaceDetails(prediction.getPlaceId());
    }

    /**
     * Creates an IntentChooser to navigate to the specified location.
     *
     * @param latLng location to navigate to.
     */
    private void navigateToLocation(LatLng latLng) {
        if (latLng == null) {
            showToastMessage(R.string.geolocation_activity_could_not_retrieve_location);
            return;
        }

        String url = "waze://?ll=" + latLng.latitude + ", " + latLng.longitude + "&navigate=yes";
        Intent intentWaze = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intentWaze.setPackage("com.waze");

        String uriGoogle = "google.navigation:q=" + latLng.latitude + "," + latLng.longitude;
        Intent intentGoogleNav = new Intent(Intent.ACTION_VIEW, Uri.parse(uriGoogle));
        intentGoogleNav.setPackage("com.google.android.apps.maps");

        String title = getString(R.string.geolocation_activity_navigate);
        Intent chooserIntent = Intent.createChooser(intentGoogleNav, title);
        Intent[] arr = new Intent[1];
        arr[0] = intentWaze;
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr);
        startActivity(chooserIntent);
    }

    /**
     * Defines the behavior for the "homeAsUp" back arrow.
     *
     * @param item menuItem
     *
     * @return whether the event was handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                this,
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    @UiThread
    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(this);
        } else {
            Utils.hideLoading();
        }
    }

    private void hideSoftInputKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
