package com.example.mateuszskolimowski.inzynierka.views;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;

/**
 * Created by Mateusz Skolimowski on 07.04.2017.
 */

public class RoutePointItemView extends FrameLayout {

    private final RoutePoint routePoint;
    private TextView pointNameTextView;
    private TextView pointTimeTextView;

    public RoutePointItemView(Context context, RoutePoint routePoint) {
        super(context);
        this.routePoint = routePoint;
        initView(context);
    }

    private void initView(Context context){
        View view = inflate(context, R.layout.route_point_item_layout,null);
        pointNameTextView = (TextView) view.findViewById(R.id.point_name_textview);
        pointTimeTextView = (TextView) view.findViewById(R.id.point_time_textview);
        initPointTextViews();
        addView(view);
    }

    private void initPointTextViews() {
        pointNameTextView.setText(routePoint.getPlaceName());
        pointTimeTextView.setText(routePoint.getStartTime().toString() + " - " + routePoint.getEndTime().toString());
    }
}
