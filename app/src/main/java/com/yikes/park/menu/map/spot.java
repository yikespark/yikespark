package com.yikes.park.menu.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yikes.park.R;
import com.yikes.park.menu.map.coords.YikeSpot;

import java.util.ArrayList;

public class spot extends AppCompatActivity {
    DatabaseReference dbSpot;
    protected ArrayList<YikeSpot> YikeSpots;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbSpot = FirebaseDatabase.getInstance().getReference().child("yikeSpots");
        YikeSpots = new ArrayList<YikeSpot>();
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);

        TextView newSpotName = findViewById(R.id.newSpotName);
        TextView newSpotAuthor = findViewById(R.id.newSpotAuthor);

//        YikeSpot yikeSpot2 = new YikeSpot("EdgeLord House" ,41.43303732514512, 2.1655388137411564, "El Admin");
//        YikeSpots.add(yikeSpot2);
//        dbSpot.setValue(YikeSpots);

        final Button button = findViewById(R.id.newSpot);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double myLatitude = Double.parseDouble(sharedPref.getString("latitude","0"));
                double myLongitude = Double.parseDouble(sharedPref.getString("longitude","0"));

                YikeSpot yikeSpot = new YikeSpot(newSpotName.getText().toString(),myLatitude, myLongitude, newSpotAuthor.getText().toString());
                YikeSpots.add(yikeSpot);
                dbSpot.setValue(YikeSpots);
            }
        });

        dbSpot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("logTest ", ""+dataSnapshot.getChildrenCount());

                YikeSpots.clear();


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    YikeSpot yikeSpot = postSnapshot.getValue(YikeSpot.class);
                    YikeSpots.add(yikeSpot);
                    
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