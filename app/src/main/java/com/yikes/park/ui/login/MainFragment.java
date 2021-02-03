package com.yikes.park.ui.login;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yikes.park.R;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
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

        return root;
    }

    ViewListener viewListener = new ViewListener() {

        @Override
        public View setViewForPosition(int position) {
            View customView = getLayoutInflater().inflate(R.layout.view_custom, null);
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
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

}