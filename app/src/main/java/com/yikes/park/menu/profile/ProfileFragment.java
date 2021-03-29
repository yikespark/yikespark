package com.yikes.park.menu.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.getUserData;
import com.yikes.park.menu.MainActivity;
import com.yikes.park.menu.profile.data.UserInformation;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private final int UPDATE_MY_DATA = 0;
    private final int GET_DATA_FROM_ANOTHER_USER = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");
    private UserInformation my_user;
    private UserInformation dif_user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        Gson gson = new Gson();
        String json =  MainActivity.sharedPref.getString(MainActivity.MY_USER_KEY,"");
        my_user = gson.fromJson(json, UserInformation.class);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        new getUserData().UserData(new getUserData.Call() {
            @Override
            public void onCallback(UserInformation value) {
                Log.d("onCallback", value.toString());
                my_user = value;

                RequestOptions options = new RequestOptions();
                options.circleCrop();
                Glide.with(getContext()).load(my_user.getAvatar()).apply(options).into((ImageView) rootView.findViewById(R.id.profile_user_image));
                TextView myUserName = rootView.findViewById(R.id.profile_field1);
                myUserName.setText(my_user.getUsername());
                TextView followers = rootView.findViewById(R.id.profile_followers);
                followers.setText(String.valueOf(my_user.getFollowers()));
                TextView email = rootView.findViewById(R.id.email);
                email.setText(my_user.getEmail());
                TextView id = rootView.findViewById(R.id.id);
                id.setText(my_user.getId());
                TextView desc = rootView.findViewById(R.id.desc);
                desc.setText(my_user.getDesc());
            }
        });
        // UserInformation myUser = new UserInformation(user.getUid(), user.getEmail(), String.valueOf(user.getPhotoUrl()));
        ImageButton editButton = rootView.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("prueba", "Editando!");
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /*private void ESTÁ-EN-CONSTRUCCIÓN(FirebaseUser user, int usecase) {

        // Read from the database
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("logTest " ,"" + dataSnapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserInformation db_user = postSnapshot.getValue(UserInformation.class);

                    if (usecase == GET_MY_USER_DATA) {
                        if (db_user.getUsername().equals(user.getUid())) {
                            // My own user
                            my_user = db_user;
                        } else {
                            // User have to be added in the database
                            my_user = new UserInformation(user.getUid(), user.getEmail(), String.valueOf(user.getPhotoUrl()));
                            db.setValue(my_user);
                        }
                    } else if (usecase == GET_DATA_FROM_ANOTHER_USER) {
                        if (db_user.getUsername().equals("nombre_de_usuario_aquí")) {
                            // Gets the other user information
                            dif_user = db_user;
                        }
                    } else if (usecase == UPDATE_MY_DATA) {

                    }



                    Log.i("logTest", my_user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "Failed to read value.", error.toException());
            }

        });

    }*/

}