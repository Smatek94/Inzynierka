package com.example.mateuszskolimowski.inzynierka.dialog_fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;


/**
 * Created by Mateusz Skolimowski on 22.03.2017.
 */

public class LoadingDialog extends DialogFragment {

    public static final String TAG = LoadingDialog.class.getCanonicalName() + "TAG";
    private static final String DIALOG_MSG_ARG_TAG = LoadingDialog.class.getName() + " DIALOG_MSG_ARG_TAG";
    private fragmentInteractionInterface listener;

    public static LoadingDialog newInstance(String msg) {
        LoadingDialog fragment = new LoadingDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_MSG_ARG_TAG,msg);
        fragment.setArguments(bundle);
        return fragment;
    }


    public LoadingDialog() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_loading_layout, container, false);
        setCancelable(false);
        initDialogBackPressedListener();
        initDialogTextView(v);
        return v;
    }

    private void initDialogTextView(View view) {
        TextView loadingDialogTextView = (TextView) view.findViewById(R.id.loading_dialog_textview);
        loadingDialogTextView.setText(getArguments().getString(DIALOG_MSG_ARG_TAG));
    }

    private void initDialogBackPressedListener() {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if ((keyCode ==  KeyEvent.KEYCODE_BACK)) {
                    if (keyEvent.getAction()!=KeyEvent.ACTION_DOWN) {
                        listener.backPressedWhenDialogWasVisible();
                        return true;
                    } else {
                        return true;
                    }
                } else
                    return false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof fragmentInteractionInterface) {
            listener = (fragmentInteractionInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement fragmentInteractionInterface");
        }
    }

    public interface fragmentInteractionInterface {
        public void backPressedWhenDialogWasVisible();
    }
}
