package com.yikes.park;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;


public class LoginActivity extends AppCompatActivity {

    CarouselView customCarouselView;
    int NUMBER_OF_PAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        customCarouselView = (CarouselView) findViewById(R.id.customCarouselView);
        customCarouselView.setPageCount(NUMBER_OF_PAGES);
        customCarouselView.setViewListener(viewListener);

        Button btn_google = findViewById(R.id.button4);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGoogleMaps();
            }
        });

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

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Titulo");
        builder.setMessage("Mensaje");
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();*/

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

}