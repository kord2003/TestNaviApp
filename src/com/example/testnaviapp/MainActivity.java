
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
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    private static final LatLng HAMBURG = new LatLng(53.558, 9.927);

    private static final LatLng KIEL = new LatLng(53.551, 9.993);

    private LocationManager locationManager;

    private volatile MyLocationListener locationListener;

    private GoogleMap map;

    private LocationBroadcastReceiver locationBroadcastReceiver;

    private Marker currentLocationMarker;

    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(MyLocationListener.KEY_LOCATION);
            Log.d(TAG, "new location = " + location);
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

        if (locationListener == null) {
            locationListener = new MyLocationListener(this);
            locationManager.removeGpsStatusListener(locationListener);
            locationManager.addGpsStatusListener(locationListener);
            locationManager.removeUpdates(locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long)60 * 1000,
                    1, locationListener);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMap();
        initMarkers();
    }

    private void initMarkers() {
        if (map != null) {
            Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG).title("Hamburg"));
            Marker kiel = map.addMarker(new MarkerOptions().position(KIEL).title("Kiel"));

            Location lastLocation = null;
            try {
                lastLocation = getLastGeoLocation(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(lastLocation != null) {
                LatLng currentLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                changeCurrentPositionMarker(currentLatLng);

            }
        }
    }

    private void changeCurrentPositionMarker(LatLng currentLatLng) {
        if(currentLocationMarker == null) {
            currentLocationMarker = map.addMarker(new MarkerOptions().title("MyLocation")
                    .snippet("I'm cool").anchor(0.5f, 0.5f).position(currentLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
        } else {
            currentLocationMarker.setPosition(currentLatLng);
        }
    }

    private void initMap() {
        if (map == null) {
            map = ((MapFragment)getFragmentManager().findFragmentById(R.id.container)).getMap();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * A placeholder fragment containing a simple view.
     */

    public Location getLastGeoLocation(Context context) {
        LocationManager locationManager = (LocationManager)context
                .getSystemService(Context.LOCATION_SERVICE);

        String locationProviderNetwork = LocationManager.NETWORK_PROVIDER;
        String locationProviderGPS = LocationManager.GPS_PROVIDER;

        Location lastKnownLocationNetwork = locationManager
                .getLastKnownLocation(locationProviderNetwork);
        Location lastKnownLocationGPS = locationManager.getLastKnownLocation(locationProviderGPS);

        if (lastKnownLocationNetwork != null) {
            //if(BuildConfig.DEBUG) Log.d(TAG, "lastKnownLocationNetwork: lat = " + lastKnownLocationNetwork.getLatitude() + ", lon = " + lastKnownLocationNetwork.getLongitude() + ", acc = " + lastKnownLocationNetwork.getAccuracy());
        }
        if (lastKnownLocationGPS != null) {
            //if(BuildConfig.DEBUG) Log.d(TAG, "lastKnownLocationGPS: lat = " + lastKnownLocationGPS.getLatitude() + ", lon = " + lastKnownLocationGPS.getLongitude() + ", acc = " + lastKnownLocationGPS.getAccuracy());
        }

        if (lastKnownLocationGPS != null) {
            return lastKnownLocationGPS;
        } else if (lastKnownLocationNetwork != null) {
            return lastKnownLocationNetwork;
        } else {
            return null;
        }
    }
}
