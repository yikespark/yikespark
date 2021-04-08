package com.yikes.park.menu.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.menu.profile.data.UserInformation;

public class externalProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_profile);

        String userId = getIntent().getStringExtra("userId");

        Log.d("TAG", userId);

        Task<DataSnapshot> dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            UserInformation userInformation = task.getResult().getValue(UserInformation.class);
                            Log.d("userInformation", userInformation.toString());

                            TextView name = findViewById(R.id.profile_field1);
                            name.setText(userInformation.getUsername());
                            TextView email = findViewById(R.id.email);
                            email.setText(userInformation.getEmail());
                            TextView desc = findViewById(R.id.desc);
                            desc.setText(userInformation.getDesc());
                            RequestOptions options = new RequestOptions();
                            options.circleCrop();
                            Glide.with(getBaseContext()).load(userInformation.getAvatar()).apply(options).into((ImageView) findViewById(R.id.profile_user_image));

                        }
                    }
                });

        Log.d("TAG", dbUsers.toString());
    }
}