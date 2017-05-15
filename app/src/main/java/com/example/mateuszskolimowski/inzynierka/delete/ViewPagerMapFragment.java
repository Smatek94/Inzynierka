package com.example.mateuszskolimowski.inzynierka.delete;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;

public class ViewPagerMapFragment extends Fragment {

    private static final String CAMERA_POSITION_SAVED_INSTANCE_STATE_TAG = ViewPagerMapFragment.class.getCanonicalName() + "camera_position_tag";
    private GoogleMap map;
    private CameraPosition cameraPosition;
    private fragmentInteractionInterface listener;

    public ViewPagerMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        retainFragmentData(savedInstanceState);
        getLayoutComponents(view);
        setUpGUI();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveLastCameraPositionIntoSharedPreferences();
    }

    private void retainFragmentData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            cameraPosition = (CameraPosition) savedInstanceState.get(CAMERA_POSITION_SAVED_INSTANCE_STATE_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (map != null) {
            outState.putParcelable(CAMERA_POSITION_SAVED_INSTANCE_STATE_TAG, map.getCameraPosition());
        }
    }

    private void getLayoutComponents(View view) {

    }

    private void setUpGUI() {
        initMap();
    }

    private void initMap() {
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                retainMapState();
            }
        });
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
    }

    private void retainMapState() {
        retainCameraPosition();
    }

    private void retainCameraPosition() {
        if (cameraPosition != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else if (getLastCameraPositionFromSharedPreferences() != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(getLastCameraPositionFromSharedPreferences()));
        } else {
            Utils.debugLog("camera position : " + null);
        }
    }

    private CameraPosition getLastCameraPositionFromSharedPreferences() {
        return null;
        //fixme implement
    }

    private void saveLastCameraPositionIntoSharedPreferences() {
        //fixme implement
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof fragmentInteractionInterface) {
            listener = (fragmentInteractionInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement fragmentInteractionInterface");
        }
    }*/

    public interface fragmentInteractionInterface {
        void getUserLocalization();
    }
}
