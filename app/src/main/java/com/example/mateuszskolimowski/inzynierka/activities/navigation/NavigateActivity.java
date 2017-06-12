package com.example.mateuszskolimowski.inzynierka.activities.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.AddToCalendarDialog;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.utils.SharedPreferencesUtils;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.example.mateuszskolimowski.inzynierka.views.DividerItemDecoration;

import java.util.ArrayList;

public class NavigateActivity extends AppCompatActivity {

    public static final String ROUTE_ID_EXTRA_TAG = NavigateActivity.class.getName() + "ROUTE_ID_EXTRA_TAG";
    public static final int GOOGLE_NAVIGATION_INTENT_REQUEST_CODE = 8;
    private RecyclerView routePointsNavigationRecyclerView;
    private RoutePointsNavigationRecyclerViewAdapter routePointsNavigationRecyclerViewAdapter;
    private MenuInflater inflater;
    private RoutePoint actualRoutePointNavigated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        getLayoutComponents();
        setUpGUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.route_navigation_reset_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset: {
                resetRoutePointsVisited();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLayoutComponents() {
        routePointsNavigationRecyclerView = (RecyclerView) findViewById(R.id.route_points_recycler_view);
    }

    private void setUpGUI() {
        routePointsNavigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        routePointsNavigationRecyclerViewAdapter = new RoutePointsNavigationRecyclerViewAdapter(
                getIntent().getExtras().getInt(ROUTE_ID_EXTRA_TAG),
                this,
                new NavigationCallback() {
                    @Override
                    public void navigationLaunched(RoutePoint routePoint) {
                        actualRoutePointNavigated = routePoint;
                    }

                    @Override
                    public void routePointCheckedOrUncheked(RoutePoint routePoint,boolean isChecked) {
                        uptdateRoute(routePoint,isChecked);
                    }
                });
        routePointsNavigationRecyclerView.setAdapter(routePointsNavigationRecyclerViewAdapter);
        routePointsNavigationRecyclerView.addItemDecoration(new DividerItemDecoration(NavigateActivity.this, R.drawable.divider));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_NAVIGATION_INTENT_REQUEST_CODE) {
            showAddToCalendarDialog();
            sendToServerActualRoute();
        }
    }

    private void resetRoutePointsVisited() {
        Route route = getRouteFromIdExtra();
        ArrayList<RoutePoint> routePoints = route.getRoutePoints();
        for (int i = 0; i < routePoints.size(); i++) {
            routePoints.get(i).setVisited(false);
        }
        route.setRoutePoints(routePoints);
        Utils.getSQLiteHelper(NavigateActivity.this).updateRoute(route);
        routePointsNavigationRecyclerViewAdapter.setRoutePointArrayList(route.getRoutePoints());
        routePointsNavigationRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void uptdateRoute(RoutePoint routePoint, boolean isChecked) {
        Route route = getRouteFromIdExtra();
        ArrayList<RoutePoint> routePoints = route.getRoutePoints();
        for (int i = 0; i < routePoints.size(); i++) {
            if (routePoints.get(i).getId().equals(routePoint.getId()))
                routePoints.get(i).setVisited(isChecked);
        }
        route.setRoutePoints(routePoints);
        Utils.getSQLiteHelper(NavigateActivity.this).updateRoute(route);
    }

    private Route getRouteFromIdExtra() {
        int routeId = getIntent().getExtras().getInt(ROUTE_ID_EXTRA_TAG);
        for (Route r : Utils.getSQLiteHelper(NavigateActivity.this).getRoutes()) {
            if (routeId == r.getId()) {
                return r;
            }
        }
        return null;
    }

    private void sendToServerActualRoute() {
        //fixme wyslac do serwera ktore punkty trasy sa odznaczone
    }

    private void showAddToCalendarDialog() {
        if (shouldDialogBeShown()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            AddToCalendarDialog addToCalendarDialog = (AddToCalendarDialog) fragmentManager.findFragmentByTag(AddToCalendarDialog.TAG);
            if (addToCalendarDialog == null) {
                addToCalendarDialog = AddToCalendarDialog.newInstance(actualRoutePointNavigated);
                addToCalendarDialog.show(fragmentManager.beginTransaction(), AddToCalendarDialog.TAG);
            }
        }
    }

    private boolean shouldDialogBeShown() {
        return SharedPreferencesUtils.shouldCalendarDialogBeShown(getApplicationContext());
    }

    public interface NavigationCallback {
        public void navigationLaunched(RoutePoint routePoint);
        public void routePointCheckedOrUncheked(RoutePoint routePoint, boolean isChecked);
    }
}
