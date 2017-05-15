package com.example.mateuszskolimowski.inzynierka.dialog_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;


/**
 * Created by Mateusz Skolimowski on 22.03.2017.
 */

public class AreYouSureDialog extends DialogFragment {

    public static final String TAG = AreYouSureDialog.class.getCanonicalName() + "TAG";
    private static final String DIALOG_MSG_ARG_TAG = AreYouSureDialog.class.getName() + " DIALOG_MSG_ARG_TAG";
    private static final String ROUTE_ARG_TAG = AreYouSureDialog.class.getName() + " ROUTE_ARG_TAG";
    private static final String ROUTE_POINT_ARG_TAG = AreYouSureDialog.class.getName() + " ROUTE_POINT_ARG_TAG";
    private DeleteRouteInterface deleteRouteListener;
    private DeleteRoutePointInterface deleteRoutePointLister;

    public static AreYouSureDialog newInstance(String msg, Route route, RoutePoint routePoint) {
        AreYouSureDialog fragment = new AreYouSureDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_MSG_ARG_TAG,msg);
        bundle.putParcelable(ROUTE_ARG_TAG,route);
        bundle.putParcelable(ROUTE_POINT_ARG_TAG,routePoint);
        fragment.setArguments(bundle);
        return fragment;
    }

    public AreYouSureDialog() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_are_you_sure_layout, container, false);
        initDialogTextView(v);
        initYesTextViewClickListener(v);
        initNoTextViewClickListener(v);
        return v;
    }

    private void initNoTextViewClickListener(View v) {
        View noTextview = v.findViewById(R.id.no_textview);
        noTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void initYesTextViewClickListener(View v) {
        View yesTextview = v.findViewById(R.id.yes_textview);
        yesTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getArguments().getParcelable(ROUTE_POINT_ARG_TAG) != null){
                    deleteRoutePointLister.deleteRoutePoint((RoutePoint) getArguments().getParcelable(ROUTE_POINT_ARG_TAG));
                } else {
                    deleteRouteListener.deleteRoute((Route) getArguments().getParcelable(ROUTE_ARG_TAG));
                }
                dismiss();
            }
        });
    }

    private void initDialogTextView(View view) {
        TextView failDialogTextView = (TextView) view.findViewById(R.id.msg_dialog_textview);
        failDialogTextView.setText(getArguments().getString(DIALOG_MSG_ARG_TAG));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DeleteRouteInterface) {
            deleteRouteListener = (DeleteRouteInterface) context;
        }
        if (context instanceof DeleteRoutePointInterface) {
            deleteRoutePointLister = (DeleteRoutePointInterface) context;
        }
    }

    public interface DeleteRouteInterface {
        void deleteRoute(Route route);
    }

    public interface DeleteRoutePointInterface {
        void deleteRoutePoint(RoutePoint routePoint);
    }
}
