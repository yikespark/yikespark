package com.yikes.park;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yikes.park.menu.MainActivity;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity {

    private final int NUMBER_OF_PAGES = 4;
    private static final int RC_SIGN_IN = 666;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CarouselView customCarouselView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        customCarouselView = findViewById(R.id.login_carousel);
        customCarouselView.setPageCount(NUMBER_OF_PAGES);
        customCarouselView.setViewListener(viewListener);

        /** Google Sign-in related configuration */
        SignInButton login_btn = findViewById(R.id.login_google_btn);
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Firebase Auth Instance
        mAuth = FirebaseAuth.getInstance();

        /** Ask for location permissions before getting into the map */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.login_google_btn) {
                    signIn();
                }
            }
        });

    }
    ViewListener viewListener = new ViewListener() {
        /** Image carousel on login screen */
        @Override
        public View setViewForPosition(int position) {
        View customView = getLayoutInflater().inflate(R.layout.view_custom,null);
            // Set view attributes here
            TextView text = customView.findViewById(R.id.login_desc_text);
            ImageView image = customView.findViewById(R.id.login_background_image);

            switch (position) {
                    case 0:
                        image.setImageResource(R.drawable.login_image0);
                        text.setText(R.string.image0);
                        break;
                    case 1:
                        image.setImageResource(R.drawable.login_image1);
                        text.setText(R.string.image1);
                        break;
                    case 2:
                        image.setImageResource(R.drawable.login_image2);
                        text.setText(R.string.image2);
                        break;
                    case 3:
                        image.setImageResource(R.drawable.login_image3);
                        text.setText(R.string.image3);
                        break;
                    default:
                        image.setImageResource(R.drawable.login_image0);
                        break;
                }
            return customView;
        }
    };

    @Override
    public void onBackPressed() {
        /** Disables back button on login screen */
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Creates the real credentials to Sign-in!
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignIn", "signInWithCredential:success");
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignIn", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();

            if (task.isSuccessful()) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("SignIn", "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignIn", "Google sign in failed", e);
                }
            } else {
                Log.w("SignIn", exception.toString());
            }

        }
    }
}