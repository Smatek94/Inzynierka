package com.example.mateuszskolimowski.inzynierka.activities.show_on_map;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ShowRoutePointsOnMapActivity extends AppCompatActivity {

    public static final String ROUTE_ID_EXTRA_TAG = ShowRoutePointsOnMapActivity.class.getName() + "ROUTE_ID_EXTRA_TAG";
    public static final String CAMERA_POSITION_OUT_STATE_TAG = ShowRoutePointsOnMapActivity.class.getName() + "CAMERA_POSITION_OUT_STATE_TAG";
    private GoogleMap map;
    private Route route;
    private ArrayList<MyMarker> markerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route_points_on_map);
        route = getRoute();
        setUpGUI(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CAMERA_POSITION_OUT_STATE_TAG,map.getCameraPosition());
    }

    private Route getRoute() {
        int routeId = getIntent().getExtras().getInt(ROUTE_ID_EXTRA_TAG);
        for(Route r : Utils.getSQLiteHelper(this).getRoutes()){
            if(r.getId() == routeId){
                return r;
            }
        }
        return null;
    }

    private void setUpGUI(Bundle savedInstanceState) {
        initMap(savedInstanceState);
    }

    private void initMap(final Bundle savedInstanceState) {
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                markerList = new ArrayList<>();
                initMapMarkers();
                if(savedInstanceState != null){
                    moveCamera((CameraPosition) savedInstanceState.getParcelable(CAMERA_POSITION_OUT_STATE_TAG));
                } else {
                    animateCamera();
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
    }

    private void moveCamera(CameraPosition cameraPosition) {
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void initMapMarkers() {
        for(int i = 0 ; i < route.getRoutePoints().size() ; i++){
            markerList.add(createNewMarker(i));
        }
    }

    private void animateCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0 ; i < markerList.size() ; i++) {
            builder.include(markerList.get(i).getMarker().getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }

    private MyMarker createNewMarker(int i) {
        return new MyMarker(map.addMarker(createNewMarkerOptions(i)),i);
    }

    private MarkerOptions createNewMarkerOptions(int i) {
        return new MarkerOptions().position(route.getRoutePoints().get(i).getRoutePointLatLng());
        //fixme dodac customowy marker
    }
}