package com.yikes.park.menu.map;

//MainActivity.sharedPref.edit().putString(MainActivity.LATITUDE_KEY, String.valueOf(location.getLatitude())).apply();
//MainActivity.sharedPref.edit().putString(MainActivity.LONGITUDE_KEY, String.valueOf(location.getLongitude())).apply();
//LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.MapStyleOptions;
import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.menu.map.Objects.SkatePark;
import com.yikes.park.menu.map.Objects.YikeSpot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import mumayank.com.airlocationlibrary.AirLocation;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    DatabaseReference dbPark;
    DatabaseReference dbSpot;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker markerName = null;
    private Boolean isFirstTime = true;
    private Location myLocation;
    private LatLng myLocationCoordinates;
    private RequestQueue mQueue;
    LatLng myDestinationCoordinates;

    public MapsFragment() {
        /* Required empty public constructor */
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        AirLocation mAirLocation = new AirLocation(requireActivity(), new AirLocation.Callback() {
            @Override
            public void onSuccess(@NotNull ArrayList<Location> arrayList) {
                Log.d("AirLocation", "onSucess. Location details: " + arrayList);
                myLocation = new Location(arrayList.get(0));

                if (!new LatLng(myLocation.getLatitude(), myLocation.getLongitude()).equals(myLocationCoordinates)){
                    myLocationCoordinates = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    setMyLocationMarker(myLocationCoordinates);
                }
            }

            @Override
            public void onFailure(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                Log.d("AirLocation", "onFailure: " + locationFailedEnum);
            }
        }, false,1000, "Please enable location permissions for this app to work.");

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        /** Checks if user has the GPS disabled, if so, a message is shown */
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "Your GPS is disabled", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("ENABLE", new gpsActivation());

            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.CENTER;
            view.setLayoutParams(params);
            snack.show();
        }

        FloatingActionButton button = rootView.findViewById(R.id.plus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });

        mAirLocation.start();

        return rootView;
    }

    public void openNewActivity() {
        Intent intent = new Intent(getContext(), NewYikeSpot.class);
        startActivity(intent);
    }

    public void setMyLocationMarker(LatLng myLocation) {
        if (isFirstTime) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.3432f));
            isFirstTime = false;
        }
        if (markerName != null) {
            markerName.remove();
            markerName = null;
        }

        markerName = mMap.addMarker(new MarkerOptions().position(myLocation).title("Your Location").icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_mylocation))));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final double[] currentLocation = {};
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
        dbPark = FirebaseDatabase.getInstance().getReference().child("SkateParks");
        dbSpot = FirebaseDatabase.getInstance().getReference().child("YikeSpots");

        ////////////////////// Firebase Skateparks //////////////////////
        dbPark.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("logTest ", "" + dataSnapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SkatePark skatePark = postSnapshot.getValue(SkatePark.class);
                    LatLng park = new LatLng(skatePark.getLat(), skatePark.getLon());

                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(park)
                                    .title("SP" + skatePark.getId())
                                    .icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_ramp)))
                    )
                            .setTag(skatePark);
                }
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
                Log.i("logTest ", "" + dataSnapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    YikeSpot yikeSpot = postSnapshot.getValue(YikeSpot.class);
                    LatLng spot = new LatLng(yikeSpot.getLat(), yikeSpot.getLon());

                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(spot)
                                    .title("YS" + yikeSpot.getId())
                                    .icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_hotspot)))
                    )
                            .setTag(yikeSpot);

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Log.d("onMarkerClick", marker.getTitle());

                            Gson gson = new Gson();
                            simpleMarker simpleMarker = new simpleMarker(
                                    marker.getTitle(),
                                    marker.getPosition().latitude,
                                    marker.getPosition().longitude
                            );
                            String myJson = gson.toJson(simpleMarker);

                            if (marker.getTitle().startsWith("YS")) {
                                Gson gsonYikes = new Gson();
                                YikeSpot spotFromMarker = (YikeSpot) marker.getTag();
                                String jsonYikes = gsonYikes.toJson(spotFromMarker);
                                Intent intent = new Intent(getContext(), YikesSpotActivity.class);
                                intent.putExtra("marker", myJson);
                                intent.putExtra("yikesSpot", jsonYikes);
                                startActivity(intent);
                                return true;
                            }
                            if (marker.getTitle().startsWith("SP")) {
                                Gson gsonPark = new Gson();
                                SkatePark parkFromMarker = (SkatePark) marker.getTag();
                                String jsonYikes = gsonPark.toJson(parkFromMarker);
                                Intent intent = new Intent(getContext(), SkateParkActivity.class);
                                intent.putExtra("marker", myJson);
                                intent.putExtra("skatePark", jsonYikes);
                                startActivity(intent);
                                return true;
                            }
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "EFailed to read value.", error.toException());
            }
        });
    }

    public class gpsActivation implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }
    }
}