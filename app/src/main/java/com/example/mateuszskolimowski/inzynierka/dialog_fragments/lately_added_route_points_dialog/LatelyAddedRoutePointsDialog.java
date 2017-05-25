package com.example.mateuszskolimowski.inzynierka.dialog_fragments.lately_added_route_points_dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.views.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Mateusz Skolimowski on 03.05.2017.
 */

public class LatelyAddedRoutePointsDialog extends DialogFragment {

    public static final String TAG = LatelyAddedRoutePointsDialog.class.getCanonicalName() + "TAG";
    private static final String LATELY_ADED_ROUTE_POINTS_EXTRA_TAG = LatelyAddedRoutePointsDialog.class.getName() + "LATELY_ADED_ROUTE_POINTS_EXTRA_TAG";
    private LatelyAddedRoutePointsRecyclerViewAdapter latelyAddedRoutePointsRecyclerViewAdapter;
    private ArrayList<RoutePointDestination> latelyAddedRoutePointDestinations;
    private LatelyAddedRoutePointsDialogInterface latelyAddedRoutePointsDialogInterface;

    public static LatelyAddedRoutePointsDialog newInstance(ArrayList<RoutePointDestination> latelyAddedRoutePointDestinations) {
        LatelyAddedRoutePointsDialog fragment = new LatelyAddedRoutePointsDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(LATELY_ADED_ROUTE_POINTS_EXTRA_TAG, latelyAddedRoutePointDestinations);
        fragment.setArguments(bundle);
        return fragment;
    }

    public LatelyAddedRoutePointsDialog() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_lately_added_route_points_layout, container, false);
        initRecyclerView((RecyclerView) v.findViewById(R.id.lately_added_route_points_recycler_view));
        return v;
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        latelyAddedRoutePointsRecyclerViewAdapter = new LatelyAddedRoutePointsRecyclerViewAdapter(
                getArguments().getParcelableArrayList(LATELY_ADED_ROUTE_POINTS_EXTRA_TAG),
                latelyAddedRoutePointsDialogInterface);
        recyclerView.setAdapter(latelyAddedRoutePointsRecyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LatelyAddedRoutePointsDialogInterface) {
            latelyAddedRoutePointsDialogInterface = (LatelyAddedRoutePointsDialogInterface) context;
        }
    }

    public interface LatelyAddedRoutePointsDialogInterface{
        void latelyAddedRoutePointDialogCallback(RoutePointDestination routePointDestination);
    }
}
