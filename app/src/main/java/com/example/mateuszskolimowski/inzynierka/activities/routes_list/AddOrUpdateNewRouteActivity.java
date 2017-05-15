package com.example.mateuszskolimowski.inzynierka.activities.routes_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.TimePickerFragment;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.Time;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;

import java.util.ArrayList;


public class AddOrUpdateNewRouteActivity extends AppCompatActivity implements TimePickerFragment.FragmentResponseListener {

    private static final String START_TIME_OUT_STATE_TAG = AddOrUpdateNewRouteActivity.class.getName() + "START_TIME_OUT_STATE_TAG";
    private static final String END_TIME_OUT_STATE_TAG = AddOrUpdateNewRouteActivity.class.getName() + "END_TIME_OUT_STATE_TAG";
    private static final String ROUTE_NAME_OUT_STATE_TAG = AddOrUpdateNewRouteActivity.class.getName() + "ROUTE_NAME_OUT_STATE_TAG";

    public static final String ROUTE_EXTRA_TAG = AddOrUpdateNewRouteActivity.class.getName() + "ROUTE_ID_EXTRA_TAG";

    public static final String ROUTE_RESULT_TAG = AddOrUpdateNewRouteActivity.class.getName() + "ROUTE_RESULT_TAG";
    public static final String UPDATED_ROUTE_ID_EXTRA_TAG = AddOrUpdateNewRouteActivity.class.getName() + "UPDATED_ROUTE_ID_EXTRA_TAG";

