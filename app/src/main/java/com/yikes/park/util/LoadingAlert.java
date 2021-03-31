package com.yikes.park.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yikes.park.R;

public class LoadingAlert {

    private Activity activity;
    private AlertDialog alert;

    private TextView loadingText;

    public LoadingAlert(Activity activity) {
        this.activity = activity;
    }

    public void startLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);

        loadingText = view.findViewById(R.id.progressText);
        loadingText.setText("Uploading..."); // To translate with XML

        builder.setView(view);
        builder.setCancelable(false);

        alert = builder.create();
        alert.show();
    }

    public void finishLoading() {
        alert.dismiss();
    }

}
