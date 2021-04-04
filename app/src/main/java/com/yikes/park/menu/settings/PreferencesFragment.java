package com.yikes.park.menu.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.yikes.park.LoginActivity;
import com.yikes.park.R;
import com.yikes.park.menu.MainActivity;

import java.util.HashMap;
import java.util.Locale;


public class PreferencesFragment extends Fragment {

    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private String[] labelLanguages;
    private AutoCompleteTextView dropDownText;


    private CardView notificationsCard;
    private CardView geolocationCard;
    private CardView cameraCard;
    private CardView galleryCard;
    private SwitchMaterial notifications;
    private SwitchMaterial geolocation;
    private SwitchMaterial camera;
    private SwitchMaterial gallery;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    private GoogleSignInClient gsi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        /** Permission Switches */
        notificationsCard = rootView.findViewById(R.id.notificationsCard);
        notifications = rootView.findViewById(R.id.notificationsSwitch);
        notifications.setClickable(false);

        geolocationCard = rootView.findViewById(R.id.geolocationCard);
        geolocation = rootView.findViewById(R.id.geolocationSwitch);
        geolocation.setClickable(false);

        cameraCard = rootView.findViewById(R.id.cameraCard);
        camera = rootView.findViewById(R.id.cameraSwitch);
        camera.setClickable(false);

        galleryCard = rootView.findViewById(R.id.galleryCard);
        gallery = rootView.findViewById(R.id.gallerySwitch);
        gallery.setClickable(false);

        checkPermissions();

        dropDownText = rootView.findViewById(R.id.dropdown_text);
        Resources res1 = getResources();
        labelLanguages = res1.getStringArray(R.array.settings_language_list);

       // dropDownText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_24px, 0,0,0);
       // dropDownText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bell_24px, 0,0,0);



       ArrayAdapter<String> adapterLanguages = new ArrayAdapter<>(
                rootView.getContext(),
                R.layout.dropdown_item,
                labelLanguages
        );
        dropDownText.setAdapter(adapterLanguages);

        /** Languages Config */
        final Button btn_save = rootView.findViewById(R.id.settings_save_btn);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = dropDownText.getText().toString();
                if (item .equals(R.string.settings_language_es)){
                    setLocale("es");
                }
                if (item .equals(R.string.settings_language_en)) {
                    setLocale("en");
                }
            }
        });


        /** Sing out */
        final Button btn_logout = rootView.findViewById(R.id.settings_logout_btn);
        btn_logout.setBackgroundColor(Color.RED);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: This alert needs improvements!
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(R.string.settings_logout_alert_title);
                builder.setMessage(R.string.settings_logout_alert_msg);

                builder.setPositiveButton(R.string.settings_logout_alert_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.sharedPref.edit().remove(MainActivity.MY_USER_KEY).apply(); // Removes stored user in memory just for safety!
                        signOut();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton(R.string.settings_logout_alert_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing else happens
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        return rootView;
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(getContext(), LoginActivity.class);
        requireActivity().finish();
        startActivity(refresh);
    }

    private void signOut() {
        // Sign out from Firebase Auth
        mAuth.signOut();

        // Sign out from Google Auth -> The user has to choose an e-mail account again
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>
                        () {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    // When comming back from a screen, permissions are checked again to update them!
    @Override
    public void onResume() {
        super.onResume();
        checkPermissions();

    }

    // Checks Permissions to modify the Switch status
    private void checkPermissions() {

        if (!NotificationManagerCompat.from(getContext()).areNotificationsEnabled()) {
            notifications.setChecked(false);
            notificationsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
        } else {
            notifications.setChecked(true);
        }

        // Checking Geolocation
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            geolocation.setChecked(false);
            geolocationCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
        } else {
            geolocation.setChecked(true);
        }

        // Checking Camera
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            camera.setChecked(false);
            cameraCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
        } else {
            camera.setChecked(true);
        }

        // Checking Gallery
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            gallery.setChecked(false);
            galleryCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
        } else {
            gallery.setChecked(true);
        }
    }

}