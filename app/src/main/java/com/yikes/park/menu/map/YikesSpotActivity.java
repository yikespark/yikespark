package com.yikes.park.menu.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.menu.MainActivity;
import com.yikes.park.menu.map.Objects.YikeSpot;
import com.yikes.park.menu.profile.data.UserInformation;

public class YikesSpotActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference dbSpot;

    private YikeSpot yikeSpot;

    private TextView name;
    private TextView latlon;
    private TextView id;

    private Button removeSpotBtn;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yikes_spot);

        /* Getting data */
        Gson gson = new Gson();
        String jsonMarker = getIntent().getStringExtra("yikesSpot");
        yikeSpot = gson.fromJson(jsonMarker, YikeSpot.class);

        /* Current user data */
        dbSpot = FirebaseDatabase.getInstance().getReference().child("YikeSpots");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        /* Setting data */
        setUsernameWithId(yikeSpot.getCreator());

        name = findViewById(R.id.name);
        name.setText(yikeSpot.getName());


        latlon = findViewById(R.id.latlong);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            double curUserLat = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LATITUDE_KEY, "0"));
            double curUserLon = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LONGITUDE_KEY, "0"));
            String mapUrl = "<a href='https://www.google.com/maps/dir/?api=1&origin=" + curUserLat + "," + curUserLon + "&destination=" + yikeSpot.getLat() + "," + yikeSpot.getLon() + "'>" + yikeSpot.getLat() + ", " + yikeSpot.getLon() + "</a>";
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
            latlon.setText(yikeSpot.getLat() + ", " + yikeSpot.getLon());
        }

        id = findViewById(R.id.id);
        id.setText("ID: " + yikeSpot.getId());

        Glide.with(this).load(yikeSpot.getPicture()).into((ImageView) findViewById(R.id.imageView));

        /* Allow to remove the Spot if the visitor is the owner */
        removeSpotBtn = findViewById(R.id.removeSpot);
        removeSpotBtn.setBackgroundColor(getResources().getColor(R.color.red_700));
        if (yikeSpot.getCreator().equals(user.getUid())) {
            removeSpotBtn.setVisibility(View.VISIBLE);
            removeSpotBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbSpot.child(yikeSpot.getId()).removeValue();
                    finish();
                }
            });


        } else {
            removeSpotBtn.setVisibility(View.GONE);
        }



    }

    public void onClick(View view) {
        Intent intent = new Intent(this, externalProfile.class);
        intent.putExtra("userId", yikeSpot.getCreator());
        startActivity(intent);
    }

    public void setUsernameWithId(String userId) {
        Task<DataSnapshot> dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    UserInformation userInformation = task.getResult().getValue(UserInformation.class);
                    Log.d("userInformation", userInformation.toString());
                    TextView creator = findViewById(R.id.creator);
                    creator.setText(userInformation.getUsername());
                }
            }
        });
        Log.d("TAG", dbUsers.toString());
    }
}