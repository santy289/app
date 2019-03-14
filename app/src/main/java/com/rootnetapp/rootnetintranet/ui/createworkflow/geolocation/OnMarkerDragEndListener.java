package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public abstract class OnMarkerDragEndListener implements GoogleMap.OnMarkerDragListener {

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public abstract void onMarkerDragEnd(Marker marker);
}
