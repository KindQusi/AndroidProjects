package com.mkuskowski.mapapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private boolean requestingLocationUpdates = true;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private Location oldLocation;

    //private MarkerOptions UserMarker;
    private String[] requiredPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //For RabbitMQ Client
    private RabbitMQ_Client client;

    private Map<String, Marker> localisationsToMark;
    private Stack nickStack = new Stack<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.println(Log.INFO, "@@@DEMOAPPLOG", "ON CREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //---------------------------------------------------------------------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //UserMarker = new MarkerOptions();
        //UserMarker.title("Our location");
        //Initialize our Map of Users
        localisationsToMark = new HashMap<>();
        //Settings for requests
        //---------------------------------------------------------------------
        createLocationRequest();
        //Request and set mark on our location
        //---------------------------------------------------------------------
        createLocationCallback();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Creating Rabbit MQ Client
        client = new RabbitMQ_Client(this, getString(R.string.rabbitmq_api));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Log.println(Log.INFO,"@@@DEMOAPPLOG","MAP READY");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        //Log.println(Log.INFO,"@@@DEMOAPPLOG","ON RESUME");
        super.onResume();
        if (requestingLocationUpdates) {
            Log.println(Log.INFO, "@@@DEMOAPPLOG", "START LOCATION UPDATE");
            startLocationUpdates();
        }
    }

    // Settings for our request like refresh time
    //-----------------------------------------------------------------------------------------------------
    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Method which we use to decide what we want to do which our localisation result
    //-----------------------------------------------------------------------------------------------------

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.println(Log.INFO, "@@@DEMOAPPLOG", "CALLBACK LOCATION RESULT NULL");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Getting our location
                    //---------------------------------------------------------------------
                    LatLng localLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    // Creating location
                    //---------------------------------------------------------------------
                    Location newLocation = new Location("New Point");
                    newLocation.setLatitude(localLatLng.latitude);
                    newLocation.setLongitude(localLatLng.longitude);
                    // Check if it is new location
                    // If we set the point for the first time
                    //---------------------------------------------------------------------
                    if (oldLocation == null) {
                        oldLocation = newLocation;
                        String latlngString = localLatLng.latitude + "," + localLatLng.longitude;
                        // Creating new string to with nick and LatLng
                        //---------------------------------------------------------------------
                        String nick_LatLng = getString(R.string.Nick) + "," + latlngString;
                        //Log.println(Log.INFO,"@@@DEMOAPPLOG",nick_LatLng);
                        // To send our loc to queue
                        //---------------------------------------------------------------------
                        client.SendMessage( nick_LatLng );
                        //MarkerManager(nick_LatLng);
                    } else {
                        // If we moved only
                        //---------------------------------------------------------------------
                        if (newLocation.distanceTo(oldLocation) > 0.1) {
                            oldLocation = newLocation;
                            String latlngString = localLatLng.latitude + "," + localLatLng.longitude;
                            // Creating new string to with nick and LatLng
                            //---------------------------------------------------------------------
                            String nick_LatLng = getString(R.string.Nick) + "," + latlngString;
                            //Log.println(Log.INFO,"@@@DEMOAPPLOG",nick_LatLng);

                            // To send our loc to queue
                            //---------------------------------------------------------------------
                            client.SendMessage( nick_LatLng );
                            //MarkerManager(nick_LatLng);
                        } else {
                            Log.println(Log.INFO, "@@@DEMOAPPLOG", "Our position didn't change");
                        }
                    }
                }

                // Checking our stack
                //---------------------------------------------------------------------
                UpdateMap();
            }
        };
    }

    // Method which we use add our Message to Stack from RabbitMQ
    //-----------------------------------------------------------------------------------------------------
    public void PushToStack(String newData)
    {
        nickStack.push(newData);
    }

    // Method which we use to set marker on map and other things createLocationCallback()
    // Its taking data from our stack nickStack
    //-----------------------------------------------------------------------------------------------------

    public void UpdateMap()
    {
        // While we have smth in stack
        //---------------------------------------------------------------------
        while(!nickStack.empty())
        {
            String newData = (String) nickStack.pop();

            String[] nick_LatLng = newData.split(",");
            String nick = nick_LatLng[0];
            LatLng LatLng = new LatLng(Double.parseDouble(nick_LatLng[1]),Double.parseDouble(nick_LatLng[2]));

            // If its new one or old one
            //---------------------------------------------------------------------

            if (localisationsToMark.containsKey(nick)) {
                // Only updating marker instead of creating new
                //---------------------------------------------------------------------
                Log.println(Log.INFO, "@@@DEMOAPPLOG", "MarkerManager() => Otrzymano dane od :" + nick + " oraz polozenie: " + LatLng + " Aktualizacja");
                localisationsToMark.get(nick).setPosition(LatLng);
            }
            else {
                Log.println(Log.INFO, "@@@DEMOAPPLOG", "MarkerManager() => Otrzymano dane od :" + nick + " oraz polozenie: " + LatLng + " To nowy nick");
                // Setting new marker
                //---------------------------------------------------------------------
                MarkerOptions newMarkerOptions = new MarkerOptions();
                newMarkerOptions.position(LatLng);
                newMarkerOptions.title(nick + "Position: " + LatLng);
                // Creating new marker and storing it to update it later
                //---------------------------------------------------------------------
                Marker newMarker = mMap.addMarker(newMarkerOptions);
                localisationsToMark.put(nick, newMarker);
            }
        }
    }


    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates()
    {
        int afl = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int acl = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        //If we get permissions we can try
        //---------------------------------------------------------------------
        if(afl != PackageManager.PERMISSION_GRANTED && acl != PackageManager.PERMISSION_GRANTED) {
            Log.println(Log.INFO,"@@@DEMOAPPLOG","PERMISSIONS NOT GRANTED");
            Log.println(Log.INFO,"@@@DEMOAPPLOG-FINELOCATION", Integer.toString(afl));
            Log.println(Log.INFO,"@@@DEMOAPPLOG-COARSELOCATION", Integer.toString(acl));
            ActivityCompat.requestPermissions(this, requiredPermissions,0);
            //  ActivityCompat#requestPermissions
            //  here to request the missing permissions, and then overriding
            //  to handle the case where the user grants the permission. See the documentation
            //  for ActivityCompat#requestPermissions for more details.
            //---------------------------------------------------------------------

            //public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
            // return;
        }
        Log.println(Log.INFO,"@@@DEMOAPPLOG","START LOCATION REQUEST");
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        Log.println(Log.INFO,"@@@DEMOAPPLOG","LOCATION REQUESTED");
    }
}