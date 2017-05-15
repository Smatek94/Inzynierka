package com.example.mateuszskolimowski.inzynierka.dialog_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.utils.SharedPreferencesUtils;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;


/**
 * Created by Mateusz Skolimowski on 22.03.2017.
 */

public class AddToCalendarDialog extends DialogFragment {

    public static final String TAG = AddToCalendarDialog.class.getCanonicalName() + "TAG";
    private static final String ACTUAL_ROUTE_POINT_EXTRA_TAG = AddToCalendarDialog.class.getName() + "ACTUAL_ROUTE_POINT_EXTRA_TAG";
    private EditText titleEditText;
    private EditText descriptionEditText;
    private CheckBox showCalendarCheckBox;
    private TextView okTextView;
    private TextView noTextView;

    public static AddToCalendarDialog newInstance(RoutePoint actualRoutePointNavigated) {
        AddToCalendarDialog fragment = new AddToCalendarDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ACTUAL_ROUTE_POINT_EXTRA_TAG,actualRoutePointNavigated);
        fragment.setArguments(bundle);
        return fragment;
    }

    public AddToCalendarDialog() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add_to_calendar, container, false);
        initDialog(v);
        setUpGUI((RoutePoint) getArguments().getParcelable(ACTUAL_ROUTE_POINT_EXTRA_TAG));
        return v;
    }

    private void setUpGUI(RoutePoint routePoint) {
        titleEditText.setText(routePoint.getRoutePointName());
        descriptionEditText.setText("Miejsce " + routePoint.getRoutePointName() + " odwiedzone o " + getActualTime() + " przy pomocy aplikacji Inzynierka"); //fixme zmienic inzynierka
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfHideCalendar();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, titleEditText.getText().toString());
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis());
                intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
                intent.putExtra(CalendarContract.Events.DESCRIPTION,descriptionEditText.getText().toString());
                startActivity(intent);
                dismiss();
            }
        });
        noTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfHideCalendar();
                dismiss();
            }
        });
    }

    private String getActualTime() {
        Date date = new Date(System.currentTimeMillis());
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return formatTime(c.get(Calendar.HOUR)) + ":" + formatTime(c.get(Calendar.MINUTE)) +
                " dnia " + formatTime(c.get(Calendar.DAY_OF_MONTH)) + "." + formatTime(c.get(Calendar.MONTH)) + "." + c.get(Calendar.YEAR);
    }

    private String formatTime(int time){
        if(time > 9){
            return "" + time;
        } else {
            return "0" + time;
        }
    }

    private void checkIfHideCalendar(){
        SharedPreferencesUtils.setShouldCalendarDialogBeShown(getContext(),!showCalendarCheckBox.isChecked());
    }

    private void initDialog(View v) {
        titleEditText = (EditText) v.findViewById(R.id.calendar_event_title);
        descriptionEditText = (EditText) v.findViewById(R.id.calendar_event_description);
        showCalendarCheckBox = (CheckBox) v.findViewById(R.id.show_calendar_dialog_checkbox);
        okTextView = (TextView) v.findViewById(R.id.ok_textview);
        noTextView = (TextView) v.findViewById(R.id.no_textview);
    }
}
