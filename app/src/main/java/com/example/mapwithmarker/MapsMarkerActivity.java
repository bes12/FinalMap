package com.example.mapwithmarker;

import android.content.Intent;
import android.content.IntentSender;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class MapsMarkerActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String MA = "MapsMarkerActivity";
    private final static int REQUEST_CODE = 100;

    private GoogleApiClient gac;
    private TravelManager manager;
    private EditText addressET;
    private TextView distanceTV;
    private TextView timeLeftTV;
    private String destinationAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        manager = new TravelManager();
        addressET = (EditText) findViewById(R.id.destination_et);
        distanceTV = (TextView) findViewById(R.id.distance_tv);
        timeLeftTV = (TextView) findViewById(R.id.time_left_tv);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Add map marker on Pennsylvania College of Technology
        LatLng penncollege = new LatLng(41.234665, -77.021058);
        googleMap.addMarker(new MarkerOptions().position(penncollege)
                .title("Marker at Pennsylvania College of Technology"));
        //Move camera to position and zoom in
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(penncollege));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(penncollege,14));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
//left off on
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

    public void onConnected (Bundle hint) {
        Log.w(MA, "Connected");
    }

    public void onConnectionSuspended (int cause) {
        Log.w(MA, "Connection Suspended");
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
}
