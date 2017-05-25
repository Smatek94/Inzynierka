package com.example.mateuszskolimowski.inzynierka.dialog_fragments.lately_added_route_points_dialog;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;

import java.util.ArrayList;

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
        final RoutePointDestination routePointDestination = (RoutePointDestination) routePointArrayList.get(position);
//        holder.routePointNameTextView.setText(routePointDestination.getRoutePointName());
        holder.itemLatelyAddedRoutePoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latelyAddedRoutePointsDialogInterface.latelyAddedRoutePointDialogCallback(routePointDestination);
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
