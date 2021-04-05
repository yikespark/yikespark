package com.yikes.park.menu.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yikes.park.R;
import com.yikes.park.getUserData;
import com.yikes.park.menu.MainActivity;
import com.yikes.park.menu.map.Objects.YikeSpot;
import com.yikes.park.menu.profile.data.UserInformation;
import com.yikes.park.util.ArrayAdapterWithIcon;
import com.yikes.park.util.LoadingAlert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class NewYikeSpot extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 127;
    private static final int GALLERY_REQUEST_CODE = 128;
    private static final int GALLERY_REQUEST_WRITE_CODE = 129;


    DatabaseReference dbSpot;
    String superUrl;
    UploadTask uploadTask;
    private UserInformation myUser;
    private ImageView spot_img;
    private Uri uri;

    private LoadingAlert loading;

    public NewYikeSpot() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String spotID = UUID.randomUUID().toString();
        dbSpot = FirebaseDatabase.getInstance().getReference().child("YikeSpots").child(spotID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspot);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        loading = new LoadingAlert(this);

        /* Loads user information */
        new getUserData().UserData(new getUserData.Call() {
            @Override
            public void onCallback(UserInformation value) {
                Log.d("onCallback", value.toString());
                myUser = value;
            }
        });

        // Upload image buttons
        spot_img = findViewById(R.id.image_yikespot);
        spot_img.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final String [] items = new String[] {getString(R.string.new_spot_gallery), getString(R.string.new_spot_camera)};
               final Integer[] icons = new Integer[] {R.drawable.ic_folder_black_24dp, R.drawable.ic_camera_24px};
               ListAdapter adapter = new ArrayAdapterWithIcon(NewYikeSpot.this, items, icons);

               new AlertDialog.Builder(NewYikeSpot.this).setTitle(getString(R.string.new_spot_img_title)).setAdapter(adapter, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int item ) {
                       if (item == 0) {
                           itemGallery();
                       } else if (item == 1) {
                           itemCamera();
                       }
                   }
               }).show();
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
                //Log.i("Checar", String.valueOf(newSpotName.getText()));

                if (String.valueOf(newSpotName.getText()).equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.new_spot_error_cannot_be_empty, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    double myLatitude = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LATITUDE_KEY, "0"));
                    double myLongitude = Double.parseDouble(MainActivity.sharedPref.getString(MainActivity.LONGITUDE_KEY, "0"));
                    if (uri != null) { // If we got an URI, we can continue
                        loading.startLoading();

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
                                            Toast toast = Toast.makeText(getApplicationContext(), R.string.new_spot_error_not_success_upload, Toast.LENGTH_LONG);
                                            toast.show();
                                            throw task.getException();
                                        } else {
                                            Toast toast = Toast.makeText(getApplicationContext(), R.string.new_spot_success_upload, Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                        // Continue with the task to get the download URL
                                        return ref.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            loading.finishLoading();
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
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.new_spot_error_image_cannot_be_empty, Toast.LENGTH_LONG);
                        toast.show();
                    }
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
    private void loadImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 10);
    }

    private void loadImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String fileName = "new-photo-name.jpg";
        // Create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image captured by camera");
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
                    spot_img.setImageBitmap(bitmap);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The user now has camera permissions
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The user now has gallery permissions
            }
        }
    }

    private void itemCamera() {
        if (ContextCompat.checkSelfPermission(NewYikeSpot.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(NewYikeSpot.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(NewYikeSpot.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(NewYikeSpot.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_WRITE_CODE);
        } else {
            loadImageFromCamera();
        }
    }

    private void itemGallery() {
        if (ContextCompat.checkSelfPermission(NewYikeSpot.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(NewYikeSpot.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(NewYikeSpot.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(NewYikeSpot.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_WRITE_CODE);
        } else {
            loadImageFromGallery();
        }
    }
}