package com.example.mateuszskolimowski.inzynierka.activities.add_route_points.api;


import android.content.Context;
import android.os.Bundle;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.api_utils.ApiURLs;
import com.example.mateuszskolimowski.inzynierka.api_utils.RequestFragment;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.model.Travel;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GetDistancesFromNewRoutePointApiFragment extends RequestFragment {

    public static final String FRAGMENT_TAG = "request " + GetDistancesFromNewRoutePointApiFragment.class.getName() + " tag";
    private static final String FROM_ROUTE_POINT_EXTRA_TAG = GetDistancesFromNewRoutePointApiFragment.class.getName() + "FROM_ROUTE_POINT_EXTRA_TAG";
    private static final String TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG = GetDistancesFromNewRoutePointApiFragment.class.getName() + "TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG";
    private static final String ACTION_TYPE_EXTRA_TAG = GetDistancesFromNewRoutePointApiFragment.class.getName() + "ACTION_TYPE_EXTRA_TAG";
    private String URL = ApiURLs.DISTANCE_MATRIX_URL;
    private RoutePointDestination routePointDestinations;
    private FragmentResponseListener fragmentResponseListener;

    public static GetDistancesFromNewRoutePointApiFragment newInstance(String fromRoutePointId, ArrayList<RoutePointDestination> toRoutePointDestinationArrayList,int actionType) {
        GetDistancesFromNewRoutePointApiFragment fragment = new GetDistancesFromNewRoutePointApiFragment();
        fragment.setArguments(createArgsBundle(fromRoutePointId, toRoutePointDestinationArrayList,actionType));
        return fragment;
    }

    private static Bundle createArgsBundle(String fromRoutePointId, ArrayList<RoutePointDestination> toRoutePointDestinationArrayList,int actionType) {
        Bundle args = new Bundle();
        args.putString(FROM_ROUTE_POINT_EXTRA_TAG, fromRoutePointId);
        args.putParcelableArrayList(TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG, toRoutePointDestinationArrayList);
        args.putInt(ACTION_TYPE_EXTRA_TAG, actionType);
        return args;
    }

    @Override
    public void request() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(URL,createRequestParams(), jsonHttpResponseHandlerHelper);
    }

    private RequestParams createRequestParams() {
        RequestParams requestParams = new RequestParams();
        routePointDestinations = Utils.getSQLiteHelper(getActivity())
                .getRoutePointDestinationFromDataBase(getArguments().getString(FROM_ROUTE_POINT_EXTRA_TAG));
        requestParams.put("origins", "place_id:" + routePointDestinations.getRoutePointPlaceId());
        ArrayList<RoutePointDestination> toRoutePointsArrayListDestination = getArguments()
                .getParcelableArrayList(TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG);
        String destinationString = "";
        for(int i = 0; i < toRoutePointsArrayListDestination.size() ; i++){
            RoutePointDestination toRoutePointDestination = toRoutePointsArrayListDestination.get(i);
            destinationString += "place_id:" + toRoutePointDestination.getRoutePointPlaceId();
            if(i < toRoutePointsArrayListDestination.size() - 1){
                destinationString += "|";
            }
        }
        requestParams.put("destinations",destinationString);
        requestParams.put("key",getString(R.string.google_maps_key));
        return requestParams;
    }

    @Override
    protected void parseData(JSONObject resposne) throws JSONException {
        Utils.debugLog(""+resposne);
        JSONArray rows = resposne.getJSONArray("rows");
        JSONObject row = rows.getJSONObject(0);
        JSONArray elements = row.getJSONArray("elements");
        for(int i = 0 ; i < elements.length() ; i++){
            JSONObject travelData = elements.getJSONObject(i);
            if(travelData.getString("status").equals("OK")){
                JSONObject distance = travelData.getJSONObject("distance");
                JSONObject duration = travelData.getJSONObject("duration");
                RoutePointDestination routePointDestination = (RoutePointDestination) getArguments().getParcelableArrayList(TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG).get(i);
                routePointDestinations.addTravel(new Travel(
                        duration.getLong("value")*1000,
                        distance.getLong("value"),
                        routePointDestination.getRoutePointPlaceId()
                ));
            }
        }
    }

    @Override
    public void onDoneRequest() {
        if(fragmentResponseListener != null)
            fragmentResponseListener.onDoneGetDestinationRoutePoints(routePointDestinations,getArguments().getInt(ACTION_TYPE_EXTRA_TAG));
    }

    @Override
    public void onFailRequest(String msg, int statusCode) {
        if(fragmentResponseListener != null)
            fragmentResponseListener.onFailureListener(msg,statusCode);
    }

    public interface FragmentResponseListener {
        void onDoneGetDestinationRoutePoints(RoutePointDestination routePointDestinations,int actionType);
        void onFailureListener(String msg, int statusCode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentResponseListener) {
            fragmentResponseListener = (FragmentResponseListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentResponseListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentResponseListener = null;
    }
}