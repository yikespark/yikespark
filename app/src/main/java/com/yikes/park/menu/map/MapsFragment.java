package com.yikes.park.menu.map;

//MainActivity.sharedPref.edit().putString(MainActivity.LATITUDE_KEY, String.valueOf(location.getLatitude())).apply();
//MainActivity.sharedPref.edit().putString(MainActivity.LONGITUDE_KEY, String.valueOf(location.getLongitude())).apply();
//LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.BitmapDescriptor;
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
import com.yikes.park.menu.MainActivity;
import com.yikes.park.menu.map.Objects.SkatePark;
import com.yikes.park.menu.map.Objects.YikeSpot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import mumayank.com.airlocationlibrary.AirLocation;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    Snackbar permissionsWarning;

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

        /** Checks if user has the GPS disabled, if so, a message is shown */
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsWarning = Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.map_warning_permissions_disabled, Snackbar.LENGTH_INDEFINITE);
            permissionsWarning.setAction(R.string.map_warning_permissions_disabled_close, new dissmissWarning());

            View view = permissionsWarning.getView();
            // FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 100, 0, 0);
            //params.gravity = Gravity.BOTTOM;
            view.setLayoutParams(params);
            permissionsWarning.show();
        }

        /** Sets the current user position */
        setUpMyLocation();

        // Buttons
        FloatingActionButton addNewSpotBtn = rootView.findViewById(R.id.plus);
        addNewSpotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });

        FloatingActionButton goToMyLocBtn = rootView.findViewById(R.id.centerCamera);
        goToMyLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationCoordinates, 15.3432f));
            }
        });

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
        //markerName = mMap.addMarker(new MarkerOptions().position(myLocation).title("Your Location").icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_mylocation))));
        markerName = mMap.addMarker(new MarkerOptions().position(myLocation).title(getString(R.string.map_your_location)).icon((bitmapDescriptorFromVector(getActivity(), R.drawable.ic_skater_multilayer))));
    }


    /** This method is needed in order to update the information when the screen is resumed */
    public void setUpMyLocation() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        AirLocation mAirLocation = new AirLocation(requireActivity(), new AirLocation.Callback() {
            @Override
            public void onSuccess(@NotNull ArrayList<Location> arrayList) {
                Log.d("AirLocation", "onSucess. Location details: " + arrayList);
                myLocation = new Location(arrayList.get(0));

                if (!new LatLng(myLocation.getLatitude(), myLocation.getLongitude()).equals(myLocationCoordinates)){
                    MainActivity.sharedPref.edit().putString(MainActivity.LATITUDE_KEY, String.valueOf(myLocation.getLatitude())).apply();
                    MainActivity.sharedPref.edit().putString(MainActivity.LONGITUDE_KEY, String.valueOf(myLocation.getLongitude())).apply();
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
        mAirLocation.start();
    }


    public void setUpMap() {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
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
                                    .title("SP: " + skatePark.getName())
                                    .snippet(getString(R.string.map_mark_snippet))
                                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_skatepark_multilayer))
                                    //.icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_ramp)))
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
                                    .title("YS: " + yikeSpot.getName())
                                    .snippet(getString(R.string.map_mark_snippet))
                                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_yikespot_multilayer))
                                    //.icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_hotspot)))
                    )
                            .setTag(yikeSpot);

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
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
                            }
                            if (marker.getTitle().startsWith("SP")) {
                                Gson gsonPark = new Gson();
                                SkatePark parkFromMarker = (SkatePark) marker.getTag();
                                String jsonYikes = gsonPark.toJson(parkFromMarker);
                                Intent intent = new Intent(getContext(), SkateParkActivity.class);
                                intent.putExtra("marker", myJson);
                                intent.putExtra("skatePark", jsonYikes);
                                startActivity(intent);
                            }
                        }});

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "EFailed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    public class dissmissWarning implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            permissionsWarning.dismiss();
        }
    }

    /** TODO: Watchout this code needs some testing! Not sure if it works properly!
     * This part of the code is called when you come back to the map from another screen (Eg: When you remove a YikeSpot from YikeSpotActivity) */
    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) {
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        } else {
            mMap.clear();
            setMyLocationMarker(myLocationCoordinates);
            setUpMap();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}