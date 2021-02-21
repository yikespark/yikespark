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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yikes.park.menu.MainActivity;

public class LoginActivity extends AppCompatActivity {

    CarouselView customCarouselView;
    int NUMBER_OF_PAGES = 4;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        customCarouselView = findViewById(R.id.customCarouselView);
        customCarouselView.setPageCount(NUMBER_OF_PAGES);
        customCarouselView.setViewListener(viewListener);

        /** Google Sign-in related configuration */
        SignInButton signInButton = findViewById(R.id.button4);
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button4) {
                    signIn(mGoogleSignInClient);
                }
            }
        });

        /** Ask for location permissions before getting into the map */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    ViewListener viewListener = new ViewListener() {

        /** Image carousel on login screen */
        @Override
        public View setViewForPosition(int position) {
            @SuppressLint("InflateParams") View customView = getLayoutInflater().inflate(R.layout.view_custom, null);
            //set view attributes here
            TextView text = customView.findViewById(R.id.textView);
            ImageView image = customView.findViewById(R.id.imageView);

            switch (position) {
                    case 0:
                        image.setImageResource(R.drawable.image0);
                        text.setText(R.string.image0);
                        break;
                    case 1:
                        image.setImageResource(R.drawable.image1);
                        text.setText(R.string.image1);
                        break;
                    case 2:
                        image.setImageResource(R.drawable.image2);
                        text.setText(R.string.image2);
                        break;
                    case 3:
                        image.setImageResource(R.drawable.image3);
                        text.setText(R.string.image3);
                        break;
                    default:
                        image.setImageResource(R.drawable.image0);
                        break;
                }
            return customView;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {
        // Updates the screen when login-in with Google
        if (account != null){
            Toast.makeText(this,"¡Te has conectado con éxito!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this,"¡Error al intentar conectarte!", Toast.LENGTH_LONG).show();
        }
    }

    private void signIn(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason. Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("G SIGN:", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
}