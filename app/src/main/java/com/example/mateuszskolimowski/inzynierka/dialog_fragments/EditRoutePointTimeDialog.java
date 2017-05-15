package com.example.mateuszskolimowski.inzynierka.dialog_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.activities.routes_list.AddOrUpdateNewRouteActivity;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.Time;


/**
 * Created by Mateusz Skolimowski on 22.03.2017.
 */

public class EditRoutePointTimeDialog extends DialogFragment {

    public static final String TAG = EditRoutePointTimeDialog.class.getCanonicalName() + "TAG";
    private static final String ROUTE_POINT_ARG_TAG = EditRoutePointTimeDialog.class.getName() + " ROUTE_POINT_ARG_TAG";
    private View startTimeLayout;
    private TextView startTimeTextView;
    private View endTimeLayout;
    private TextView endTimeTextView;
    private TextView okTextView;
    private EditRoutePointTimeInterace editRoutePointTimeListener;
    private RoutePoint routePoint;

    public static EditRoutePointTimeDialog newInstance(RoutePoint routePoint) {
        EditRoutePointTimeDialog fragment = new EditRoutePointTimeDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ROUTE_POINT_ARG_TAG,routePoint);
        fragment.setArguments(bundle);
        return fragment;
    }

    public EditRoutePointTimeDialog() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_edit_route_point_time_layout, container, false);
        routePoint = getArguments().getParcelable(ROUTE_POINT_ARG_TAG);
        getLayoutComponents(v);
        setUpGUI();
        return v;
    }

    private void getLayoutComponents(View v) {
        View timeChoosingLayout = v.findViewById(R.id.time_choosing_layout);
        startTimeLayout = timeChoosingLayout.findViewById(R.id.start_time_layout);
        startTimeTextView = (TextView) startTimeLayout.findViewById(R.id.time_textview);
        endTimeLayout = timeChoosingLayout.findViewById(R.id.end_time_layout);
        endTimeTextView = (TextView) endTimeLayout.findViewById(R.id.time_textview);
        okTextView = (TextView) v.findViewById(R.id.ok_textview);
    }

    private void setUpGUI() {
        AddOrUpdateNewRouteActivity.initTimeViews(
                startTimeLayout,
                getString(R.string.start_time),
                endTimeLayout,getString(R.string.end_time),
                (AppCompatActivity) getActivity());
        startTimeTextView.setText(routePoint.getRoutePointStartTime().toString());
        endTimeTextView.setText(routePoint.getRoutePointEndTime().toString());
        initOkTextViewClick();
    }

    private void initOkTextViewClick() {
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editRoutePointTimeListener.editRoutePointTime(
                        (RoutePoint) getArguments().getParcelable(ROUTE_POINT_ARG_TAG),
                        new Time(AddOrUpdateNewRouteActivity.getHourFromTimeTextView(startTimeTextView),AddOrUpdateNewRouteActivity.getMinuteFromTimeTextView(startTimeTextView)),
                        new Time(AddOrUpdateNewRouteActivity.getHourFromTimeTextView(endTimeTextView),AddOrUpdateNewRouteActivity.getMinuteFromTimeTextView(endTimeTextView)));
                dismiss();
            }
        });
    }

    public TextView getEndTimeTextView() {
        return endTimeTextView;
    }

    public TextView getStartTimeTextView() {
        return startTimeTextView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditRoutePointTimeInterace) {
            editRoutePointTimeListener = (EditRoutePointTimeInterace) context;
        }
    }

    public interface EditRoutePointTimeInterace {
        void editRoutePointTime(RoutePoint routePoint, Time startTime, Time endTime);
    }
}
