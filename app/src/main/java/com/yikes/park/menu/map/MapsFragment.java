package com.yikes.park.menu.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yikes.park.menu.map.coords.SkatePark;
import com.yikes.park.R;
import java.util.ArrayList;
import com.google.maps.android.data.geojson.GeoJsonLayer;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;


    DatabaseReference db;
    protected ArrayList<SkatePark> SkateParks;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // http://geojson.io/
        // https://github.com/googlemaps/android-maps-utils

        GeoJsonLayer test;

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return rootView;
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Marker[] markerName = {null};
        final Boolean[] firstime = {true};
        SkateParks = new ArrayList<SkatePark>();
        mMap = googleMap;
        db = FirebaseDatabase.getInstance().getReference().child("skateParks");

//        SkatePark skatePark = new SkatePark("Favencia",41.443010789187205, 2.1714888401371972, "hotspot");
//        SkatePark skatePark2 = new SkatePark("Zona franca",41.356810315073126, 2.1411484268477743,"marker_ramp");
//        SkateParks.add(skatePark);
//        SkateParks.add(skatePark2);
//        db.setValue(SkateParks);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("logTest ", ""+dataSnapshot.getChildrenCount());

                SkateParks.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    SkatePark skatePark = postSnapshot.getValue(SkatePark.class);
                    SkateParks.add(skatePark);
//                    Log.i("logTest",skatePark.getParkName());
                    LatLng park = new LatLng(skatePark.getParkLat(), skatePark.getParkLong());

                    int markerIcon = getResources().getIdentifier(skatePark.getParkType(), "drawable", getActivity().getPackageName());
                    mMap.addMarker(new MarkerOptions().position(park).title("Marker in Skate Park "+ skatePark.getParkName()).icon((BitmapDescriptorFactory.fromResource(markerIcon))));

                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(park, 15.3432f));

                }
                db.setValue(SkateParks);

                // START Get my current location
                locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        if (firstime[0] == true){
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.3432f));
                            firstime[0] = false;
                        }
                        if (markerName[0] != null) {
                            markerName[0].remove();
                            markerName[0] = null;
                        }
                        if (markerName[0] == null) {
                            markerName[0] = mMap.addMarker(new MarkerOptions().position(myLocation).title("Your Location").icon((BitmapDescriptorFactory.fromResource(R.drawable.hotspot))));
                        }

                    }
                });
                // END Get my current location
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "EFailed to read value.", error.toException());
            }
        });
    }
}