package com.jiat.sarongstyles.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstyles.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private GoogleMap map;
    private Location currentLocation;
    private LatLng getLatLng;
    private FirebaseFirestore firestore;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        firestore = FirebaseFirestore.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (intent.hasExtra("OrderId")) {
            orderId = (String) intent.getSerializableExtra("OrderId");
            //Toast.makeText(this, orderId, Toast.LENGTH_SHORT).show();
        }

        Button setLocationButton = findViewById(R.id.locationSetButton);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(MapActivity.this, "Current location setted up", Toast.LENGTH_SHORT).show();

                String newLocation = String.valueOf(getLatLng.latitude+", "+getLatLng.longitude);

                DocumentReference orderDocument = firestore.collection("orders").document(orderId);

                orderDocument
                        .update("location", newLocation)
                        .addOnSuccessListener(aVoid -> {
                            // Update successful
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                        });

                Intent intent = new Intent(MapActivity.this, OrderDetailsActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map=googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        if(checkPermission()){
            map.setMyLocationEnabled(true);
            getLastLocation();
        }else{
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private boolean checkPermission(){
        boolean permission = false;

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        ){
            permission=true;
        }
        return permission;
    }

    private void getLastLocation() {
        if (checkPermission()) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        getLatLng = latLng;
                        map.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Snackbar.make(findViewById(R.id.container),"Location permission denied",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }
}