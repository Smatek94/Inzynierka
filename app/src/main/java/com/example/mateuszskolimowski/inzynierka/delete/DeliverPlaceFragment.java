package com.example.mateuszskolimowski.inzynierka.delete;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mateuszskolimowski.inzynierka.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeliverPlaceFragment extends Fragment {


    public DeliverPlaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deliver_place, container, false);
    }

}
