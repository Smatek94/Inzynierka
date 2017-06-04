package com.example.mateuszskolimowski.inzynierka.activities.add_route_points.api;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.api_utils.ApiURLs;
import com.example.mateuszskolimowski.inzynierka.api_utils.RequestFragment;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.model.Travel;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GetDistanceFromYourLocalizationApiFragment extends RequestFragment {

    public static final String FRAGMENT_TAG = "request " + GetDistanceFromYourLocalizationApiFragment.class.getName() + " tag";
    private static final String FROM_ROUTE_POINT_EXTRA_TAG = GetDistanceFromYourLocalizationApiFragment.class.getName() + "FROM_ROUTE_POINT_EXTRA_TAG";
    private static final String TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG = GetDistanceFromYourLocalizationApiFragment.class.getName() + "TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG";
    private String URL = ApiURLs.DISTANCE_MATRIX_URL;
    private RoutePointDestination routePointDestinations;
    private FragmentResponseListener fragmentResponseListener;

    public static GetDistanceFromYourLocalizationApiFragment newInstance(Location mLastLocation, ArrayList<RoutePoint> routePoints) {
        GetDistanceFromYourLocalizationApiFragment fragment = new GetDistanceFromYourLocalizationApiFragment();
        fragment.setArguments(createArgsBundle(mLastLocation, routePoints));
        return fragment;
    }

    private static Bundle createArgsBundle(Location mLastLocation, ArrayList<RoutePoint> routePoints) {
        Bundle args = new Bundle();
        args.putParcelable(FROM_ROUTE_POINT_EXTRA_TAG, mLastLocation);
        args.putParcelableArrayList(TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG, routePoints);
        return args;
    }

    @Override
    public void request() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(URL,createRequestParams(), jsonHttpResponseHandlerHelper);
    }

    private RequestParams createRequestParams() {
        RequestParams requestParams = new RequestParams();
        Location location = getArguments().getParcelable(FROM_ROUTE_POINT_EXTRA_TAG);
        routePointDestinations = new RoutePointDestination("mylocalization");
        requestParams.put("origins", location.getLatitude()+","+location.getLongitude());
        ArrayList<RoutePoint> toRoutePointsArrayListDestination = getArguments().getParcelableArrayList(TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG);
        String destinationString = "";
        for(int i = 0; i < toRoutePointsArrayListDestination.size() ; i++){
            RoutePoint toRoutePointDestination = toRoutePointsArrayListDestination.get(i);
            destinationString += "place_id:" + toRoutePointDestination.getId();
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
                RoutePoint routePointDestination = (RoutePoint) getArguments().getParcelableArrayList(TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG).get(i);
                routePointDestinations.addTravel(new Travel(
                        duration.getLong("value")*1000,
                        distance.getLong("value"),
                        routePointDestination.getId()
                ));
            }
        }
    }

    @Override
    public void onDoneRequest() {
        if(fragmentResponseListener != null)
            fragmentResponseListener.onDoneGetDestinationRoutePoints(routePointDestinations);
    }

    @Override
    public void onFailRequest(String msg, int statusCode) {
        if(fragmentResponseListener != null)
            fragmentResponseListener.onFailureListener(msg,statusCode);
    }

    public interface FragmentResponseListener {
        void onDoneGetDestinationRoutePoints(RoutePointDestination routePointDestinations);
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
