package com.example.mateuszskolimowski.inzynierka.activities.add_route_points;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.activities.add_route_points.api.GetDistancesFromNewRoutePointApiFragment;
import com.example.mateuszskolimowski.inzynierka.activities.add_route_points.api.GetDistancesToNewRoutePointApiFragment;
import com.example.mateuszskolimowski.inzynierka.activities.routes_list.AddOrUpdateNewRouteActivity;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.LoadingDialog;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.lately_added_route_points_dialog.LatelyAddedRoutePointsDialog;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.TimePickerFragment;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.Time;
import com.example.mateuszskolimowski.inzynierka.model.Travel;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AddNewRoutePointActivity extends AppCompatActivity
        implements TimePickerFragment.FragmentResponseListener,
        LatelyAddedRoutePointsDialog.LatelyAddedRoutePointsDialogInterface,
        GetDistancesFromNewRoutePointApiFragment.FragmentResponseListener,
        GetDistancesToNewRoutePointApiFragment.FragmentResponseListener,
        LoadingDialog.fragmentInteractionInterface{

    public static final String ROUTE_EXTRA_TAG = AddRoutePointsActivity.class.getName() + "ROUTE_ID_EXTRA_TAG";
    public static final String ROUTE_OUTSTATE_TAG = AddRoutePointsActivity.class.getName() + "ROUTE_OUTSTATE_TAG";
    private static final String START_TIME_OUT_STATE_TAG = AddOrUpdateNewRouteActivity.class.getName() + "START_TIME_OUT_STATE_TAG";
    private static final String END_TIME_OUT_STATE_TAG = AddOrUpdateNewRouteActivity.class.getName() + "END_TIME_OUT_STATE_TAG";

    public static final int ADD_NEW_ROUTE_POINT_ACTIVITY_TAG = 1;
    private static final int CLEAR_DATA = 1;
    private static final int FINISH_ACTIVITY_WITH_RESULT = 2;

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
//    private Button chooseFromLastPickedLocationsButton;
    private Route route;
    private LatLng selectedPlaceLatLng;
    private String selectedPlaceId;
    private String selectedPlaceName;
    private PlaceAutocompleteFragment autocompleteFragment;
    private boolean toFinished;
    private boolean fromFinished;
    private long time;

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
//        chooseFromLastPickedLocationsButton = (Button) findViewById(R.id.choose_from_last_picked_locations_button);
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
                updateRoute(CLEAR_DATA);
//                clearData();
            }
        });
        availableAddAndFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRoute(FINISH_ACTIVITY_WITH_RESULT);
