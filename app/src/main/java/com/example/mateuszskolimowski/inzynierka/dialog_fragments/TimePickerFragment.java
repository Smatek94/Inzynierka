package com.example.mateuszskolimowski.inzynierka.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.example.mateuszskolimowski.inzynierka.utils.Utils;

import java.util.Calendar;

/**
 * Created by Mateusz Skolimowski on 26.03.2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    public static final String TIMER_KIND_ARG_TAG = TimePickerFragment.class.getName() + "TIMER_KIND_ARG_TAG";
    public static final String HOUR_ARG_TAG = TimePickerFragment.class.getName() + "HOUR_ARG_TAG";
    public static final String MINUTE_ARG_TAG = TimePickerFragment.class.getName() + "MINUTE_ARG_TAG";
    public static final int START_TIMER_KIND = 1;
    public static final int END_TIMER_KIND = 2;

    private int timerKind;
    private FragmentResponseListener fragmentResponseListener;

    public static TimePickerFragment newInstance(int timerKind, int hour, int minute) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TIMER_KIND_ARG_TAG,timerKind);
        bundle.putInt(HOUR_ARG_TAG,hour);
        bundle.putInt(MINUTE_ARG_TAG,minute);
        fragment.setArguments(bundle);
        return fragment;
    }

    public TimePickerFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        timerKind = getArguments().getInt(TIMER_KIND_ARG_TAG,-1);
        int timerHour = getArguments().getInt(HOUR_ARG_TAG, 0);
        int timerMinute = getArguments().getInt(MINUTE_ARG_TAG, 0);
        if(timerHour == 0 && timerMinute == 0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            timerHour = calendar.get(Calendar.HOUR_OF_DAY);
            timerMinute = calendar.get(Calendar.MINUTE);
        }
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK,this,timerHour ,timerMinute , DateFormat.is24HourFormat(getActivity()));
//        TimePickerDialog tpd = new TimePickerDialog(getActivity(), android.R.style.Theme_Material_Dialog_Alert ,this, hour, minute, DateFormat.is24HourFormat(getActivity()));
//        TimePickerDialog tpd = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog ,this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        return tpd;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        fragmentResponseListener.onDoneGetTime(timerKind,hourOfDay,minute);
    }

    public interface FragmentResponseListener {
        void onDoneGetTime(int timerKind, int hour, int minute);
    }

    @Override
    public void onAttach(Context context) {
        Utils.debugLog("onattach");
        super.onAttach(context);
        if (context instanceof FragmentResponseListener) {
            fragmentResponseListener = (FragmentResponseListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentResponseListener");
        }
    }
}
