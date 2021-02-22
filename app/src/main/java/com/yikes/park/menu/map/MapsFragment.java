package com.yikes.park.menu.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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

import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.MapStyleOptions;
import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.yikes.park.R;
import com.yikes.park.menu.map.coords.SkatePark;
import com.yikes.park.menu.map.coords.YikeSpot;

import java.util.ArrayList;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;

    DatabaseReference dbPark;
    DatabaseReference dbSpot;
    protected ArrayList<SkatePark> SkateParks;
    protected ArrayList<YikeSpot> YikeSpots;

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
        final Boolean[] isFirstTime = {true};
        SkateParks = new ArrayList<SkatePark>();
        YikeSpots = new ArrayList<YikeSpot>();
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }
        dbPark = FirebaseDatabase.getInstance().getReference().child("skateParks");
        dbSpot = FirebaseDatabase.getInstance().getReference().child("yikeSpots");

        // START Get my current location
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                if (isFirstTime[0]) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.3432f));
                    isFirstTime[0] = false;
                }
                if (markerName[0] != null) {
                    markerName[0].remove();
                    markerName[0] = null;
                }
                if (markerName[0] == null) {
                    markerName[0] = mMap.addMarker(new MarkerOptions().position(myLocation).title("Your Location").icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_skater))));
                }

            }
        });
        // END Get my current location

        ////////////////////// Firebase Skateparks //////////////////////
        dbPark.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("logTest ", ""+dataSnapshot.getChildrenCount());

                SkateParks.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    SkatePark skatePark = postSnapshot.getValue(SkatePark.class);
                    SkateParks.add(skatePark);
                    LatLng park = new LatLng(skatePark.getParkLat(), skatePark.getParkLong());

                    int markerIcon = getResources().getIdentifier(skatePark.getParkType(), "drawable", getActivity().getPackageName());
                    mMap.addMarker(new MarkerOptions().position(park).title("Skate Park "+ skatePark.getParkName()).icon((BitmapDescriptorFactory.fromResource(markerIcon))));

                }
                dbPark.setValue(SkateParks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "EFailed to read value.", error.toException());
            }
        });

        ////////////////////// Firebase YikeSpots //////////////////////
        dbSpot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("logTest ", ""+dataSnapshot.getChildrenCount());

                YikeSpots.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    YikeSpot yikeSpot = postSnapshot.getValue(YikeSpot.class);
                    YikeSpots.add(yikeSpot);
                    LatLng spot = new LatLng(yikeSpot.getSpotLat(), yikeSpot.getSpotLong());
                    mMap.addMarker(new MarkerOptions().position(spot).title("YikeSpot "+ yikeSpot.getSpotName()).icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_hotspot))));

                }
                dbSpot.setValue(YikeSpots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "EFailed to read value.", error.toException());
            }
        });
    }
}