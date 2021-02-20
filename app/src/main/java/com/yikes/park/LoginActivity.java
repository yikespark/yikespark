package com.yikes.park;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

/* F5:3B:6F:D1:EE:11:62:BD:41:03:43:F3:0F:93:F2:5B:3E:B1:57:74 */
public class LoginActivity extends AppCompatActivity {

    CarouselView customCarouselView;
    int NUMBER_OF_PAGES = 2;
    private static int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        /* GOOGLE AUTH */
        SignInButton signInButton = findViewById(R.id.button4);
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button4:
                        signIn(mGoogleSignInClient);
                        break;
                }
            }
        });


        /* END GOOGLE AUTH */



        customCarouselView = (CarouselView) findViewById(R.id.customCarouselView);
        customCarouselView.setPageCount(NUMBER_OF_PAGES);
        customCarouselView.setViewListener(viewListener);

        /*Button btn_google = findViewById(R.id.button4);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGoogleMaps();
            }
        });*/

        /* END TEST */





        /* OLD STUFF
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }*/

    }

    public void goToGoogleMaps() {
        Toast.makeText(this,"¡Te has conectado con éxito!", Toast.LENGTH_LONG).show();

        startActivity(new Intent(this, com.yikes.park.menu.MainActivity.class));
    }

    ViewListener viewListener = new ViewListener() {

        @Override
        public View setViewForPosition(int position) {
            @SuppressLint("InflateParams") View customView = getLayoutInflater().inflate(R.layout.view_custom, null);
            //set view attributes here
            TextView text = (TextView) customView.findViewById(R.id.textView);
            ImageView image = (ImageView) customView.findViewById(R.id.imageView);

            switch (position) {
                case 0:
                    image.setImageResource(R.drawable.image0);
                    text.setText("wowowowowowoowowowowowowowowowowowowowowowow");
                    break;
                case 1:
                    image.setImageResource(R.drawable.image2);
                    text.setText("SDFKLJSDFGLKSDJFHKLSDJFHSDKLFHKLSDJFLDFSDFSD");
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
            // The Task returned from this call is always completed, no need to attach
            // a listener.
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
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("G SIGN:", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }



}