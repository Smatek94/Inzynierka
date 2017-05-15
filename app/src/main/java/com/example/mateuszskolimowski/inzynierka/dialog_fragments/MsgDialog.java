package com.example.mateuszskolimowski.inzynierka.dialog_fragments;

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

public class MsgDialog extends DialogFragment {

    public static final String TAG = MsgDialog.class.getCanonicalName() + "TAG";
    private static final String DIALOG_MSG_ARG_TAG = MsgDialog.class.getName() + " DIALOG_MSG_ARG_TAG";

    public static MsgDialog newInstance(String msg) {
        MsgDialog fragment = new MsgDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_MSG_ARG_TAG,msg);
        fragment.setArguments(bundle);
        return fragment;
    }

    public MsgDialog() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_msg_layout, container, false);
        initDialogTextView(v);
        initOkTextViewClickListener(v);
        return v;
    }

    private void initOkTextViewClickListener(View v) {
        View okTextView = v.findViewById(R.id.ok_textview);
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void initDialogTextView(View view) {
        TextView failDialogTextView = (TextView) view.findViewById(R.id.msg_dialog_textview);
        failDialogTextView.setText(getArguments().getString(DIALOG_MSG_ARG_TAG));
    }
}
