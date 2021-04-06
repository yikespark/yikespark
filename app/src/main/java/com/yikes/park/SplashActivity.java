package com.yikes.park;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yikes.park.menu.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Handler handler = new Handler();
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    startActivity(mainActivityIntent);
                } else {
                    startActivity(loginActivityIntent);
                }
                finish();
            }
        },1000);
    }
}