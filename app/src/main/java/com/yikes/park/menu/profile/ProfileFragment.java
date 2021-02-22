package com.yikes.park.menu.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yikes.park.R;

public class ProfileFragment extends Fragment {
    FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        /* TODO: Improve everything! */
        // Gets the avatar image from the current e-mail account
        Glide.with(this).load(user.getPhotoUrl()).into((ImageView) rootView.findViewById(R.id.profile_user_image));

        // Gets the id
        TextView txt1 = rootView.findViewById(R.id.profile_field1);
        if (user != null) {
            txt1.setText(user.getDisplayName());
        } else {
            txt1.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}