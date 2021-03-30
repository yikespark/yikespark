package com.yikes.park.menu.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.menu.map.Objects.YikeSpot;
import com.yikes.park.menu.profile.data.UserInformation;

public class YikesSpotActivity extends AppCompatActivity {

    private YikeSpot yikeSpot;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yikes_spot);

        Gson gson = new Gson();
        String jsonMarker = getIntent().getStringExtra("yikesSpot");
        yikeSpot = gson.fromJson(jsonMarker, YikeSpot.class);

        setUsernameWithId(yikeSpot.getCreator());

        TextView name = findViewById(R.id.name);
        name.setText(yikeSpot.getName());

        TextView lat = findViewById(R.id.lat);
        lat.setText(Double.toString(yikeSpot.getLat()));

        TextView lon = findViewById(R.id.lon);
        lon.setText(Double.toString(yikeSpot.getLon()));

        TextView id = findViewById(R.id.id);
        id.setText(yikeSpot.getId());

        Glide.with(this).load(yikeSpot.getPicture()).into((ImageView) findViewById(R.id.imageView));
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