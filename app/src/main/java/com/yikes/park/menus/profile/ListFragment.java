package com.yikes.park.menus.profile;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.yikes.park.R;

public class ListFragment extends Fragment {

    SQLiteDatabase db;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View addIncidencia = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView textView = addIncidencia.findViewById(R.id.textView);
        textView.setText("ᕕ( ᐛ )ᕗ");


        return addIncidencia;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}