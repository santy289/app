package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.ActivityGeolocationBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class GeolocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener {

    private static final String TAG = "GeolocationActivity";

    @Inject
    GeolocationViewModelFactory geolocationViewModelFactory;
    private GeolocationViewModel geolocationViewModel;
    private ActivityGeolocationBinding mBinding;

    private GoogleMap mMap;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_geolocation);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        geolocationViewModel = ViewModelProviders
                .of(this, geolocationViewModelFactory)
                .get(GeolocationViewModel.class);

        subscribe();
        setActionBar();
        setOnClickListeners();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void subscribe() {
        geolocationViewModel.getObservableToastMessage().observe(this, this::showToastMessage);
        geolocationViewModel.getObservableLocationPermissionsGranted()
                .observe(this, this::handleLocationPermissionsGranted);
        geolocationViewModel.getObservableEnableConfirmButton()
                .observe(this, this::enableConfirmButton);
    }

    /**
     * Setup the Activity's action bar.
     */
    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getTitle());
    }

    private void setOnClickListeners() {
        mBinding.btnConfirm.setOnClickListener(v -> confirmLocation());
    }

    @UiThread
    private void enableConfirmButton(boolean enable) {
        mBinding.btnConfirm.setEnabled(enable);
    }

    //region Map

    /**
     * Manipulates the map once available. This callback is triggered when the map is ready to be
     * used. This is where we can add markers or lines, add listeners or move the camera. In this
     * case, we just add a marker near Sydney, Australia. If Google Play services is not installed
     * on the device, the user will be prompted to install it inside the SupportMapFragment. This
     * method will only be triggered once the user has installed Google Play services and returned
     * to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Move the camera to Panama as a default location
        moveMap(new LatLng(GeolocationViewModel.PANAMA_DEFAULT_LAT,
                GeolocationViewModel.PANAMA_DEFAULT_LNG));

        mMap.setOnCameraIdleListener(() -> {
            //get location at the center by calling
            LatLng centerLatLng = mMap.getCameraPosition().target;
            geolocationViewModel.setSelectedLocation(centerLatLng);
        });

        checkLocationPermissions();
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
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

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
        geolocationViewModel.handleRequestPermissionsResult(requestCode, grantResults);
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

    private void confirmLocation() {
        LatLng latLng = geolocationViewModel.getSelectedLocation();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(GeolocationViewModel.EXTRA_REQUESTED_LOCATION, latLng);
        setResult(RESULT_OK, returnIntent);
        finish();
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
}
