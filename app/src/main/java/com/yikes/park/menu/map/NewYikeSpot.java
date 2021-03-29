package com.yikes.park.menu.map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.yikes.park.R;
import com.yikes.park.menu.MainActivity;
import com.yikes.park.menu.map.coords.YikeSpot;
import com.yikes.park.menu.profile.data.UserInformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NewYikeSpot extends AppCompatActivity {
    DatabaseReference dbSpot;
    protected ArrayList<YikeSpot> YikeSpots;
    private UserInformation myUser;
    private Button add_img_from_gallery;
    private ImageView spot_img;
    private String imgUrl = "";
    private Uri uri;
    private File photoFile;

    private ImageView add_img_from_camera;

    private long yikeID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbSpot = FirebaseDatabase.getInstance().getReference().child("yikeSpots");
        YikeSpots = new ArrayList<YikeSpot>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspot);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /** Loads user information */
        Gson gson = new Gson();
        String json = MainActivity.sharedPref.getString(MainActivity.MY_USER_KEY,null);
        myUser = gson.fromJson(json, UserInformation.class);
        waitForDataFetch();

        spot_img = findViewById(R.id.image_yikespot);

        add_img_from_gallery = findViewById(R.id.image_yikespot_btn);
        add_img_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFromGalery();
            }
        });

        add_img_from_camera = spot_img;
        add_img_from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFromCamera();
            }
        });

        TextView newSpotName = findViewById(R.id.newSpotName);

        ///////////////////Delet Img/////////////////////////////////
//        StorageReference Folder = FirebaseStorage.getInstance().getReference();
//        StorageReference desertRef = Folder.child("/yikeSpot/EdgeLord/file/storage/emulated/0/DCIM/Camera/IMG_20210227_202726.jpg");
//        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                // File deleted successfully
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Uh-oh, an error occurred!
//            }
//        });

        final Button button = findViewById(R.id.newSpot);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double myLatitude = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LATITUDE_KEY,"0"));
                double myLongitude = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LONGITUDE_KEY,"0"));

                StorageReference storageRef = storage.getReference();

                StorageReference Folder = storageRef.child("yikeSpot");

                StorageReference file_name = Folder.child(newSpotName.getText().toString());

                if (uri != null) { // If no URI, then put a default one
                    file_name.putFile(uri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(furi -> {
                        imgUrl = String.valueOf(furi);
                        Log.i("imgURI", imgUrl);
                    }));
                } else {
                    /** TODO
                     *  Se debe añadir una imagen por defecto en caso de que el usuario no añada ninguna imagen:
                     *  imgURL = AÑADIR UNA URI POR DEFECTO;
                     */
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        YikeSpot yikeSpot = new YikeSpot(newSpotName.getText().toString(), myLatitude, myLongitude, myUser.getId(), imgUrl);
                        YikeSpots.add(yikeSpot);
                        dbSpot.setValue(YikeSpots);
                    }
                }, 5050);

            }
        });

        dbSpot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                yikeID = dataSnapshot.getChildrenCount(); // Stores the last item ID in the DB
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

    private void checkIfData(Integer children, String yikesPot) {
        dbSpot.child(String.valueOf(children)).orderByChild("spotName").equalTo(yikesPot);
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 10);
    }

    private void loadImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String fileName = "new-photo-name.jpg";
        // Create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        uri =
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 20);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        if(requestCode == 10 && resultCode == RESULT_OK){
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), uri);
            } catch (Exception e){
                e.printStackTrace();
            }

        } else if (requestCode == 20 && resultCode == RESULT_OK) {
            // bitmap = (Bitmap) data.getExtras().get("data");

            try {
                ContentResolver cr = getContentResolver();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                    add_img_from_camera.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IllegalArgumentException e) {
                if (e.getMessage() != null)
                    Log.e("Exception", e.getMessage());
                else
                    Log.e("Exception", "Exception");
                e.printStackTrace();
            }
        }

        if(bitmap != null){
            spot_img.setImageBitmap(bitmap);
        }
    }
}