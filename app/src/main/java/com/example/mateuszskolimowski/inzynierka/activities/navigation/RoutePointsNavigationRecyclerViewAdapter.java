package com.example.mateuszskolimowski.inzynierka.activities.navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Mateusz Skolimowski on 26.03.2017.
 */
public class RoutePointsNavigationRecyclerViewAdapter
        extends RecyclerView.Adapter<RoutePointsNavigationRecyclerViewAdapter.ViewHolder> {

    private ArrayList<RoutePoint> routePointArrayList;
    private final NavigateActivity.NavigationCallback navigationCallback;
    private View v;
    private AppCompatActivity appCompatActivity;

    public RoutePointsNavigationRecyclerViewAdapter(int routeId, AppCompatActivity appCompatActivity, NavigateActivity.NavigationCallback navigationCallback) {
        Route route = null;
        for(Route r : Utils.getSQLiteHelper(appCompatActivity).getRoutes()){
            if(r.getId() == routeId){
                route = r;
                break;
            }
        }
        this.routePointArrayList = route.getRoutePoints();
        this.appCompatActivity = appCompatActivity;
        this.navigationCallback = navigationCallback;
    }

    public void setRoutePointArrayList(ArrayList<RoutePoint> routePointArrayList){
        this.routePointArrayList = routePointArrayList;
    }

    @Override
    public RoutePointsNavigationRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_point_navigation, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RoutePoint routePoint = routePointArrayList.get(position);
        holder.routePointsNameTextView.setText(routePoint.getRoutePointName());
        holder.timeTextView.setText(routePoint.getRoutePointStartTime().toString() + " - " + routePoint.getRoutePointEndTime().toString());
        holder.navigateToRoutePointImageView.setOnClickListener(new OnNavigateToRoutePointClickListerner(routePoint,holder));
        checkCheckBox(routePoint.getVisited(),holder.routePointVisitedCheckBox);
        holder.routePointVisitedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkRoutePoint(b,routePoint,holder.routePointVisitedCheckBox);
            }
        });
    }

    private void checkCheckBox(int visited, CheckBox routePointVisitedCheckBox) {
        if(visited == RoutePoint.VISITED){
            routePointVisitedCheckBox.setChecked(true);
        } else {
            routePointVisitedCheckBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return routePointArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView routePointsNameTextView;
        private final TextView timeTextView;
        private final ImageView navigateToRoutePointImageView;
        private final CheckBox routePointVisitedCheckBox;

        public ViewHolder(View v) {
            super(v);
            routePointsNameTextView = (TextView) v.findViewById(R.id.route_points_name_textview);
            timeTextView = (TextView) v.findViewById(R.id.time_textview);
            navigateToRoutePointImageView = (ImageView) v.findViewById(R.id.navigate_to_route_point_imageview);
            routePointVisitedCheckBox = (CheckBox) v.findViewById(R.id.route_point_visited_checkbox);
        }
    }

    private class OnNavigateToRoutePointClickListerner implements View.OnClickListener {
        private final RoutePoint routePoint;
        private final ViewHolder holder;

        public OnNavigateToRoutePointClickListerner(RoutePoint routePoint, ViewHolder holder) {
            this.routePoint = routePoint;
            this.holder = holder;
        }

        @Override
        public void onClick(View view) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+routePoint.getRoutePointLatLng().latitude+","+routePoint.getRoutePointLatLng().longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            appCompatActivity.startActivityForResult(mapIntent, NavigateActivity.GOOGLE_NAVIGATION_INTENT_REQUEST_CODE);
            checkRoutePoint(true,routePoint,holder.routePointVisitedCheckBox);
            navigationCallback.navigationLaunched(routePoint);
        }
    }

    private void checkRoutePoint(boolean isCheck, RoutePoint routePoint, CheckBox routePointVisitedCheckBox) {
        routePoint.setVisited(isCheck);
        routePointVisitedCheckBox.setChecked(isCheck);
        navigationCallback.routePointCheckedOrUncheked(routePoint,isCheck);
    }
}