//                finishActivityWithResult();
            }
        });
        /*chooseFromLastPickedLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<RoutePointDestination> latelyAddedRoutePoints = Utils.getSQLiteHelper(AddNewRoutePointActivity.this).getLatelyAddedRoutePoints();
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
        });*/
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

    private void finishActivityWithResult() {
        Intent intent = getIntent();
        intent.putExtra(AddRoutePointsActivity.ROUTE_RESULT_TAG, route);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateRoute(final int actionType){
        if (Utils.isOnline(this)) {
            Utils.showLoadingDialog("pobieranie danych...",this);
            time = System.currentTimeMillis();
            if(Utils.getSQLiteHelper(this).getRoutePointDestinationFromDataBase(selectedPlaceId) == null){
                addRoutePointDestinationToDataBase(selectedPlaceId);
            }
            addRoutePointToRoute();
            getRoutePointDestinationsFromApi(selectedPlaceId,actionType);
        } else {
            Toast.makeText(this,"brak internetu",Toast.LENGTH_SHORT).show();
        }
    }

    private Route addRoutePointToRoute() {
        route.addRoutePointId(new RoutePoint(
                selectedPlaceId,
//                new Time(AddOrUpdateNewRouteActivity.getHourFromTimeTextView(startTimeTextView),AddOrUpdateNewRouteActivity.getMinuteFromTimeTextView(startTimeTextView)),
                new Time(8,0),//fixme tylko na testy
//                new Time(AddOrUpdateNewRouteActivity.getHourFromTimeTextView(endTimeTextView),AddOrUpdateNewRouteActivity.getMinuteFromTimeTextView(endTimeTextView)),
                new Time(20,0),//fixem tylko na testy
                selectedPlaceLatLng,
                selectedPlaceName,
                System.currentTimeMillis(),
                RoutePoint.NOT_VISITED
        ));
        Utils.getSQLiteHelper(AddNewRoutePointActivity.this).updateRoutePoints(route);
        return route;
    }

    private void addRoutePointDestinationToDataBase(String selectedPlaceId) {
        Utils.getSQLiteHelper(this).addRoutePointDestination(selectedPlaceId);
    }

    private void getRoutePointDestinationsFromApi(final String selectedPlaceId, final int actionType) {
        RoutePointDestination newRoutePointDestination = Utils.getSQLiteHelper(this).getRoutePointDestinationFromDataBase(selectedPlaceId);
        ArrayList<RoutePointDestination> routePointsWithoutTravelToNewPointList = getRoutePointsWithoutTravelToNewPoint(newRoutePointDestination);

        if(routePointsWithoutTravelToNewPointList.size() != 0) {
            getDistancesFromNewRoutePoint(selectedPlaceId, routePointsWithoutTravelToNewPointList, actionType);
            getDistanceToNewRoutePoint(selectedPlaceId, routePointsWithoutTravelToNewPointList, actionType);
        } else {
            handleActionType(actionType);
        }
    }

    //funkcja ktora tworzy liste z punktami ktore jeszcze nie wyznaczaly polaczenia z nowo dodanym punktem
    private ArrayList<RoutePointDestination> getRoutePointsWithoutTravelToNewPoint(RoutePointDestination newRoutePointDestination) {
        ArrayList<RoutePointDestination> routePointDestinationArrayList = Utils.getSQLiteHelper(this).getRoutePointsDestinationList();
        ArrayList<RoutePointDestination> resultList = new ArrayList<>();
        boolean add;
        for(RoutePointDestination rpd : routePointDestinationArrayList){
            add = true;
            if(rpd.getRoutePointPlaceId().equals(newRoutePointDestination.getRoutePointPlaceId())){
                add = false;
            }
            for(Travel travel : rpd.getTravelToPointList()){
                if(travel.getDestinationPlaceId().equals(newRoutePointDestination.getRoutePointPlaceId())){
                    add = false;
                    break;
                }
            }
            if(add){
                resultList.add(rpd);
            }
        }
        return filterResultList(resultList);
    }

    //funkcja ktora usuwa punkty ktore nie sa w aktualnie aktywnej trasie
    private ArrayList<RoutePointDestination> filterResultList(ArrayList<RoutePointDestination> allPointsList) {
        ArrayList<RoutePointDestination> resultList = new ArrayList<>();
        for(RoutePointDestination rpd : allPointsList){
            for(RoutePoint rp : route.getRoutePoints()){
                if(rp.getId().equals(rpd.getRoutePointPlaceId())){
                    resultList.add(rpd);
                    break;
                }
            }
        }
        return resultList;
    }

    //pobiera dane o trasie do nowo danego punktu z api
    private void getDistanceToNewRoutePoint(String selectedPlaceId, ArrayList<RoutePointDestination> routePointsWithoutTravelToNewPointList, int actionType) {
        GetDistancesToNewRoutePointApiFragment getDistancesToNewRoutePointApiFragment = (GetDistancesToNewRoutePointApiFragment) getSupportFragmentManager().findFragmentByTag(GetDistancesToNewRoutePointApiFragment.FRAGMENT_TAG);
        if (getDistancesToNewRoutePointApiFragment == null) {
                getDistancesToNewRoutePointApiFragment = GetDistancesToNewRoutePointApiFragment.newInstance(selectedPlaceId,routePointsWithoutTravelToNewPointList, actionType);
                getSupportFragmentManager().beginTransaction().add(getDistancesToNewRoutePointApiFragment, GetDistancesToNewRoutePointApiFragment.FRAGMENT_TAG).commitAllowingStateLoss();
        }
    }

    //pobiranie danych o trasie z nowo dodanego punktu do pozostalych
    private void getDistancesFromNewRoutePoint(String selectedPlaceId, ArrayList<RoutePointDestination> routePointsWithoutTravelToNewPointList, int actionType) {
        GetDistancesFromNewRoutePointApiFragment getDistancesFromNewRoutePointApiFragment = (GetDistancesFromNewRoutePointApiFragment) getSupportFragmentManager().findFragmentByTag(GetDistancesFromNewRoutePointApiFragment.FRAGMENT_TAG);
        if (getDistancesFromNewRoutePointApiFragment == null) {
                getDistancesFromNewRoutePointApiFragment = GetDistancesFromNewRoutePointApiFragment.newInstance(selectedPlaceId,routePointsWithoutTravelToNewPointList, actionType);
                getSupportFragmentManager().beginTransaction().add(getDistancesFromNewRoutePointApiFragment, GetDistancesFromNewRoutePointApiFragment.FRAGMENT_TAG).commitAllowingStateLoss();
        }
    }

    private void initPlaceAutocompleteFragment() {
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().
                findFragmentById(R.id.place_autocomplete_fragment);
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
        enableAddRouteButton();//fixme usunac, tylko na testy
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
        finishActivityWithResult();
    }

    @Override
    public void latelyAddedRoutePointDialogCallback(RoutePointDestination routePointDestination) {
       /* FragmentManager fragmentManager = getSupportFragmentManager();
        LatelyAddedRoutePointsDialog latelyAddedRoutePointsDialog = (LatelyAddedRoutePointsDialog) fragmentManager.findFragmentByTag(LatelyAddedRoutePointsDialog.TAG);
        if (latelyAddedRoutePointsDialog != null) {
            latelyAddedRoutePointsDialog.dismiss();
        }
        selectedPlaceLatLng = routePointDestination.getRoutePointLatLng();
        selectedPlaceId = routePointDestination.getRoutePointPlaceId();
        selectedPlaceName = routePointDestination.getRoutePointName() + "";
        autocompleteFragment.setText(selectedPlaceName);
        checkIfAllInfoAvailable(); fixme*/
    }

    @Override
    public void onDoneGetDestinationRoutePoints(final RoutePointDestination routePointDestination, final int actionType) {
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                Utils.getSQLiteHelper(AddNewRoutePointActivity.this).updateRoutePointsDestination(routePointDestination);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(toFinished){
                    toFinished = false;
                    fromFinished = false;
                    handleActionType(actionType);
                } else {
                    fromFinished = true;
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    public void onDoneGetDestinationRoutePoints(final ArrayList<RoutePointDestination> routePointDestinationsList, final int actionType) {
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                Utils.getSQLiteHelper(AddNewRoutePointActivity.this).updateRoutePointsDestination(routePointDestinationsList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(fromFinished){
                    toFinished = false;
                    fromFinished = false;
                    handleActionType(actionType);
                } else {
                    toFinished = true;
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void handleActionType(int actionType) {
        hideLoadingDialog();
        if(actionType == CLEAR_DATA){
            clearData();
        } else if(actionType == FINISH_ACTIVITY_WITH_RESULT){
            finishActivityWithResult();
        }
    }

    public void hideLoadingDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.executePendingTransactions();
        LoadingDialog customDialog = (LoadingDialog) fragmentManager.findFragmentByTag(LoadingDialog.TAG);
        if(customDialog != null){
            customDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onFailureListener(String msg, int statusCode) {

    }

    @Override
    public void backPressedWhenDialogWasVisible() {
        finish();
    }
}
