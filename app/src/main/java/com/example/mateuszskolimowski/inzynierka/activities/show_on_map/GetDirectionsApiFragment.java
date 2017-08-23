package com.example.mateuszskolimowski.inzynierka.activities.show_on_map;


import android.content.Context;
import android.os.Bundle;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.api_utils.ApiURLs;
import com.example.mateuszskolimowski.inzynierka.api_utils.RequestFragment;
import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;


public class GetDirectionsApiFragment extends RequestFragment {

    public static final String FRAGMENT_TAG = "request " + GetDirectionsApiFragment.class.getName() + " tag";
    private static final String ROUTE_EXTRA_TAG = GetDirectionsApiFragment.class.getName() + "FROM_ROUTE_POINT_EXTRA_TAG";
    private String URL = ApiURLs.DIRECTIONS_URL;
    private FragmentResponseListener fragmentResponseListener;
    private ArrayList<LatLng> latlngsList = new ArrayList<>();

    public static GetDirectionsApiFragment newInstance(Route route) {
        GetDirectionsApiFragment fragment = new GetDirectionsApiFragment();
        fragment.setArguments(createArgsBundle(route));
        return fragment;
    }

    private static Bundle createArgsBundle(Route route) {
        Bundle args = new Bundle();
        args.putParcelable(ROUTE_EXTRA_TAG, route);
        return args;
    }

    @Override
    public void request() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(URL,createRequestParams(), jsonHttpResponseHandlerHelper);
    }

    private RequestParams createRequestParams() {
        RequestParams requestParams = new RequestParams();
        Route route = getArguments().getParcelable(ROUTE_EXTRA_TAG);
        ArrayList<RoutePoint> routePoints = route.getRoutePoints();
        requestParams.put("origin", "place_id:" + routePoints.get(0).getId());
        requestParams.put("destination","place_id:" + routePoints.get(routePoints.size() - 1).getId());
        if(routePoints.size() > 2){
            String waypoints = "";
            for(int i = 1 ; i < routePoints.size() - 1 ; i++){
                waypoints += "place_id:" +routePoints.get(i).getId();
                if(i < routePoints.size() - 2){
                    waypoints += "|";
                }
            }
            requestParams.put("waypoints",waypoints);
        }
        requestParams.put("key",getString(R.string.google_maps_key));
        return requestParams;
    }

    @Override
    protected void parseData(JSONObject resposne) throws JSONException {
        Utils.debugLog(""+resposne);
        JSONArray routes = resposne.getJSONArray("routes");
        JSONObject bounds = routes.getJSONObject(0);
        JSONArray legs = bounds.getJSONArray("legs");
        for(int i = 0 ; i < legs.length() ; i++){
            JSONObject leg = legs.getJSONObject(i);
            latlngsList.addAll(parseLeg(leg));
        }
    }

    private Collection<? extends LatLng> parseLeg(JSONObject leg) throws JSONException {
        ArrayList<LatLng> resultList = new ArrayList<>();
        JSONArray steps = leg.getJSONArray("steps");
        for(int i = 0 ; i < steps.length() ; i++){
            JSONObject step = steps.getJSONObject(i);
            resultList.addAll(PolyUtil.decode(step.getJSONObject("polyline").getString("points")));
        }
        return resultList;
    }

    @Override
    public void onDoneRequest() {
        if(fragmentResponseListener != null)
            fragmentResponseListener.onDoneGetLatLngsListRoutePoints(latlngsList);
    }

    @Override
    public void onFailRequest(String msg, int statusCode) {
        if(fragmentResponseListener != null)
            fragmentResponseListener.onFailureListener(msg,statusCode);
    }

    public interface FragmentResponseListener {
        void onDoneGetLatLngsListRoutePoints(ArrayList<LatLng> latlngsList);
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
