package com.yikes.park;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yikes.park.menu.profile.data.UserInformation;

public class getUserData {
    /** Database Related Stuff */
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private UserInformation myUser;

    public interface Call {
        void onCallback(UserInformation value);
    }
    public void UserData(Call call) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

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
                    call.onCallback(db_user);
                }
                // The user is not in the Database, so we add it!
                else {
                    myUser = new UserInformation(user.getUid(), user.getDisplayName(), user.getEmail(), String.valueOf(user.getPhotoUrl()), 0,"desc");
                    call.onCallback(myUser);
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
