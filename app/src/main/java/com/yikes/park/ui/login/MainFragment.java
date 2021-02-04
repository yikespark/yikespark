package com.yikes.park.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yikes.park.R;
import com.yikes.park.menus.MainActivity;

public class MainFragment extends Fragment {

    CarouselView customCarouselView;
    int NUMBER_OF_PAGES = 2;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.main_fragment, container, false);

        customCarouselView = (CarouselView) root.findViewById(R.id.customCarouselView);
        customCarouselView.setPageCount(NUMBER_OF_PAGES);
        customCarouselView.setViewListener(viewListener);

        /* TESTING BUTTON */
        Button btn_google = root.findViewById(R.id.button4);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGoogleMaps();
            }
        });
        /* END TEST */


        return root;
    }

    /* TESTING METHOD */
    public void goToGoogleMaps() {
        startActivity(new Intent(getContext(), MainActivity.class));
    }
    /* END TEST */


    ViewListener viewListener = new ViewListener() {

        @Override
        public View setViewForPosition(int position) {
            @SuppressLint("InflateParams") View customView = getLayoutInflater().inflate(R.layout.view_custom, null);
            //set view attributes here
            TextView text = (TextView) customView.findViewById(R.id.textView);
            ImageView image = (ImageView) customView.findViewById(R.id.imageView);

            switch (position)
            {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}