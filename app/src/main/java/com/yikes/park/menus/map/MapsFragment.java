package com.yikes.park.menus.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yikes.park.CL.SkatePark;
import com.yikes.park.R;
import java.util.ArrayList;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import org.json.JSONObject;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;


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
        SkateParks = new ArrayList<SkatePark>();
        mMap = googleMap;
        db = FirebaseDatabase.getInstance().getReference().child("skateParks");


        SkatePark skatePark = new SkatePark("Favencia",41.443010789187205, 2.1714888401371972, "rampa");
//        SkatePark skatePark2 = new SkatePark("Zona franca",41.356810315073126, 2.1411484268477743);
//        SkateParks.add(skatePark);
        SkateParks.add(skatePark);

        db.setValue(SkateParks);


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
                   // LatLng park = new LatLng(41.4035806, 2.17428);

                    mMap.addMarker(new MarkerOptions().position(park).title("Marker in Skate Park "+ skatePark.getParkName()).icon((BitmapDescriptorFactory.fromResource(R.drawable.marker_hotspot))));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(park));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(park, 15.3432f));
                }
                db.setValue(SkateParks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "EFailed to read value.", error.toException());
            }
        });

//        mMap = googleMap;
//
//        LatLng familia = new LatLng(41.4035806, 2.17428);
//        mMap.addMarker(new MarkerOptions().position(familia).title("Marker in Sagrada Familia"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(familia));
    }
}