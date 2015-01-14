
package com.example.testnaviapp;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    private static final LatLng HAMBURG = new LatLng(53.558, 9.927);

    private static final LatLng KIEL = new LatLng(53.551, 9.993);

    private LocationManager locationManager;

    private volatile CustomLocationListener locationListener;

    private GoogleMap map;

    private LocationBroadcastReceiver locationBroadcastReceiver;

    private Marker currentLocationMarker;

    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location newLocation = intent.getParcelableExtra(CustomLocationListener.KEY_LOCATION);
            FileLogger.appendLog(MainActivity.this, "newLocation = " + newLocation);
            changeCurrentPositionMarker(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new MapFragment()).commit();
        }

        if (locationManager == null) {
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSettings() {
        Intent intent = new Intent(this, DebugActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMap();
        initMarkers();
        registerReceiver();
        bindGpsListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
        unbindGpsListeners();
    }

    private void registerReceiver() {
        if (locationBroadcastReceiver == null) {
            locationBroadcastReceiver = new LocationBroadcastReceiver();
        }
        registerReceiver(locationBroadcastReceiver, new IntentFilter(
                CustomLocationListener.EVENT_CHANGE_LOCATION));
    }

    private void unregisterReceiver() {
        unregisterReceiver(locationBroadcastReceiver);
    }

    private void bindGpsListeners() {
        if (locationListener == null) {
            locationListener = new CustomLocationListener(this);
            locationManager.removeGpsStatusListener(locationListener);
            locationManager.addGpsStatusListener(locationListener);
            locationManager.removeUpdates(locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long)60 * 1000,
                    100, locationListener);
        }
    }

    private void unbindGpsListeners() {
        if (locationListener != null) {
            locationManager.removeGpsStatusListener(locationListener);
            locationManager.removeUpdates(locationListener);
        }
    }

    private void initMarkers() {
        if (map != null) {
            Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG).title("Hamburg"));
            Marker kiel = map.addMarker(new MarkerOptions().position(KIEL).title("Kiel"));

            Location initialLocation = null;
            try {
                initialLocation = getLastGeoLocation(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (initialLocation != null) {
                FileLogger.appendLog(MainActivity.this, "initialLocation = " + initialLocation);

                LatLng currentLatLng = new LatLng(initialLocation.getLatitude(),
                        initialLocation.getLongitude());
                changeCurrentPositionMarker(currentLatLng);
            }
        }
    }

    private void changeCurrentPositionMarker(LatLng currentLatLng) {
        if (currentLocationMarker == null) {
            currentLocationMarker = map.addMarker(new MarkerOptions().title("MyLocation")
                    .snippet("I'm cool").anchor(0.5f, 0.5f).position(currentLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
        } else {
            Log.d(TAG, "changeCurrentPositionMarker: " + currentLatLng);
            currentLocationMarker.setPosition(currentLatLng);
        }
    }

    private void initMap() {
        if (map == null) {
            map = ((MapFragment)getFragmentManager().findFragmentById(R.id.container)).getMap();
        }
    }

    public Location getLastGeoLocation(Context context) {
        LocationManager locationManager = (LocationManager)context
                .getSystemService(Context.LOCATION_SERVICE);

        String locationProviderNetwork = LocationManager.NETWORK_PROVIDER;
        String locationProviderGPS = LocationManager.GPS_PROVIDER;

        Location networkLocation = locationManager.getLastKnownLocation(locationProviderNetwork);
        Location gpsLocation = locationManager.getLastKnownLocation(locationProviderGPS);

        if (networkLocation != null) {
            String networkLocationDate = DateFormatter.fullDateForFile(new Date(networkLocation
                    .getTime()));
            if (BuildConfig.DEBUG)
                Log.d(TAG, "networkLocation: lat = " + networkLocation.getLatitude() + ", lon = "
                        + networkLocation.getLongitude() + ", acc = "
                        + networkLocation.getAccuracy() + ", date = " + networkLocationDate);
        }
        if (gpsLocation != null) {
            String gpsLocationDate = DateFormatter.fullDateForFile(new Date(gpsLocation.getTime()));
            if (BuildConfig.DEBUG)
                Log.d(TAG, "gpsLocation: lat = " + gpsLocation.getLatitude() + ", lon = "
                        + gpsLocation.getLongitude() + ", acc = " + gpsLocation.getAccuracy()
                        + ", date = " + gpsLocationDate);
        }

        if (gpsLocation != null && networkLocation != null) {
            Date networkLocationDate = new Date(networkLocation.getTime());
            Date gpsLocationDate =new Date(gpsLocation.getTime());
            if(networkLocationDate.after(gpsLocationDate)) {
                return networkLocation;
            } else {
                return gpsLocation;
            }
        } if (gpsLocation != null) {
            return gpsLocation;
        } else if (networkLocation != null) {
            return networkLocation;
        } else {
            return null;
        }
    }
}
