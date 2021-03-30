package com.yikes.park.menu.map;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.libraries.maps.model.Marker;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.menu.map.Objects.SkatePark;

public class SkateParkActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skate_park);

        Gson gson = new Gson();

        String jsonMarker = getIntent().getStringExtra("marker");
        simpleMarker marker = gson.fromJson(jsonMarker, simpleMarker.class);

        String jsonPark = getIntent().getStringExtra("skatePark");
        SkatePark skatePark = gson.fromJson(jsonPark, SkatePark.class);

        TextView name = findViewById(R.id.name);
        name.setText(skatePark.getName());
        TextView lat = findViewById(R.id.lat);
        lat.setText(Double.toString(skatePark.getLat()));
        TextView lon = findViewById(R.id.lon);
        lon.setText(Double.toString(skatePark.getLon()));
    }
}