package com.example.mapwithmarker;

import android.content.Intent;
import android.content.IntentSender;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MapsMarkerActivity extends AppCompatActivity
        implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String MA = "MapsMarkerActivity";
    private final static int REQUEST_CODE = 100;

    private GoogleApiClient gac;
    GoogleMap mMap;
    private Location lastLocation;
    private TextView locationTV;
    private TravelManager manager;

    private FusedLocationProviderClient flpa;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private LatLng loc1 = new LatLng(41.235040,-77.030978);
    private LatLng loc2 = new LatLng(41.234502,-77.028654);
    private LatLng loc3 = new LatLng(41.236327,-77.025772);
    private LatLng loc4 = new LatLng(41.238134,-77.024192);
    private LatLng loc5 = new LatLng(41.234339,-77.021788);

    private Location lo1 = new Location("");
    private Location lo2 = new Location("");
    private Location lo3 = new Location("");
    private Location lo4 = new Location("");
    private Location lo5 = new Location("");

    private Marker mark1;
    private Marker mark2;
    private Marker mark3;
    private Marker mark4;
    private Marker mark5;
    private Marker myMark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        locationTV = findViewById(R.id.distance);

        manager = new TravelManager();

        flpa = LocationServices.getFusedLocationProviderClient(this);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        lo1.setLatitude(loc1.latitude);
        lo1.setLongitude(loc1.longitude);
        lo2.setLatitude(loc2.latitude);
        lo2.setLongitude(loc2.longitude);
        lo3.setLatitude(loc3.latitude);
        lo3.setLongitude(loc3.longitude);
        lo4.setLatitude(loc4.latitude);
        lo4.setLongitude(loc4.longitude);
        lo5.setLatitude(loc5.latitude);
        lo5.setLongitude(loc5.longitude);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mark1 = googleMap.addMarker(new MarkerOptions().position(loc1)
                .title("Dauphin Hall/Capitol Eatery"));
        mark2 = googleMap.addMarker(new MarkerOptions().position(loc2)
                .title("Field House"));
        mark3 = googleMap.addMarker(new MarkerOptions().position(loc3)
                .title("Le Jeune Chef"));
        mark4 = googleMap.addMarker(new MarkerOptions().position(loc4)
                .title("The Village at Penn College"));
        mark5 = googleMap.addMarker(new MarkerOptions().position(loc5)
                .title("Madigan Library"));

        mMap.setOnMarkerClickListener(this);

        //get location permission
        getLocationPermission();

        //get current location of device
        getDeviceLocation();


    }

    @Override
    public boolean onMarkerClick(final Marker marker){

        if(marker.equals(mark1)){
            manager.setDestination(lo1);
            int dist = (int) manager.distanceToDestination(lastLocation);
            locationTV.setText(dist + " Meters from " + mark1.getTitle() );
        }
        else if(marker.equals(mark2)){
            manager.setDestination(lo2);
            int dist = (int) manager.distanceToDestination(lastLocation);
            locationTV.setText(dist + " Meters from " + mark2.getTitle() );
        }
        else if(marker.equals(mark3)){
            manager.setDestination(lo3);
            int dist = (int) manager.distanceToDestination(lastLocation);
            locationTV.setText(dist + " Meters from " + mark3.getTitle() );
        }
        else if(marker.equals(mark4)){
            manager.setDestination(lo4);
            int dist = (int) manager.distanceToDestination(lastLocation);
            locationTV.setText(dist + " Meters from " + mark4.getTitle() );
        }
        else if(marker.equals(mark5)){
            manager.setDestination(lo5);
            int dist = (int) manager.distanceToDestination(lastLocation);
            locationTV.setText(dist + " Meters from " + mark5.getTitle() );
        }
        else if(marker.equals(myMark)){
            locationTV.setText("You Are Here!");
        }
            return false;
    }

    public void getDeviceLocation()
    {

            Task<Location> locationResult = flpa.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if(task.isSuccessful()){
                        lastLocation = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 18));
                        Toast.makeText(MapsMarkerActivity.this, "lat "+ lastLocation.getLatitude() + ", long " + lastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        myMark = mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()))
                                .title("Marker at current location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    } else {
                        Toast.makeText(MapsMarkerActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    public void checkMarker(){
        int distanceToMarker = 10;
        Location locat = new Location("");

        if(distanceToMarker<=manager.distanceToDestination(locat)){
            
        }
    }

    /*
        googleMap.addMarker(new MarkerOptions().position(currentLoc).title("Marker at Current Location of Phone"));
        //Move camera to position and zoom in
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,14));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


        //Add map marker on Pennsylvania College of Technology
        LatLng pennCollege = new LatLng(41.234665, -77.021058);
        googleMap.addMarker(new MarkerOptions().position(pennCollege)
                .title("Marker at Pennsylvania College of Technology"));
        //Move camera to position and zoom in
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pennCollege));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pennCollege,14));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        */


    //Check for location permissions
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    public void onConnected (Bundle hint) {
       // Toast.makeText(this, "Connected!", Toast.LENGTH_LONG).show();
    }

    public void onConnectionSuspended (int cause) {
        Toast.makeText(this, "Connection Suspended", Toast.LENGTH_LONG).show();
    }

    public void onConnectionFailed (ConnectionResult result) {
        Log.w(MA, "Connection Failed");
        if (result.hasResolution()){
            try{
                result.startResolutionForResult(this, REQUEST_CODE);
            } catch (IntentSender.SendIntentException sie){
                Toast.makeText(this, "Google Play services problem, exiting", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            gac.connect();
        }
    }

    protected void onStart(){
        super.onStart();
        if(gac != null)
            gac.connect();
    }




















/*
    public void updateTrip(View v) {
        String address = addressET.getText().toString();
        boolean goodGeoCoding = true;
        if (!address.equals(destinationAddress)){
            destinationAddress = address;
            Geocoder coder = new Geocoder(this);
            try{
                //geocode destination
                List<Address> addresses = coder.getFromLocationName(destinationAddress, 5);
                if(address != null){
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    Location destinationLocation = new Location("destination" );
                    destinationLocation.setLatitude(latitude);
                    destinationLocation.setLongitude(longitude);
                    manager.setDestination(destinationLocation);
                }
            } catch (IOException ioe){
                goodGeoCoding = false;
            }
        }

        FusedLocationProviderApi flpa = LocationServices.FusedLocationApi;
        Location current = flpa.getLastLocation(gac);
        if(current != null && goodGeoCoding){
            distanceTV.setText(manager.milesToDestination(current));
            timeLeftTV.setText(manager.timeToDestination(current));
        }
    }
*/




}
