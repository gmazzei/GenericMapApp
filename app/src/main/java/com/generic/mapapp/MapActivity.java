package com.generic.mapapp;


import com.generic.mapapp.domain.Store;
import com.generic.mapapp.domain.StoreType;
import com.generic.mapapp.service.StoreService;
import com.generic.mapapp.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int OK = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    public GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;

    protected ArrayList<StoreType> storeTypes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        AsyncTask task = new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object... params) {
                storeTypes = (ArrayList<StoreType>) StoreService.get().getStoreTypes();
                return null;
            }
        };

        task.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creating map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;


        AsyncTask task = new AsyncTask<Object, Object, List<Store>>() {

            @Override
            protected List<Store> doInBackground(Object... params) {
                return StoreService.get().getStores();
            }

            @Override
            protected void onPostExecute(List<Store> stores) {
                setMarkers(mMap, stores);
            }
        };
        task.execute();

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Moving to current location", Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }




    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    private void moveToMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null && mGoogleApiClient != null) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(cameraUpdate);
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        moveToMyLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(getClass().getName(), "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(getClass().getName(), "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void setMarkers(GoogleMap map, List<Store> stores) {

        Map<Integer,Float> colorMap = new HashMap<Integer,Float>();
        colorMap.put(0, BitmapDescriptorFactory.HUE_RED);
        colorMap.put(1, BitmapDescriptorFactory.HUE_GREEN);
        colorMap.put(2, BitmapDescriptorFactory.HUE_YELLOW);

        for (Store store : stores) {
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(store.getLatitude(), store.getLongitude()));
            options.title(store.getName());
            options.snippet(store.getType().getName());
            options.icon(BitmapDescriptorFactory.defaultMarker(colorMap.get(store.getType().getId())));
            map.addMarker(options);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_settings:
                Intent intent = new Intent(this, MapSettingsActivity.class);
                intent.putExtra("criteria", storeTypes);
                startActivityForResult(intent, OK);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OK && resultCode == OK && data.hasExtra("criteria")) {

            storeTypes = (ArrayList<StoreType>)data.getSerializableExtra("criteria");
            mMap.clear();

            AsyncTask task = new AsyncTask<Object, Object, List<Store>>() {

                @Override
                protected List<Store> doInBackground(Object... params) {
                    List<Store> stores = StoreService.get().getStores(storeTypes);
                    return stores;
                }

                @Override
                protected void onPostExecute(List<Store> stores) {
                    setMarkers(mMap, stores);
                }
            };

            task.execute();
        }

    }


}


