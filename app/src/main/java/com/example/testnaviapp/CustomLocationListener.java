package com.example.testnaviapp;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class CustomLocationListener implements LocationListener, Listener {

    private static final String TAG = CustomLocationListener.class.getName();

    public final static String EVENT_CHANGE_LOCATION = "event_change_location";
    public final static String KEY_LOCATION = "key_location";
    private double MAX_ACCURACY = 35;
    private Location prevLocation = null;

    private boolean gpsFix = false;
    private Context context;

    public CustomLocationListener(Context context) {
        this.context = context;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "Location provider status changed " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void sendChangeLocation(Location location) {
        Intent intent = new Intent(EVENT_CHANGE_LOCATION);
        intent.putExtra(KEY_LOCATION, new LatLng(location.getLatitude(), location.getLongitude()));
        context.sendBroadcast(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getAccuracy() > MAX_ACCURACY) {
            Log.d(TAG, location.getAccuracy()
                    + " > " + MAX_ACCURACY + ", " + location);
            sendChangeLocation(location);
            return;
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                if (prevLocation != null) {
                    //gpsFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < (getGpsFrequencyDeltaTime(mCurrentGPSFrequencyMode) * 2000);

                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:

                gpsFix = true;
                break;
            case GpsStatus.GPS_EVENT_STARTED:

                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                gpsFix = false;

                break;
        }
    }

    public boolean isGPSFix() {
        return gpsFix;
    }
}
