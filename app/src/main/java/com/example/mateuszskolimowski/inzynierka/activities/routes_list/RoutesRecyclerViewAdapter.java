package com.example.mateuszskolimowski.inzynierka.activities.routes_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.example.mateuszskolimowski.inzynierka.views.GrayDividerView;
import com.example.mateuszskolimowski.inzynierka.views.RoutePointItemView;
import com.example.mateuszskolimowski.inzynierka.activities.add_route_points.AddRoutePointsActivity;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.AreYouSureDialog;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.utils.Animations;

import java.util.ArrayList;

/**
 * Created by Mateusz Skolimowski on 26.03.2017.
 */
public class RoutesRecyclerViewAdapter
        extends RecyclerView.Adapter<RoutesRecyclerViewAdapter.ViewHolder>
//        implements ItemTouchHelperAdapterInterface
        {

    private final ArrayList<Route> routesList;
    private final Context context;
    private AppCompatActivity appCompatActivity;
    private View v;

    public RoutesRecyclerViewAdapter(ArrayList<Route> routesList, Context context, AppCompatActivity appCompatActivity) {
        this.routesList = routesList;
        this.context = context;
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    public RoutesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void deleteRoute(Route route){
        routesList.remove(route);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Route route = routesList.get(position);
        holder.routeNameTextView.setText(route.getRouteName());
        holder.timeTextView.setText(route.getStartTime().toString() + " - " + route.getEndTime().toString());
        holder.editRouteImageView.setOnClickListener(new EditRouteImageViewClickListener(position));
        holder.deleteRouteImageView.setOnClickListener(new DeleteRouteImageViewClickListener(position));
        holder.copyRouteImageView.setOnClickListener(new CopyRouteImageViewClickListener(position));
        holder.expandRouteImageView.setOnClickListener(new ExpandImageViewClickListener(context,holder.routePointsLayout,holder.expandRouteImageView,position));
        holder.item.setOnClickListener(new AddRoutePointsActivityClickListener(position));
        initRoutePointsLayout(position,holder);
    }

    private void initRoutePointsLayout(int position, ViewHolder holder) {
        LinearLayout routePointsLinearLayout = (LinearLayout) holder.routePointsLayout.findViewById(R.id.route_points_linear_layout);
        routePointsLinearLayout.removeAllViews();
        for(int i = 0 ; i < routesList.get(position).getRoutePoints().size() ; i++){
            addRoutePointToLayout(routesList.get(position).getRoutePoints().get(i),routePointsLinearLayout);
            if(i < routesList.get(position).getRoutePoints().size() - 1 ){
                routePointsLinearLayout.addView(new GrayDividerView(context,R.layout.gray_divider));
            }
        }
    }

    private void addRoutePointToLayout(RoutePoint rp, LinearLayout routePointsLinearLayout) {
        routePointsLinearLayout.addView(new RoutePointItemView(context,rp));
    }

    @Override
    public int getItemCount() {
        return routesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeTextView;
        private final ImageView editRouteImageView;
        private final ImageView deleteRouteImageView;
        private final ImageView copyRouteImageView;
        private final ImageView expandRouteImageView;
        private final View item;
        public TextView routeNameTextView;
        public RelativeLayout routePointsLayout;

        public ViewHolder(View v) {
            super(v);
            item = v;
            routeNameTextView = (TextView) v.findViewById(R.id.route_name_textview);
            timeTextView = (TextView) v.findViewById(R.id.time_textview);
            editRouteImageView = (ImageView) v.findViewById(R.id.edit_route_imageview);
            deleteRouteImageView = (ImageView) v.findViewById(R.id.delete_route_imageview);
            copyRouteImageView = (ImageView) v.findViewById(R.id.copy_route_imageview);
            expandRouteImageView = (ImageView) v.findViewById(R.id.expand_route_imageview);
            routePointsLayout = (RelativeLayout) v.findViewById(R.id.route_points_expandable_layout);
        }
    }

    private class RouteHoldingClass{

        protected int position;

        RouteHoldingClass(int position){
            this.position = position;
        }
    }

    private class AddRoutePointsActivityClickListener extends RouteHoldingClass implements View.OnClickListener {

        AddRoutePointsActivityClickListener(int position) {
            super(position);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(appCompatActivity, AddRoutePointsActivity.class);
            intent.putExtra(AddRoutePointsActivity.ROUTE_EXTRA_TAG,routesList.get(position));
            appCompatActivity.startActivityForResult(intent,RoutesListActivity.ADD_ROUTE_POINTS_ACTIVITY_REQUEST_CODE);
        }
    }

    private class CopyRouteImageViewClickListener extends RouteHoldingClass implements View.OnClickListener {

        CopyRouteImageViewClickListener(int position) {
            super(position);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(appCompatActivity, AddOrUpdateNewRouteActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(AddOrUpdateNewRouteActivity.ROUTE_EXTRA_TAG, routesList.get(position));
            intent.putExtras(bundle);
            appCompatActivity.startActivityForResult(intent, RoutesListActivity.ADD_NEW_ROUTE_ACTIVITY_CALLBACK_REQUEST_CODE);
        }
    }

    private class EditRouteImageViewClickListener extends RouteHoldingClass implements View.OnClickListener {

        EditRouteImageViewClickListener(int position) {
            super(position);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(appCompatActivity, AddOrUpdateNewRouteActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(AddOrUpdateNewRouteActivity.ROUTE_EXTRA_TAG, routesList.get(position));
            bundle.putInt(AddOrUpdateNewRouteActivity.UPDATED_ROUTE_ID_EXTRA_TAG, routesList.get(position).getId());
            intent.putExtras(bundle);
            appCompatActivity.startActivityForResult(intent, RoutesListActivity.UPDATE_ROUTE_ACTIVITY_CALLBACK_REQUEST_CODE);
        }
    }

    private class DeleteRouteImageViewClickListener extends RouteHoldingClass implements View.OnClickListener {

        DeleteRouteImageViewClickListener(int position) {
            super(position);
        }

        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
            AreYouSureDialog areYouSureDialog = (AreYouSureDialog) fragmentManager.findFragmentByTag(AreYouSureDialog.TAG);
            if (areYouSureDialog == null) {
                areYouSureDialog = AreYouSureDialog.newInstance(appCompatActivity.getString(R.string.are_you_sure_you_want_to_delete_this_route),routesList.get(position),null);
                areYouSureDialog.show(fragmentManager.beginTransaction(), AreYouSureDialog.TAG);
            }
        }
    }

    private class ExpandImageViewClickListener extends RouteHoldingClass implements View.OnClickListener {

        private final Context context;
        private ArrayList<RoutePoint> routePoints;
        private View layout;
        private ImageView expandArrowImageView;

        public ExpandImageViewClickListener(Context context, View layout, ImageView expandArrowImageView, int position) {
            super(position);
            this.context = context;
            this.layout = layout;
            this.expandArrowImageView = expandArrowImageView;
        }

        @Override
        public void onClick(View view) {
            this.routePoints = routesList.get(position).getRoutePoints();
            if(routePoints.size() != 0) {
                handleAnimation();
            } else {
                Utils.showMsgDialog(appCompatActivity,appCompatActivity.getString(R.string.no_points_in_route));
            }
        }

        private void handleAnimation(){
            if (layout.getVisibility() == View.VISIBLE) {
                Animations.collapse(layout, 2);
                Animations.rotateRightFrom180(expandArrowImageView, context);
            } else {
                Animations.expand(layout, 2);
                Animations.rotateRight(expandArrowImageView, context);
            }
        }
    }
}
