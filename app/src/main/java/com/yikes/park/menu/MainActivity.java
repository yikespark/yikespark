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
        getUserInformationFromDatabase();


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

    private void getUserInformationFromDatabase() {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userFound = false;
                Gson gson = new Gson();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.child(user.getUid()).exists()) {
                        UserInformation db_user = postSnapshot.child(user.getUid()).getValue(UserInformation.class);
                        userFound = true;
                        myUser = db_user;

                        // Parses UserInformation into a JSON and stores it in memory for future magic
                        String userJSON = gson.toJson(myUser);
                        sharedPref.edit().putString(MY_USER_KEY, userJSON).apply();
                        break;
                    }
                }
                // The user is not in the Database, so we add it!
                if (!userFound) {
                    myUser = new UserInformation(user.getUid(), user.getUid(), user.getEmail(), String.valueOf(user.getPhotoUrl()), 0);
                    db.setValue(myUser);

                    // Parses UserInformation into a JSON and stores it in memory for future magic
                    String userJSON = gson.toJson(myUser);
                    sharedPref.edit().putString(MY_USER_KEY, userJSON).apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "Failed to read value.", error.toException());
            }
        });



    }

}