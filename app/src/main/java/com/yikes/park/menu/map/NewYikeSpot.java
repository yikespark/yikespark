package com.yikes.park.menu.map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yikes.park.R;
import com.yikes.park.getUserData;
import com.yikes.park.menu.MainActivity;
import com.yikes.park.menu.map.coords.YikeSpot;
import com.yikes.park.menu.profile.data.UserInformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class NewYikeSpot extends AppCompatActivity {
    DatabaseReference dbSpot;
    String superUrl;
    UploadTask uploadTask;
    private UserInformation myUser;
    private ImageView spot_img;
    private Uri uri;
    private ImageView add_img_from_camera;

    public NewYikeSpot() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String spotID = UUID.randomUUID().toString();
        dbSpot = FirebaseDatabase.getInstance().getReference().child("YikeSpots").child(spotID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspot);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /* Loads user information */
        new getUserData().UserData(new getUserData.Call() {
            @Override
            public void onCallback(UserInformation value) {
                Log.d("onCallback", value.toString());
                myUser = value;
            }
        });

        spot_img = findViewById(R.id.image_yikespot);

        Button add_img_from_gallery = findViewById(R.id.image_yikespot_btn);
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
                double myLatitude = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LATITUDE_KEY, "0"));
                double myLongitude = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LONGITUDE_KEY, "0"));
                if (uri != null) { // If no URI, then put a default one
                    StorageReference storageRef = storage.getReference();
                    StorageReference Folder = storageRef.child("yikeSpot/" + uri.getLastPathSegment());

                    String uriPath = getPathFromInputStreamUri(getBaseContext(), uri);

                    Uri file = Uri.fromFile(new File(uriPath));

                    uploadTask = Folder.putFile(file);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.d("onFailure", exception.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            Log.d("Metadata", String.valueOf(taskSnapshot.getMetadata().getSizeBytes()));
                            final StorageReference ref = storageRef.child("yikeSpot/" + uri.getLastPathSegment());
                            uploadTask = ref.putFile(file);
                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    // Continue with the task to get the download URL
                                    return ref.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        Log.d("SUEPRURL", downloadUri.toString());
                                        superUrl = downloadUri.toString();
                                        YikeSpot yikeSpot = new YikeSpot(newSpotName.getText().toString(), myLatitude, myLongitude, myUser.getId(), superUrl, spotID);
                                        dbSpot.setValue(yikeSpot);
                                        finish();
                                    } else {
                                        // Handle failures
                                        // ...
                                    }
                                }
                            });
                        }
                    });

                    Log.i("imgURI", uri.getPath());
                    /*file_name.putFile(uri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(furi -> {
                        imgUrl = String.valueOf(furi);
                        Log.i("imgURL", imgUrl);
                    }));*/
                } else {
                    /** TODO
                     *  Se debe añadir una imagen por defecto en caso de que el usuario no añada ninguna imagen:
                     *  imgURL = AÑADIR UNA URI POR DEFECTO;
                     */
                }
            }
        });

        /*dbSpot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                YikeSpots.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    YikeSpot yikeSpot = postSnapshot.getValue(YikeSpot.class);
                    YikeSpots.add(yikeSpot);
                }
                dbSpot.setValue(YikeSpots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("logTest", "EFailed to read value.", error.toException());
            }
        });*/
    }

    public String getPathFromInputStreamUri(Context context, Uri uri) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File photoFile = createTemporalFileFrom(inputStream);

                filePath = photoFile.getPath();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    private File createTemporalFileFrom(InputStream inputStream) throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];

            targetFile = createTemporalFile();
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }

    private File createTemporalFile() {
        return new File(this.getFilesDir(), "tempPicture.jpg");
    }

    /**
     * Image related stuff
     */
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
        if (requestCode == 10 && resultCode == RESULT_OK) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), uri);
            } catch (Exception e) {
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

        if (bitmap != null) {
            spot_img.setImageBitmap(bitmap);
        }
    }
}