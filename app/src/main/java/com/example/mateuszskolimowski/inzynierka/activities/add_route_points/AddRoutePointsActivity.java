package com.example.mateuszskolimowski.inzynierka.activities.add_route_points;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.activities.navigation.NavigateActivity;
import com.example.mateuszskolimowski.inzynierka.activities.routes_list.AddOrUpdateNewRouteActivity;
import com.example.mateuszskolimowski.inzynierka.activities.show_on_map.ShowRoutePointsOnMapActivity;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.AreYouSureDialog;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.EditRoutePointTimeDialog;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.TimePickerFragment;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.model.Time;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.Travel;
import com.example.mateuszskolimowski.inzynierka.utils.PermissionsUtils;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.example.mateuszskolimowski.inzynierka.views.DividerItemDecoration;
import com.example.mateuszskolimowski.inzynierka.vns.VNS;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class AddRoutePointsActivity extends AppCompatActivity implements
        AreYouSureDialog.DeleteRoutePointInterface,
        TimePickerFragment.FragmentResponseListener,
        EditRoutePointTimeDialog.EditRoutePointTimeInterace, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String ROUTE_EXTRA_TAG = AddRoutePointsActivity.class.getName() + "ROUTE_ID_EXTRA_TAG";
    public static final String ROUTE_RESULT_TAG = AddRoutePointsActivity.class.getName() + "ROUTE_RESULT_TAG";
    public static final String ROUTE_OUTSTATE_TAG = AddRoutePointsActivity.class.getName() + "ROUTE_OUTSTATE_TAG";
    private static final int GET_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
    private Route route;
    private TextView noRoutePointsTextView;
    private Button addRoutePointButton;
    private RecyclerView routePointsRecyclerView;
    private RoutePointsRecyclerViewAdapter routePointsRecyclerViewAdapter;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper touchHelper;
    private MenuInflater inflater;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            route = getIntent().getExtras().getParcelable(ROUTE_EXTRA_TAG);
        } else {
            route = savedInstanceState.getParcelable(ROUTE_OUTSTATE_TAG);
        }
        Utils.initToolbarTitle(getSupportActionBar(), route.getRouteName());
        setContentView(R.layout.activity_add_route_points);
        getLayoutComponents();
        setUpGUI();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.route_points_menu, menu);
        handleIconsVisibility(menu);
        return true;
    }

    private void handleIconsVisibility(Menu menu) {
        if (route.getRoutePoints().size() == 0) {
            menu.setGroupVisible(R.id.menu_group, false);
        } else {
            menu.setGroupVisible(R.id.menu_group, true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_on_map: {
                Intent intent = new Intent(AddRoutePointsActivity.this, ShowRoutePointsOnMapActivity.class);
                intent.putExtra(ShowRoutePointsOnMapActivity.ROUTE_ID_EXTRA_TAG, route.getId());
                startActivity(intent);
                break;
            }
            case R.id.action_optimize: {
                if (VNS.checkIfRouteIsFeasible(route)) {
                    if(PermissionsUtils.requestPermission(this,this,permissions,GET_LOCATION_PERMISSION_REQUEST_CODE)){
                        permissionAcceptedOptimzeRoute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Trasa nie jest poprawna. Któreś z okien czasowych punktu trasy nie zawiera się w oknie czasowym całej trasy", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.action_navigate: {
                Intent intent = new Intent(AddRoutePointsActivity.this, NavigateActivity.class);
                intent.putExtra(NavigateActivity.ROUTE_ID_EXTRA_TAG, route.getId());
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void permissionAcceptedOptimzeRoute() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        } else {
            optimizeRoute();
        }

    }

    private void optimizeRoute() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Route x = VNS.optimal(route, this);
        Route x2 = VNS.VNS(route,this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ROUTE_OUTSTATE_TAG, route);
    }


    private void getLayoutComponents() {
        noRoutePointsTextView = (TextView) findViewById(R.id.no_route_points_textview);
        addRoutePointButton = (Button) findViewById(R.id.add_route_point_button);
        routePointsRecyclerView = (RecyclerView) findViewById(R.id.route_points_recycler_view);
    }

    private void setUpGUI() {
        initAddNewRoutePointButtonClick();
        initRoutePointsRecyclerView();
    }

    private void initAddNewRoutePointButtonClick() {
        addRoutePointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddRoutePointsActivity.this, AddNewRoutePointActivity.class);
                intent.putExtra(AddNewRoutePointActivity.ROUTE_EXTRA_TAG, route);
                startActivityForResult(intent, AddNewRoutePointActivity.ADD_NEW_ROUTE_POINT_ACTIVITY_TAG);
            }
        });
    }

    private void initRoutePointsRecyclerView() {
        routePointsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        routePointsRecyclerViewAdapter = new RoutePointsRecyclerViewAdapter(route, this, this);
        routePointsRecyclerView.setAdapter(routePointsRecyclerViewAdapter);
        routePointsRecyclerView.addItemDecoration(new DividerItemDecoration(AddRoutePointsActivity.this, R.drawable.divider));
//        callback = new SimpleItemTouchHelperCallback(routePointsRecyclerViewAdapter);
//        touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(routePointsRecyclerView);
        handleRoutesVisibilityLayouts();
    }

    private void handleRoutesVisibilityLayouts() {
        if (route.getRoutePoints().size() == 0) {
            noRoutePointsTextView.setVisibility(View.VISIBLE);
            routePointsRecyclerView.setVisibility(View.GONE);
        } else {
            routePointsRecyclerView.setVisibility(View.VISIBLE);
            noRoutePointsTextView.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AddNewRoutePointActivity.ADD_NEW_ROUTE_POINT_ACTIVITY_TAG: {
                    route = (Route) data.getExtras().getParcelable(ROUTE_RESULT_TAG);
                    initRoutePointsRecyclerView();
                    updateRoute(AddRoutePointsActivity.this, route);
                    routePointsRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public static void updateRoute(AppCompatActivity appCompatActivity, Route route) {
        Utils.getSQLiteHelper(appCompatActivity).updateRoute(route);
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra(ROUTE_RESULT_TAG, route);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    public void deleteRoutePoint(RoutePoint routePoint) {
        route.deleteRoutePointId(routePoint);
        routePointsRecyclerViewAdapter.notifyDataSetChanged();
        handleRoutesVisibilityLayouts();
        updateRoute(AddRoutePointsActivity.this, route);
    }

    @Override
    public void onDoneGetTime(int timerKind, int hour, int minute) {
        EditRoutePointTimeDialog editRoutePointTimeDialog = (EditRoutePointTimeDialog) getSupportFragmentManager().findFragmentByTag(EditRoutePointTimeDialog.TAG);
        if (editRoutePointTimeDialog != null) {
            if (timerKind == TimePickerFragment.START_TIMER_KIND) {
                AddOrUpdateNewRouteActivity.editStartTimeTextView(
                        hour,
                        minute,
                        editRoutePointTimeDialog.getEndTimeTextView(),
                        editRoutePointTimeDialog.getStartTimeTextView(),
                        AddRoutePointsActivity.this,
                        R.id.activity_add_route_points);
            } else if (timerKind == TimePickerFragment.END_TIMER_KIND) {
                AddOrUpdateNewRouteActivity.editEndTimeTextView(
                        hour,
                        minute,
                        editRoutePointTimeDialog.getStartTimeTextView(),
                        editRoutePointTimeDialog.getEndTimeTextView(),
                        AddRoutePointsActivity.this,
                        R.id.activity_add_route_points);
            }
        }
    }

    @Override
    public void editRoutePointTime(RoutePoint routePoint, Time startTime, Time endTime) {
        int index = route.getRoutePoints().indexOf(routePoint);
        route.getRoutePoints().get(index).setStartTime(startTime);
        route.getRoutePoints().get(index).setEndTime(endTime);
        routePointsRecyclerViewAdapter.notifyDataSetChanged();
        updateRoute(AddRoutePointsActivity.this, route);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GET_LOCATION_PERMISSION_REQUEST_CODE){
            PermissionsUtils.handleRequestPermissionResult(grantResults,this,permissions,"Te pozwolenia są potrzebne aby poprawnie wyznaczać trasę.","Bez zaakceptowania pozwolenia nie możesz wyznaczać punktów trasy. Przejdź do ustawień aplikacji aby edytować pozwolenia.",
                    new PermissionsUtils.OnPermissionResultListener() {
                        @Override
                        public void onDone() {
                            permissionAcceptedOptimzeRoute();
                        }
                    });
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
