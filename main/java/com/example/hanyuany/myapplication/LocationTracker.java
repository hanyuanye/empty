package com.example.hanyuany.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by hanyuany on 15/03/2018.
 */

public class LocationTracker {
    private static String TAG = "tag";
    private static int requestCode = 1;

    private Context mContext;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location userLocation;
    private LocationManager locationManager;
    public LocationTracker(Context context, Activity activity) {
        mContext = context;
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "permission denied");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        }

        locationManager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "location changed");
                userLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(TAG, "status changed");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "provider enabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "provider disabled");
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(6000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                userLocation = locationResult.getLastLocation();
            }
        };
    }

    public Location getLocation() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        return userLocation;
    }

}
