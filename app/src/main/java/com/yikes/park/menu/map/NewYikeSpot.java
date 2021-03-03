package com.yikes.park.menu.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.menu.MainActivity;
import com.yikes.park.menu.map.coords.YikeSpot;
import com.yikes.park.menu.profile.data.UserInformation;

import java.util.ArrayList;

public class NewYikeSpot extends AppCompatActivity {
    DatabaseReference dbSpot;
    protected ArrayList<YikeSpot> YikeSpots;
    private UserInformation myUser;
    private Button add_spot_img_btn;
    private ImageView spot_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbSpot = FirebaseDatabase.getInstance().getReference().child("yikeSpots");
        YikeSpots = new ArrayList<YikeSpot>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspot);

        /** Loads user information */
        Gson gson = new Gson();
        String json = MainActivity.sharedPref.getString(MainActivity.MY_USER_KEY,null);
        myUser = gson.fromJson(json, UserInformation.class);
        waitForDataFetch();


        spot_img = findViewById(R.id.image_yikespot);

        add_spot_img_btn = findViewById(R.id.image_yikespot_btn);
        add_spot_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFromGalery();
            }
        });


        TextView newSpotName = findViewById(R.id.newSpotName);

        final Button button = findViewById(R.id.newSpot);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double myLatitude = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LATITUDE_KEY,"0"));
                double myLongitude = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LONGITUDE_KEY,"0"));

                YikeSpot yikeSpot = new YikeSpot(newSpotName.getText().toString(), myLatitude, myLongitude, myUser.getId());
                YikeSpots.add(yikeSpot);
                dbSpot.setValue(YikeSpots);
            }
        });

        dbSpot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("logTest ", "" + dataSnapshot.getChildrenCount());
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

    /** User profile related stuff */
    private void waitForDataFetch() {
        if (myUser != null) {
            // MyUser has data and can be used
        } else {
            waitForDataFetch();
        }
    }

    /** Image related stuff */
    private void loadImageFromGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        if(requestCode == 10 && resultCode == RESULT_OK){
            Uri uri;
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), uri);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else if (requestCode == 20 && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
        }
        if(bitmap != null){
            spot_img.setImageBitmap(bitmap);
        }
    }
}