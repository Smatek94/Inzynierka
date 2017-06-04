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


public class GetDistancesToNewRoutePointApiFragment extends RequestFragment {

    public static final String FRAGMENT_TAG = "request " + GetDistancesToNewRoutePointApiFragment.class.getName() + " tag";
    private static final String TO_ROUTE_POINT_EXTRA_TAG = GetDistancesToNewRoutePointApiFragment.class.getName() + "FROM_ROUTE_POINT_EXTRA_TAG";
    private static final String FROM_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG = GetDistancesToNewRoutePointApiFragment.class.getName() + "TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG";
    private static final String ACTION_TYPE_EXTRA_TAG = GetDistancesToNewRoutePointApiFragment.class.getName() + "ACTION_TYPE_EXTRA_TAG";
    private String URL = ApiURLs.DISTANCE_MATRIX_URL;
    private ArrayList<RoutePointDestination> routePointDestinationsList;
    private FragmentResponseListener fragmentResponseListener;

    public static GetDistancesToNewRoutePointApiFragment newInstance(String fromRoutePointId, ArrayList<RoutePointDestination> toRoutePointDestinationArrayList, int actionType) {
        GetDistancesToNewRoutePointApiFragment fragment = new GetDistancesToNewRoutePointApiFragment();
        fragment.setArguments(createArgsBundle(fromRoutePointId, toRoutePointDestinationArrayList, actionType));
        return fragment;
    }

    private static Bundle createArgsBundle(String fromRoutePointId, ArrayList<RoutePointDestination> toRoutePointDestinationArrayList, int actionType) {
        Bundle args = new Bundle();
        args.putString(TO_ROUTE_POINT_EXTRA_TAG, fromRoutePointId);
        args.putParcelableArrayList(FROM_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG, toRoutePointDestinationArrayList);
        args.putInt(ACTION_TYPE_EXTRA_TAG, actionType);
        return args;
    }

