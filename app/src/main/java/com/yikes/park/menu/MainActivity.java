package com.yikes.park.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.menu.map.MapsFragment;
import com.yikes.park.menu.profile.ProfileFragment;
import com.yikes.park.menu.profile.data.UserInformation;
import com.yikes.park.menu.settings.PreferencesFragment;

public class MainActivity extends AppCompatActivity {

    final Fragment fr_maps = new MapsFragment();
    final Fragment fr_profile = new ProfileFragment();
    final Fragment fr_settings = new PreferencesFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fr_maps;

    /** Shared Preferences Related Stuff */
    public static SharedPreferences sharedPref;
    public static final String YIKES_KEY = "YikesKey";

    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";

    public static final String MY_USER_KEY = "MyUser";



    /** Database Related Stuff */
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private UserInformation myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getApplicationContext().getSharedPreferences(YIKES_KEY , Context.MODE_PRIVATE);

        /* Database */
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        /* Navigation */
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fr_settings, "3").hide(fr_settings).commit();
        fm.beginTransaction().add(R.id.main_container, fr_profile, "2").hide(fr_profile).commit();
        fm.beginTransaction().add(R.id.main_container, fr_maps, "1").commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    fm.beginTransaction().hide(active).show(fr_maps).commit();
                    active = fr_maps;
                    return true;

                case R.id.navigation_profile:
                    fm.beginTransaction().hide(active).show(fr_profile).commit();
                    active = fr_profile;
                    return true;

                case R.id.navigation_settings:
                    fm.beginTransaction().hide(active).show(fr_settings).commit();
                    active = fr_settings;
                    return true;
            }
            return false;
        }
    };

    public interface MyCallback {
        void onCallback(UserInformation value);
    }

    public void getUserInformationFromDatabase(MyCallback myCallback) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    //lol, la mierda esta no funcionaba, cada vez que abriamos la aplicación
                    //reescribia los datos del usuario, ahora deberia de funcionar.
                    //Quizás pasa lo mismo con otras cosas.
                    //if (postSnapshot.child(user.getUid()).exists())
                    //Pero quien coño a hecho esto de verdad, son las 4 y todavia estoy arreglando
                    //las chapuzas.

                        UserInformation db_user = dataSnapshot.getValue(UserInformation.class);
                        myCallback.onCallback(db_user);
                }
                // The user is not in the Database, so we add it!
                else {
                    myUser = new UserInformation(user.getUid(), user.getDisplayName(), user.getEmail(), String.valueOf(user.getPhotoUrl()), 0,"desc");
                    db.setValue(myUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "Failed to read value.", error.toException());
            }
        });
    }

}