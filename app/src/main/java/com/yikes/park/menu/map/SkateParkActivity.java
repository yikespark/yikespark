package com.yikes.park.menu.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.libraries.maps.model.Marker;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.menu.MainActivity;
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


        TextView latlon = findViewById(R.id.splatlong);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            double curUserLat = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LATITUDE_KEY, "0"));
            double curUserLon = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LONGITUDE_KEY, "0"));
            String mapUrl = "<a href='https://www.google.com/maps/dir/?api=1&origin=" + curUserLat + "," + curUserLon + "&destination=" + skatePark.getLat() + "," + skatePark.getLon() + "'>" + skatePark.getLat() + ", " + skatePark.getLon() + "</a>";
            // Removes underline from HTML Text
            Spannable s = (Spannable) Html.fromHtml(mapUrl);
            for (URLSpan u: s.getSpans(0, s.length(), URLSpan.class)) {
                s.setSpan(new UnderlineSpan() {
                    public void updateDrawState(TextPaint tp) {
                        tp.setUnderlineText(false);
                    }
                }, s.getSpanStart(u), s.getSpanEnd(u), 0);
            }
            latlon.setText(s);
            latlon.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            latlon.setText(skatePark.getLat() + ", " + skatePark.getLon());
        }

        RatingBar spRating = findViewById(R.id.rating);
        spRating.setRating(Integer.parseInt(skatePark.getType()));
    }
}