    @Override
    public void request() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(URL, createRequestParams(), jsonHttpResponseHandlerHelper);
    }

    private RequestParams createRequestParams() {
        RequestParams requestParams = new RequestParams();
        routePointDestinationsList = getArguments().getParcelableArrayList(FROM_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG);
        String originsString = "";
        for (int i = 0; i < routePointDestinationsList.size(); i++) {
            RoutePointDestination toRoutePointDestination = routePointDestinationsList.get(i);
            originsString += "place_id:" + toRoutePointDestination.getRoutePointPlaceId();
            if (i < routePointDestinationsList.size() - 1) {
                originsString += "|";
            }
        }
        requestParams.put("origins", originsString);
        RoutePointDestination routePointDestinations = Utils.getSQLiteHelper(getActivity()).getRoutePointDestinationFromDataBase(getArguments().getString(TO_ROUTE_POINT_EXTRA_TAG));
        requestParams.put("destinations", "place_id:" + routePointDestinations.getRoutePointPlaceId());

        requestParams.put("key", getString(R.string.google_maps_key));
        return requestParams;
    }

    @Override
    protected void parseData(JSONObject resposne) throws JSONException {
        double time = System.currentTimeMillis();
        Utils.debugLog("" + resposne);
        JSONArray rows = resposne.getJSONArray("rows");
        for (int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.getJSONObject(i);
            JSONArray elements = row.getJSONArray("elements");
            JSONObject travelData = elements.getJSONObject(0);
            if (travelData.getString("status").equals("OK")) {
                JSONObject distance = travelData.getJSONObject("distance");
                JSONObject duration = travelData.getJSONObject("duration");
                if(getActivity() != null) {
                    RoutePointDestination routePointDestination = Utils.getSQLiteHelper(getActivity()).getRoutePointDestinationFromDataBase(getArguments().getString(TO_ROUTE_POINT_EXTRA_TAG));
                    routePointDestinationsList.get(i).addTravel(new Travel(duration.getLong("value")*1000, distance.getLong("value"), routePointDestination.getRoutePointPlaceId()));
                } else {
                    break;
                }
            }
        }
        Utils.debugLog("minelo : " + (System.currentTimeMillis() - time));
    }

    @Override
    public void onDoneRequest() {
        if (fragmentResponseListener != null)
            fragmentResponseListener.onDoneGetDestinationRoutePoints(routePointDestinationsList, getArguments().getInt(ACTION_TYPE_EXTRA_TAG));
    }

    @Override
    public void onFailRequest(String msg, int statusCode) {
        if (fragmentResponseListener != null)
            fragmentResponseListener.onFailureListener(msg, statusCode);
    }

    public interface FragmentResponseListener {
        void onDoneGetDestinationRoutePoints(ArrayList<RoutePointDestination> routePointDestinationsList, int actionType);

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

/*public class GetCurrencySumApiFragment extends Fragment {

    public static final String FRAGMENT_TAG = "request " + GetCurrencySumApiFragment.class.getName() + " tag";
    private static final String CURRENCY_TAG = GetCurrencySumApiFragment.class.getName() + "CURRENCY_TAG";
    private String URL = ApiURLs.GET_CURRENCY_SUM_URL;

    private FragmentResponseListener fragmentResponseListener;
    private AsyncHttpClient asyncHttpClient;
    private boolean paused;
    private boolean doOnDoneRequest;
    private String currencySum;
    private boolean onFailRequest;

    public static GetCurrencySumApiFragment newInstance(String currency) {
        GetCurrencySumApiFragment fragment = new GetCurrencySumApiFragment();
        fragment.setArguments(createArgsBundle(currency));
        return fragment;
    }

    private static Bundle createArgsBundle(String currency) {
        Bundle args = new Bundle();
        args.putString(CURRENCY_TAG, currency);
        return args;
    }

    public GetCurrencySumApiFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        request(getArguments().getString(CURRENCY_TAG));
    }

    private void request(String currency) {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", SharedPreferencesUtils.getLoginDataFromSharedPreferences(getContext()).getToken());
        String url = URL + "/" + currency;
        asyncHttpClient.get(url, new JsonHttpResponseHandlerHelper(new OnDoneObjectListener() {
            @Override
            public void onDone(final Object object, final int statusCode) {
                if (Config.threadSleep) {
                    Utils.threadSleep(new OnDoneListener() {
                        @Override
                        public void onDone() {
                            parseResponse(object, statusCode);
                        }
                    });
                } else {
                    parseResponse(object, statusCode);
                }
            }
        }));
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        if (doOnDoneRequest) {
            onDoneRequest();
        }
        if (onFailRequest) {
            onFail();
        }
    }

    private void parseResponse(Object resposne, int statusCode) {
        if (resposne == null) {
            Utils.debugLog("response jest nullem");
            handleNullResponse();
        } else if (resposne instanceof JSONObject) {
            Utils.debugLog("response jest jsonobjectem");
            parseJSONObject((JSONObject) resposne, statusCode);
        }
    }

    private void handleNullResponse() {
        handleIfFragmentPausedRequestFail();
    }

    private void parseJSONObject(JSONObject resposne, int statusCode) {
        try {
            if (statusCode == 200) {
                currencySum = parseData(resposne);
                handleIfFragmentPausedRequestSucced();
            } else {
                onFailRequest(resposne.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
//                onFailRequest("[GET HISTORY TRANSACTION API FRAGMENT]nie udalo sie pobrac danych z JSONa");
        }
    }

    private String parseData(JSONObject resposne) throws JSONException {
        JSONObject data = resposne.getJSONObject("data");
        return data.getString("ExchangedSubAccountAmountSum") + " " + data.getString("ExchangedSubAccountAmountSumCurrencyName");
    }

    private void handleIfFragmentPausedRequestSucced() {
        if (paused) {
            doOnDoneRequest = true;
        } else {
            onDoneRequest();
        }
    }

    private void handleIfFragmentPausedRequestFail() {
        if (paused) {
            onFailRequest = true;
        } else {
            onFail();
        }
    }

    private void onFail() {
        onFailRequest(getString(R.string.cant_connect_to_server));
    }

    private void onDoneRequest() {
        if (fragmentResponseListener != null)
            fragmentResponseListener.onDoneGetCurrencySumRequest(currencySum);
        if (getActivity() != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    private void onFailRequest(String msg) {
        fragmentResponseListener.onFailureListener(msg);
        if (getActivity() != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    public interface FragmentResponseListener {
        void onDoneGetCurrencySumRequest(String currencySum);
        void onFailureListener(String msg);
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

    @Override
    public void onDetach() {
        Utils.debugLog("ondetach");
        super.onDetach();
        fragmentResponseListener = null;
    }
}*/
