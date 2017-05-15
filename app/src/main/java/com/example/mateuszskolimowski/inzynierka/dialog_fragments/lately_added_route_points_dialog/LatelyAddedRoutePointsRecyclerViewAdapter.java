package com.example.mateuszskolimowski.inzynierka.dialog_fragments.lately_added_route_points_dialog;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.activities.add_route_points.AddRoutePointsActivity;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.AreYouSureDialog;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.EditRoutePointTimeDialog;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.views.ItemTouchHelperAdapterInterface;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Mateusz Skolimowski on 26.03.2017.
 */
public class LatelyAddedRoutePointsRecyclerViewAdapter
        extends RecyclerView.Adapter<LatelyAddedRoutePointsRecyclerViewAdapter.ViewHolder> {

    private final LatelyAddedRoutePointsDialog.LatelyAddedRoutePointsDialogInterface latelyAddedRoutePointsDialogInterface;
    private ArrayList<Parcelable> routePointArrayList;

    public LatelyAddedRoutePointsRecyclerViewAdapter(ArrayList<Parcelable> routePointArrayList, LatelyAddedRoutePointsDialog.LatelyAddedRoutePointsDialogInterface latelyAddedRoutePointsDialogInterface) {
        this.routePointArrayList = routePointArrayList;
        this.latelyAddedRoutePointsDialogInterface = latelyAddedRoutePointsDialogInterface;
    }

    @Override
    public LatelyAddedRoutePointsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lately_added_route_points, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RoutePoint routePoint = (RoutePoint) routePointArrayList.get(position);
        holder.routePointNameTextView.setText(routePoint.getRoutePointName());
        holder.itemLatelyAddedRoutePoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latelyAddedRoutePointsDialogInterface.latelyAddedRoutePointDialogCallback(routePoint);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routePointArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView routePointNameTextView;
        private final View itemLatelyAddedRoutePoint;

        public ViewHolder(View v) {
            super(v);
            routePointNameTextView = (TextView) v.findViewById(R.id.route_points_name_textview);
            itemLatelyAddedRoutePoint = v.findViewById(R.id.item_lately_added_route_point);
        }
    }
}
