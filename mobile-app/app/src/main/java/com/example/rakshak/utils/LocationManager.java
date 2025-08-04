package com.example.rakshak.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationManager {

    private static final String TAG = "LocationManager";
    private final FusedLocationProviderClient fusedLocationClient;
    private final Context context;

    // A callback interface to return the location asynchronously
    public interface LocationResultListener {
        void onLocationResult(Location location);
        void onLocationError(String error);
    }

    public LocationManager(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation(final LocationResultListener listener) {
        // 1. Check for location permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            listener.onLocationError("Location permission not granted.");
            return;
        }

        // 2. Create a high-accuracy location request
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdateDelayMillis(10000)
                .build();
        
        // 3. Create a callback to receive the location update
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    Log.w(TAG, "Failed to get location.");
                    listener.onLocationError("Failed to get location.");
                    return;
                }
                // Stop listening for updates after we get one
                fusedLocationClient.removeLocationUpdates(this);
                
                // Return the result
                Location location = locationResult.getLastLocation();
                Log.i(TAG, "Location found: " + location.getLatitude() + ", " + location.getLongitude());
                listener.onLocationResult(location);
            }
        };

        // 4. Request the location update
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
}