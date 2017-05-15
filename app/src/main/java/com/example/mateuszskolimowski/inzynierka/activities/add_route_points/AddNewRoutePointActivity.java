package com.example.mateuszskolimowski.inzynierka.activities.add_route_points;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.activities.routes_list.AddOrUpdateNewRouteActivity;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.lately_added_route_points_dialog.LatelyAddedRoutePointsDialog;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.TimePickerFragment;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.Time;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AddNewRoutePointActivity extends AppCompatActivity
        implements TimePickerFragment.FragmentResponseListener,
        LatelyAddedRoutePointsDialog.LatelyAddedRoutePointsDialogInterface{

    public static final String ROUTE_EXTRA_TAG = AddRoutePointsActivity.class.getName() + "ROUTE_ID_EXTRA_TAG";
    public static final String ROUTE_OUTSTATE_TAG = AddRoutePointsActivity.class.getName() + "ROUTE_OUTSTATE_TAG";
    private static final String START_TIME_OUT_STATE_TAG = AddOrUpdateNewRouteActivity.class.getName() + "START_TIME_OUT_STATE_TAG";
    private static final String END_TIME_OUT_STATE_TAG = AddOrUpdateNewRouteActivity.class.getName() + "END_TIME_OUT_STATE_TAG";

    public static final int ADD_NEW_ROUTE_POINT_ACTIVITY_TAG = 1;

    private View startTimeLayout;
    private TextView startTimeTextView;
    private View endTimeLayout;
    private TextView endTimeTextView;
    private Place selectedPlace;
    private boolean buttonEnabled;
    private View availableButtonsLayout;
    private View unavailableButtonsLayout;
    private Button availableAddAndContiuneButton;
    private Button availableAddAndFinishButton;
    private Button chooseFromLastPickedLocationsButton;
    private Route route;
    private LatLng selectedPlaceLatLng;
    private String selectedPlaceId;
    private String selectedPlaceName;
    private PlaceAutocompleteFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_route_point);
        Utils.initToolbarTitle(getSupportActionBar(),getString(R.string.add_update_route_point));
        getLayoutComponents();
        setUpGUI();
        if(savedInstanceState == null){
            route = getIntent().getExtras().getParcelable(ROUTE_EXTRA_TAG);
        } else {
            route = savedInstanceState.getParcelable(ROUTE_OUTSTATE_TAG);
            startTimeTextView.setText(savedInstanceState.getString(START_TIME_OUT_STATE_TAG));
            endTimeTextView.setText(savedInstanceState.getString(END_TIME_OUT_STATE_TAG));
        }
        checkIfAllInfoAvailable();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ROUTE_OUTSTATE_TAG,route);
        outState.putString(START_TIME_OUT_STATE_TAG,startTimeTextView.getText().toString());
        outState.putString(END_TIME_OUT_STATE_TAG,endTimeTextView.getText().toString());
    }

    private void getLayoutComponents() {
        View timeChoosingLayout = findViewById(R.id.time_choosing_layout);
        startTimeLayout = timeChoosingLayout.findViewById(R.id.start_time_layout);
        startTimeTextView = (TextView) startTimeLayout.findViewById(R.id.time_textview);
        endTimeLayout = timeChoosingLayout.findViewById(R.id.end_time_layout);
        endTimeTextView = (TextView) endTimeLayout.findViewById(R.id.time_textview);
        availableButtonsLayout = findViewById(R.id.available_buttons_layout);
        unavailableButtonsLayout = findViewById(R.id.unavailable_buttons_layout);
        availableAddAndContiuneButton = (Button) findViewById(R.id.available_add_and_contiune_button);
        availableAddAndFinishButton = (Button) findViewById(R.id.available_add_and_finish_button);
        chooseFromLastPickedLocationsButton = (Button) findViewById(R.id.choose_from_last_picked_locations_button);
    }

    private void setUpGUI() {
        AddOrUpdateNewRouteActivity.initTimeViews(startTimeLayout,getString(R.string.start_time),endTimeLayout,getString(R.string.end_time),AddNewRoutePointActivity.this);
        initPlaceAutocompleteFragment();
        initButtonsClicks();
    }

    private void initButtonsClicks() {
        availableAddAndContiuneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRoute();
                clearData();
            }
        });
        availableAddAndFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivityWithResult(updateRoute());
            }
        });
        chooseFromLastPickedLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<RoutePoint> latelyAddedRoutePoints = Utils.getSQLiteHelper(AddNewRoutePointActivity.this).getLatelyAddedRoutePoints();
                if(latelyAddedRoutePoints.size() == 0){
                    Utils.showMsgDialog(AddNewRoutePointActivity.this,getString(R.string.no_route_points_lately_added));
                } else {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    LatelyAddedRoutePointsDialog latelyAddedRoutePointsDialog = (LatelyAddedRoutePointsDialog) fragmentManager.findFragmentByTag(LatelyAddedRoutePointsDialog.TAG);
                    if (latelyAddedRoutePointsDialog == null) {
                        latelyAddedRoutePointsDialog = LatelyAddedRoutePointsDialog.newInstance(latelyAddedRoutePoints);
                        latelyAddedRoutePointsDialog.show(fragmentManager.beginTransaction(), LatelyAddedRoutePointsDialog.TAG);
                    }
                }
            }
        });
    }

    private void clearData() {
        startTimeTextView.setText("00:00");
        endTimeTextView.setText("00:00");
        selectedPlace = null;
        selectedPlaceLatLng = null;
        selectedPlaceId = null;
        selectedPlaceName = null;
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setText("");
    }

    private void finishActivityWithResult(Route route) {
        Intent intent = getIntent();
        intent.putExtra(AddRoutePointsActivity.ROUTE_RESULT_TAG, route);
        setResult(RESULT_OK, intent);
        finish();
    }

    private Route updateRoute(){
        RoutePoint routePoint = new RoutePoint(
                selectedPlaceLatLng,
                selectedPlaceName,
                selectedPlaceId,
                new Time(AddOrUpdateNewRouteActivity.getHourFromTimeTextView(startTimeTextView),AddOrUpdateNewRouteActivity.getMinuteFromTimeTextView(startTimeTextView)),
                new Time(AddOrUpdateNewRouteActivity.getHourFromTimeTextView(endTimeTextView),AddOrUpdateNewRouteActivity.getMinuteFromTimeTextView(endTimeTextView)));
        route.addRoutePoint(routePoint);
        Utils.getSQLiteHelper(AddNewRoutePointActivity.this).updateRoutePoints(route);
        Utils.getSQLiteHelper(AddNewRoutePointActivity.this).addNewLatelyAddedRoutePoint(routePoint);
        return route;
    }

    private void initPlaceAutocompleteFragment() {
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                selectedPlaceLatLng = place.getLatLng();
                selectedPlaceId = place.getId();
                selectedPlaceName = place.getName() + "";
                checkIfAllInfoAvailable();
            }

            @Override public void onError(Status status) {}
        });
        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPlaceLatLng = null;
                selectedPlaceId = null;
                selectedPlaceName = null;
                checkIfAllInfoAvailable();
                autocompleteFragment.setText("");
            }
        });
    }

    @Override
    public void onDoneGetTime(int timerKind, int hour, int minute) {
        if(timerKind == TimePickerFragment.START_TIMER_KIND){
            AddOrUpdateNewRouteActivity.editStartTimeTextView(hour,minute,endTimeTextView,startTimeTextView,AddNewRoutePointActivity.this,R.id.activity_add_new_route_point);
        } else if(timerKind == TimePickerFragment.END_TIMER_KIND){
            AddOrUpdateNewRouteActivity.editEndTimeTextView(hour,minute,startTimeTextView,endTimeTextView,AddNewRoutePointActivity.this,R.id.activity_add_new_route_point);
        }
        checkIfAllInfoAvailable();
    }

    private void checkIfAllInfoAvailable() {
        if(!AddOrUpdateNewRouteActivity.isTimeTextViewZero(startTimeTextView) && !AddOrUpdateNewRouteActivity.isTimeTextViewZero(endTimeTextView) && isLocationPicked()){
            if(!buttonEnabled) {
                enableAddRouteButton();
                buttonEnabled = true;
            }
        } else {
            if(buttonEnabled) {
                disableAddRouteButton();
                buttonEnabled = false;
            }
        }
    }

    private void disableAddRouteButton() {
        availableButtonsLayout.setVisibility(View.GONE);
        unavailableButtonsLayout.setVisibility(View.VISIBLE);
    }

    private void enableAddRouteButton() {
        availableButtonsLayout.setVisibility(View.VISIBLE);
        unavailableButtonsLayout.setVisibility(View.GONE);
    }

    private boolean isLocationPicked() {
        if(selectedPlaceLatLng == null && selectedPlaceId == null && selectedPlaceName == null){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        finishActivityWithResult(route);
    }

    @Override
    public void latelyAddedRoutePointDialogCallback(RoutePoint routePoint) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LatelyAddedRoutePointsDialog latelyAddedRoutePointsDialog = (LatelyAddedRoutePointsDialog) fragmentManager.findFragmentByTag(LatelyAddedRoutePointsDialog.TAG);
        if (latelyAddedRoutePointsDialog != null) {
            latelyAddedRoutePointsDialog.dismiss();
        }
        selectedPlaceLatLng = routePoint.getRoutePointLatLng();
        selectedPlaceId = routePoint.getRoutePointPlaceId();
        selectedPlaceName = routePoint.getRoutePointName() + "";
        autocompleteFragment.setText(selectedPlaceName);
        checkIfAllInfoAvailable();
    }
}
