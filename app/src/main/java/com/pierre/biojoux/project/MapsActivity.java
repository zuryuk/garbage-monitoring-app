package com.pierre.biojoux.project;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import GarbageBin.GarbageBin;
import GarbageBin.GarbageHandler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Direction.DirectionFinder;
import Direction.DirectionFinderListener;
import Direction.Route;

/*
https://github.com/hiepxuan2008/GoogleMapDirectionSimple/
http://www.codeproject.com/Articles/1113633/Google-Map-Tutorial-in-Android-Studio-How-to-get-c
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        DirectionFinderListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button buttonGoTo;
    private Button buttonBack;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private String origin;
    private LatLng latLng;
    private LatLng latLngGarbageBin;
    private double longitude;
    private double latitude;
    private int ID=1;
    private String doIt;
    private ArrayList<GarbageBin> binList = null;
    private GarbageBin Bin = null;
    private GarbageHandler db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        binList = (ArrayList<GarbageBin>) getIntent().getExtras().getSerializable("binList");
        db = new GarbageHandler(this);
        Bin = (GarbageBin) getIntent().getExtras().getSerializable("Bin");
        Bin = binList.get(1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        doIt = getIntent().getExtras().getString("doIt");

        buttonGoTo = (Button) findViewById(R.id.buttonStart);
        buttonGoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quit();
            }
        });
    }

    private void quit() {
        finish();
    }

    private void sendRequest() {
        if(doIt.equals("OneGarbageBin")){
            String destination = binList.get(ID).getCoord();
            try {
                new DirectionFinder(this, origin, destination).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        setMarkers(mMap);
        LatLng myLoc = new LatLng(56.17, 10.12);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);
                String IdMarker = marker.getId().replace("m", "");
                int id = Integer.parseInt(IdMarker);
                System.out.println(id);
                intent.putExtra("Bin", id);
                startActivity(intent);

            }
        });
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        //move map camera
        if(doIt.equals("ManyGarbageBins")){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(8));
        }else{
            latLngGarbageBin = new LatLng(Bin.getLat(), Bin.getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngGarbageBin));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(8));
        }

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation,6));
            ((TextView) findViewById(R.id.textViewTime)).setText("Time: "+route.duration.text);
            ((TextView) findViewById(R.id.textViewDistance)).setText("Distance: "+route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
    public void setMarkers(GoogleMap mMap){
        //Place markers of garbage bins
        if(doIt.equals("ManyGarbageBins")){
            for (int i = 0; i < binList.size(); i++) {
                latLngGarbageBin = new LatLng(binList.get(i).getLat(), binList.get(i).getLon());
                if (binList.get(i).getStatus().equals("Full")) {
                    mMap.addMarker(new MarkerOptions()
                            .position(latLngGarbageBin)
                            .title("Garbage Bin:" + binList.get(i).getID())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.redgarbage))
                    );}
                else if (binList.get(i).getStatus().equals("Medium")) {
                    mMap.addMarker(new MarkerOptions()
                            .position(latLngGarbageBin)
                            .title("Garbage Bin:" + binList.get(i).getID())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowgarbage))
                    );
                }
                else{
                    mMap.addMarker(new MarkerOptions()
                            .position(latLngGarbageBin)
                            .title("Garbage Bin:" + binList.get(i).getID())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.greengarbage))
                    );
                }
            }

        }


        //Place marker of the garbage bin chosen
        if(doIt.equals("OneGarbageBin")){
            latLngGarbageBin = new LatLng(Bin.getLat(), Bin.getLon());
            if (Bin.getStatus().equals("Full")) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLngGarbageBin)
                        .title("Garbage Bin:" + Bin.getID())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.redgarbage))
                );}
            else if (Bin.getStatus().equals("Medium")) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLngGarbageBin)
                        .title("Garbage Bin:" + Bin.getID())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowgarbage))
                );
            }
            else{
                mMap.addMarker(new MarkerOptions()
                        .position(latLngGarbageBin)
                        .title("Garbage Bin:" + Bin.getID())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.greengarbage))
                );
            }
        }
    }
}