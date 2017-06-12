package com.example.mateuszskolimowski.inzynierka.activities.routes_list;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.activities.add_route_points.AddRoutePointsActivity;
import com.example.mateuszskolimowski.inzynierka.activities.show_on_map.ShowRoutePointsOnMapActivity;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.AreYouSureDialog;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.example.mateuszskolimowski.inzynierka.views.DividerItemDecoration;

import java.util.ArrayList;

public class RoutesListActivity extends AppCompatActivity
    implements AreYouSureDialog.DeleteRouteInterface {

    public static final int ADD_NEW_ROUTE_ACTIVITY_CALLBACK_REQUEST_CODE = 1;
    public static final int UPDATE_ROUTE_ACTIVITY_CALLBACK_REQUEST_CODE = 2;
    public static final int ADD_ROUTE_POINTS_ACTIVITY_REQUEST_CODE = 3;

    private ArrayList<Route> routes;
    private TextView noRoutesTextView;
    private Button addRouteButton;
    private RecyclerView routesRecyclerView;
    private RoutesRecyclerViewAdapter routesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        getLayoutComponents();
        setUpGUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.route_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(RoutesListActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLayoutComponents() {
        noRoutesTextView = (TextView) findViewById(R.id.no_routes_textview);
        addRouteButton = (Button) findViewById(R.id.add_route_button);
        routesRecyclerView = (RecyclerView) findViewById(R.id.routes_recycler_view);
    }

    private void setUpGUI() {
        getRoutesFromSQLite();
        initAddRouteButton();
        initRoutesRecyclerView();
    }

    private void initRoutesRecyclerView() {
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        routesRecyclerViewAdapter = new RoutesRecyclerViewAdapter(routes, this, this);
        routesRecyclerView.setAdapter(routesRecyclerViewAdapter);
        routesRecyclerView.addItemDecoration(new DividerItemDecoration(RoutesListActivity.this, R.drawable.divider));
    }

    private void initAddRouteButton() {
        addRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.debugLog("wlaczam activity do dodawania nowej trasy");
                Intent intent = new Intent(RoutesListActivity.this, AddOrUpdateNewRouteActivity.class);
                startActivityForResult(intent, ADD_NEW_ROUTE_ACTIVITY_CALLBACK_REQUEST_CODE);
            }
        });
    }

    private void getRoutesFromSQLite() {
        routes = Utils.getSQLiteHelper(this).getRoutes();
        handleRoutesVisibilityLayouts();
    }

    private void handleRoutesVisibilityLayouts() {
        if (routes.size() == 0) {
            noRoutesTextView.setVisibility(View.VISIBLE);
            routesRecyclerView.setVisibility(View.GONE);
        } else {
            routesRecyclerView.setVisibility(View.VISIBLE);
            noRoutesTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ADD_NEW_ROUTE_ACTIVITY_CALLBACK_REQUEST_CODE: {
                    addNewRouteToRecyclerViewAndDatabase(getRouteFromCallback(data, AddOrUpdateNewRouteActivity.ROUTE_RESULT_TAG));
                    break;
                }
                case UPDATE_ROUTE_ACTIVITY_CALLBACK_REQUEST_CODE: {
                    Utils.getSQLiteHelper(this).updateRoute(getRouteFromCallback(data, AddOrUpdateNewRouteActivity.ROUTE_RESULT_TAG));
                    updateRouteInRecyclerViewAndDatabase(getRouteFromCallback(data, AddOrUpdateNewRouteActivity.ROUTE_RESULT_TAG));
                    break;
                }
                case ADD_ROUTE_POINTS_ACTIVITY_REQUEST_CODE: {
                    updateRouteInRecyclerViewAndDatabase(getRouteFromCallback(data, AddRoutePointsActivity.ROUTE_RESULT_TAG));
                    break;
                }
            }
        }
    }

    private void updateRouteInRecyclerViewAndDatabase(Route routeResultDataFromCallback) {
        changeRouteInList(routeResultDataFromCallback);
        routesRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void changeRouteInList(Route newRoute) {
        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).getId() == newRoute.getId()) {
                routes.set(i, newRoute);
                break;
            }
        }
    }

    private void addNewRouteToRecyclerViewAndDatabase(Route routeResultDataFromCallback) {
        Utils.getSQLiteHelper(this).insertNewRoute(routeResultDataFromCallback);
        routes.add(routeResultDataFromCallback);
        handleRoutesVisibilityLayouts();
        routesRecyclerViewAdapter.notifyDataSetChanged();
    }

    private Route getRouteFromCallback(Intent data, String tag) {
        Bundle bundle = data.getExtras();
        return bundle.getParcelable(tag);
    }

    @Override
    public void deleteRoute(Route route) {
        Utils.getSQLiteHelper(this).deleteRoute(route);
        routesRecyclerViewAdapter.deleteRoute(route);
        handleRoutesVisibilityLayouts();
    }
}