    private View startTimeLayout;
    private View endTimeLayout;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private EditText routeNameEditText;
    private boolean buttonEnabled;
    private Button unavailableAddRouteButton;
    private Button availableAddRouteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_route);
        Utils.initToolbarTitle(getSupportActionBar(),getString(R.string.add_update_route));
        getLayoutComponents();
        setUpGUI(savedInstanceState,getIntent().getExtras());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(START_TIME_OUT_STATE_TAG,startTimeTextView.getText().toString());
        outState.putString(END_TIME_OUT_STATE_TAG,endTimeTextView.getText().toString());
        outState.putString(ROUTE_NAME_OUT_STATE_TAG,routeNameEditText.getText().toString());
    }

    private void getLayoutComponents() {
        startTimeLayout = findViewById(R.id.start_time_layout);
        startTimeTextView = (TextView) startTimeLayout.findViewById(R.id.time_textview);
        endTimeLayout = findViewById(R.id.end_time_layout);
        endTimeTextView = (TextView) endTimeLayout.findViewById(R.id.time_textview);
        routeNameEditText = (EditText) findViewById(R.id.route_name_edittext);
        unavailableAddRouteButton = (Button) findViewById(R.id.unavailable_add_route_button);
        availableAddRouteButton = (Button) findViewById(R.id.available_add_route_button);
    }

    private void setUpGUI(Bundle savedInstanceState, Bundle extras) {
        initTimeViews(startTimeLayout,getString(R.string.start_time),endTimeLayout,getString(R.string.end_time),AddOrUpdateNewRouteActivity.this);
        if(extras != null){
            Route route = extras.getParcelable(ROUTE_EXTRA_TAG);
            setDataForTextAndEditViews(
                    route.getStartTime().toString(),
                    route.getEndTime().toString(),
                    route.getRouteName());
        }
        if(savedInstanceState != null){
            setDataForTextAndEditViews(
                    savedInstanceState.getString(START_TIME_OUT_STATE_TAG),
                    savedInstanceState.getString(END_TIME_OUT_STATE_TAG),
                    savedInstanceState.getString(ROUTE_NAME_OUT_STATE_TAG));
        }
        initEditTextListener();
        initAddRouteButtonClickListener();
    }

    private void setDataForTextAndEditViews(String startTime, String endTime, String routeName) {
        startTimeTextView.setText(startTime);
        endTimeTextView.setText(endTime);
        routeNameEditText.setText(routeName);
        checkIfAllInfoAvailable();
    }

    private void initAddRouteButtonClickListener() {
        availableAddRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                ArrayList<RoutePoint> routePointArrayList = new ArrayList<RoutePoint>();
                Route route = new Route(routeNameEditText.getText().toString(),
                        new Time(getHourFromTimeTextView(startTimeTextView),getMinuteFromTimeTextView(startTimeTextView)),
                        new Time(getHourFromTimeTextView(endTimeTextView),getMinuteFromTimeTextView(endTimeTextView)),
                        routePointArrayList,
                        Route.createRouteId(AddOrUpdateNewRouteActivity.this));
                if(getIntent().getExtras() != null && getIntent().getExtras().getInt(UPDATED_ROUTE_ID_EXTRA_TAG) != 0) {
                    route.setId(getIntent().getExtras().getInt(UPDATED_ROUTE_ID_EXTRA_TAG));
                }
                if(getIntent().getExtras() != null){
                    route.setRoutePoints(((Route)getIntent().getExtras().getParcelable(ROUTE_EXTRA_TAG)).getRoutePoints());
                }
                bundle.putParcelable(ROUTE_RESULT_TAG,route);
                resultIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void initEditTextListener() {
        routeNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               checkIfAllInfoAvailable();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static void initTimeViews(View startTimeLayout, String startTimeText, View endTimeLayout, String endTimeText,AppCompatActivity appCompatActivity) {
        initTimeViewTimeDescription(startTimeLayout,startTimeText);
        initTimeViewTimeDescription(endTimeLayout,endTimeText);
        initTimeClickListener(startTimeLayout,TimePickerFragment.START_TIMER_KIND,"StartTimeFragment",appCompatActivity);
        initTimeClickListener(endTimeLayout,TimePickerFragment.END_TIMER_KIND,"EndTimeFragment",appCompatActivity);
    }

    private static void initTimeClickListener(final View timeLayout, final int timerKind, final String fragmentTag, final AppCompatActivity appCompatActivity) {
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView timeTextView = (TextView) timeLayout.findViewById(R.id.time_textview);
                DialogFragment newFragment = TimePickerFragment.newInstance(timerKind,getHourFromTimeTextView(timeTextView),getMinuteFromTimeTextView(timeTextView));
                newFragment.show(appCompatActivity.getSupportFragmentManager(),fragmentTag);
            }
        });
    }

    private static void initTimeViewTimeDescription(View timeLayout, String description) {
        TextView descriptionTextView = (TextView) timeLayout.findViewById(R.id.time_description_textview);
        descriptionTextView.setText(description);
    }

    private void checkIfAllInfoAvailable() {
        if(!isTimeTextViewZero(startTimeTextView) && !isTimeTextViewZero(endTimeTextView) && !routeNameEditText.getText().toString().equals("")){
            if(!buttonEnabled) {
                enableAddRouteButton();
                buttonEnabled = true;
            }
        } else {
            if(buttonEnabled) {
                disableAddRouteButton();
                buttonEnabled = false;
            }
        }
    }

    private void disableAddRouteButton() {
        unavailableAddRouteButton.setVisibility(View.VISIBLE);//todo ewentualna animacja
        availableAddRouteButton.setVisibility(View.GONE);
    }

    private void enableAddRouteButton() {
        unavailableAddRouteButton.setVisibility(View.GONE);//todo ewentualna animacja
        availableAddRouteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDoneGetTime(int timerKind, int hour, int minute) {
        if(timerKind == TimePickerFragment.START_TIMER_KIND){
            editStartTimeTextView(hour,minute,endTimeTextView,startTimeTextView,AddOrUpdateNewRouteActivity.this,R.id.activity_add_new_route);
        } else if(timerKind == TimePickerFragment.END_TIMER_KIND){
            editEndTimeTextView(hour,minute,startTimeTextView,endTimeTextView,AddOrUpdateNewRouteActivity.this,R.id.activity_add_new_route);
        }
        checkIfAllInfoAvailable();
    }

    public static void editEndTimeTextView(int hour, int minute, TextView startTimeTextView,TextView endTimeTextView,AppCompatActivity appCompatActivity, int viewId) {
        if(isTimeTextViewZero(startTimeTextView) || isStartTimeTextViewValueLesser(hour,minute,startTimeTextView)) {
            endTimeTextView.setText(Utils.formatTime(hour) + ":" + Utils.formatTime(minute));
            return;
        }
//        Utils.showSnackbar(appCompatActivity.findViewById(R.id.activity_add_new_route), appCompatActivity.getString(R.string.time_choosen_is_invalid));
        Utils.showSnackbar(appCompatActivity.findViewById(viewId), appCompatActivity.getString(R.string.time_choosen_is_invalid));
    }

    public static void editStartTimeTextView(int hour, int minute, TextView endTimeTextView,TextView startTimeTextView,AppCompatActivity appCompatActivity, int viewId) {
        if(isTimeTextViewZero(endTimeTextView) || isEndTimeTextViewValueGreater(hour,minute,endTimeTextView)) {
            startTimeTextView.setText(Utils.formatTime(hour) + ":" + Utils.formatTime(minute));
            return;
        }
//        Utils.showSnackbar(appCompatActivity.findViewById(R.id.activity_add_new_route), appCompatActivity.getString(R.string.time_choosen_is_invalid));
        Utils.showSnackbar(appCompatActivity.findViewById(viewId), appCompatActivity.getString(R.string.time_choosen_is_invalid));
    }

    private static boolean isStartTimeTextViewValueLesser(int hour, int minute, TextView startTimeTextView) {
        if(getHourFromTimeTextView(startTimeTextView) < hour){
            return true;
        } else if(getHourFromTimeTextView(startTimeTextView) == hour && getMinuteFromTimeTextView(startTimeTextView) < minute){
            return true;
        } else {
            return false;
        }
    }

    private static boolean isEndTimeTextViewValueGreater(int hour, int minute, TextView endTimeTextView) {
        if(getHourFromTimeTextView(endTimeTextView) > hour){
            return true;
        } else if(getHourFromTimeTextView(endTimeTextView) == hour && getMinuteFromTimeTextView(endTimeTextView) > minute){
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTimeTextViewZero(TextView timeTextView) {
        return getMinuteFromTimeTextView(timeTextView) == 0 && getHourFromTimeTextView(timeTextView) == 0;
    }

    public static int getHourFromTimeTextView(TextView timeTextView){
        return Integer.parseInt(timeTextView.getText().toString().substring(0,2));
    }

    public static int getMinuteFromTimeTextView(TextView timeTextView){
        return Integer.parseInt(timeTextView.getText().toString().substring(3,5));
    }
}
