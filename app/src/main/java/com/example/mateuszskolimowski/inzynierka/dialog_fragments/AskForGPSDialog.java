package com.example.mateuszskolimowski.inzynierka.dialog_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;


/**
 * Created by Mateusz Skolimowski on 22.03.2017.
 */

public class AskForGPSDialog extends DialogFragment {

    public static final String TAG = AskForGPSDialog.class.getCanonicalName() + "TAG";
    private static final String DIALOG_MSG_ARG_TAG = AskForGPSDialog.class.getName() + " DIALOG_MSG_ARG_TAG";
    private AskForGPSDialogInterface askForGPSDialogInterface;

    public static AskForGPSDialog newInstance() {
        AskForGPSDialog fragment = new AskForGPSDialog();
//        Bundle bundle = new Bundle();
//        bundle.putString(DIALOG_MSG_ARG_TAG,msg);
//        fragment.setArguments(bundle);
        return fragment;
    }

    public AskForGPSDialog() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_ask_for_gps_layout, container, false);
//        initDialogTextView(v);
        initOkTextViewClickListener(v);
        return v;
    }

    private void initOkTextViewClickListener(View v) {
        View okTextView = v.findViewById(R.id.ok_textview);
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
                askForGPSDialogInterface.okClicked();
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AskForGPSDialogInterface) {
            askForGPSDialogInterface = (AskForGPSDialogInterface) context;
        }
    }

    public interface AskForGPSDialogInterface{
        void okClicked();
    }
}
