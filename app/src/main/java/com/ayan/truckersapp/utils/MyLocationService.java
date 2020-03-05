package com.ayan.truckersapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.ayan.truckersapp.MapActivity;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

public class MyLocationService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            LocationResult result = LocationResult.extractResult(intent);
            Location loc = result.getLastLocation();
            LatLng loc_lt = new LatLng(loc.getLatitude(), loc.getLongitude());

        }
    }
}
