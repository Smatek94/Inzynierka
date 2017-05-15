package com.example.mateuszskolimowski.inzynierka.activities.add_route_points;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.AreYouSureDialog;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.EditRoutePointTimeDialog;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.example.mateuszskolimowski.inzynierka.views.ItemTouchHelperAdapterInterface;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Mateusz Skolimowski on 26.03.2017.
 */
public class RoutePointsRecyclerViewAdapter
        extends RecyclerView.Adapter<RoutePointsRecyclerViewAdapter.ViewHolder>
//        implements ItemTouchHelperAdapterInterface
{

    private final Context context;
    private Route route;
    private AppCompatActivity appCompatActivity;
    private View v;

    public RoutePointsRecyclerViewAdapter(Route route, Context context, AppCompatActivity appCompatActivity) {
        this.route = route;
        this.context = context;
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    public RoutePointsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_point, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoutePoint routePoint = route.getRoutePoints().get(position);
        holder.routePointsNameTextView.setText(route.getRoutePoints().get(position).getRoutePointName());
        holder.timeTextView.setText(route.getRoutePoints().get(position).getRoutePointStartTime().toString() + " - " + route.getRoutePoints().get(position).getRoutePointEndTime().toString());
        holder.editRoutePointImageView.setOnClickListener(new EditRoutePointClickListener(routePoint));
        holder.deleteRoutePointImageView.setOnClickListener(new DeleteRoutePointClickListener(routePoint));
        holder.moveDownRoutePointImageView.setOnClickListener(new MoveDownRoutePointClickListener(routePoint));
        holder.moveUpRoutePointImageView.setOnClickListener(new MoveUpRoutePointClickListener(routePoint));
    }

    @Override
    public int getItemCount() {
        return route.getRoutePoints().size();
    }

   /* @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(route.getRoutePoints(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(route.getRoutePoints(), i, i - 1);
            }
        }
        AddRoutePointsActivity.updateRoute(appCompatActivity,route);
        notifyItemMoved(fromPosition, toPosition);
    }
*/

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View routePointView;
        private final TextView routePointsNameTextView;
        private final TextView timeTextView;
        private final ImageView deleteRoutePointImageView;
        private final ImageView editRoutePointImageView;
        private final ImageView moveDownRoutePointImageView;
        private final ImageView moveUpRoutePointImageView;

        public ViewHolder(View v) {
            super(v);
            routePointView = v.findViewById(R.id.route_point_view);
            routePointsNameTextView = (TextView) v.findViewById(R.id.route_points_name_textview);
            timeTextView = (TextView) v.findViewById(R.id.time_textview);
            deleteRoutePointImageView = (ImageView) v.findViewById(R.id.delete_route_point_imageview);
            editRoutePointImageView = (ImageView) v.findViewById(R.id.edit_route_point_imageview);
            moveDownRoutePointImageView = (ImageView) v.findViewById(R.id.move_down_route_point_imageview);
            moveUpRoutePointImageView = (ImageView) v.findViewById(R.id.move_up_route_point_imageview);
        }
    }

    private class EditRoutePointClickListener implements View.OnClickListener {

        private final RoutePoint routePoint;

        public EditRoutePointClickListener(RoutePoint routePoint) {
            this.routePoint = routePoint;
        }

        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
            EditRoutePointTimeDialog editRoutePointTimeDialog = (EditRoutePointTimeDialog) fragmentManager.findFragmentByTag(EditRoutePointTimeDialog.TAG);
            if (editRoutePointTimeDialog == null) {
                editRoutePointTimeDialog = EditRoutePointTimeDialog.newInstance(routePoint);
                editRoutePointTimeDialog.show(fragmentManager.beginTransaction(), EditRoutePointTimeDialog.TAG);
            }
        }
    }

    private class DeleteRoutePointClickListener implements View.OnClickListener {
        private final RoutePoint routePoint;

        public DeleteRoutePointClickListener(RoutePoint routePoint) {
            this.routePoint = routePoint;
        }

        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
            AreYouSureDialog areYouSureDialog = (AreYouSureDialog) fragmentManager.findFragmentByTag(AreYouSureDialog.TAG);
            if (areYouSureDialog == null) {
                areYouSureDialog = AreYouSureDialog.newInstance(appCompatActivity.getString(R.string.are_you_sure_you_want_to_delete_this_route_point), null, routePoint);
                areYouSureDialog.show(fragmentManager.beginTransaction(), AreYouSureDialog.TAG);
            }
        }
    }

    private class MoveDownRoutePointClickListener implements View.OnClickListener {
        private final RoutePoint routePoint;

        public MoveDownRoutePointClickListener(RoutePoint routePoint) {
            this.routePoint = routePoint;
        }

        @Override
        public void onClick(View view) {
            int index = route.getRoutePoints().indexOf(routePoint);
            if(index != -1 && index != route.getRoutePoints().size() -1){
                Collections.swap(route.getRoutePoints(),index,index + 1);
                AddRoutePointsActivity.updateRoute(appCompatActivity,route);
                notifyItemMoved(index, index + 1);
            }
        }
    }

    private class MoveUpRoutePointClickListener implements View.OnClickListener {
        private final RoutePoint routePoint;

        public MoveUpRoutePointClickListener(RoutePoint routePoint) {
            this.routePoint = routePoint;
        }

        @Override
        public void onClick(View view) {
            int index = route.getRoutePoints().indexOf(routePoint);
            if(index != -1 && index != 0){
                Collections.swap(route.getRoutePoints(),index,index -1);
                AddRoutePointsActivity.updateRoute(appCompatActivity,route);
                notifyItemMoved(index, index - 1);
            }
        }
    }
}
