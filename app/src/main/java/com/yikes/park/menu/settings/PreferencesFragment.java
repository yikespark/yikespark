package com.yikes.park.menu.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.yikes.park.LoginActivity;
import com.yikes.park.R;

import java.util.Locale;


public class PreferencesFragment extends Fragment {

    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

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

        AutoCompleteTextView dropDownText = rootView.findViewById(R.id.dropdown_text);
        String[ ] labelsIdiomas;
        Resources res1 = getResources();
        labelsIdiomas = res1.getStringArray( R.array.Languages );

        ArrayAdapter<String> adapterLanguages = new ArrayAdapter<>(
                rootView.getContext(),
                R.layout.dropdown_item,
                labelsIdiomas
        );
        dropDownText.setAdapter(adapterLanguages);

        final Button btn_save = rootView.findViewById(R.id.settings_save_btn);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = dropDownText.getText().toString();
                if (item .equals("Spanish")){
                    setLocale("es");
                }
                if (item .equals("English")) {
                    setLocale("en");
                }
            }
        });


        final Button btn_logout = rootView.findViewById(R.id.settings_logout_btn);
        btn_logout.setBackgroundColor(Color.RED);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: This alert needs improvements!
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Logout");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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
}