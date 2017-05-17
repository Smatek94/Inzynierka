package com.example.mateuszskolimowski.inzynierka.activities.add_route_points.api;


import android.content.Context;
import android.os.Bundle;

import com.example.mateuszskolimowski.inzynierka.api_utils.ApiURLs;
import com.example.mateuszskolimowski.inzynierka.api_utils.RequestFragment;
import com.example.mateuszskolimowski.inzynierka.model.DestinationRoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
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
    private String URL = ApiURLs.DISTANCE_MATRIX_URL;
    private ArrayList<DestinationRoutePoint> destinationRoutePoints;
    private FragmentResponseListener fragmentResponseListener;

    public static GetDistancesFromNewRoutePointApiFragment newInstance(RoutePoint fromRoutePoint, ArrayList<RoutePoint> toRoutePointArrayList) {
        GetDistancesFromNewRoutePointApiFragment fragment = new GetDistancesFromNewRoutePointApiFragment();
        fragment.setArguments(createArgsBundle(fromRoutePoint,toRoutePointArrayList));
        return fragment;
    }

    private static Bundle createArgsBundle(RoutePoint fromRoutePoint, ArrayList<RoutePoint> toRoutePointArrayList) {
        Bundle args = new Bundle();
        args.putParcelable(FROM_ROUTE_POINT_EXTRA_TAG, fromRoutePoint);
        args.putParcelableArrayList(TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG, toRoutePointArrayList);
        return args;
    }

    @Override
    public void request() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(URL,createRequestParams(), jsonHttpResponseHandlerHelper);
    }

    private RequestParams createRequestParams() {
        RequestParams requestParams = new RequestParams();
        RoutePoint routePoint = getArguments().getParcelable(FROM_ROUTE_POINT_EXTRA_TAG);
        requestParams.put("origins",routePoint.getRoutePointLatLng().latitude+","+routePoint.getRoutePointLatLng().longitude);
        ArrayList<RoutePoint> toRoutePointsArrayList = getArguments().getParcelableArrayList(TO_ROUTE_POINTS_ARRAY_LIST_EXTRA_TAG);
        String destinationString = "";
        for(int i = 0 ; i < toRoutePointsArrayList.size() ; i++){
            RoutePoint toRoutePoint = toRoutePointsArrayList.get(i);
            destinationString += toRoutePoint.getRoutePointLatLng().latitude+","+toRoutePoint.getRoutePointLatLng().longitude;
            if(i < toRoutePointsArrayList.size() - 1){
                destinationString += "|";
            }
        }
        requestParams.put("destinations",destinationString);
        return requestParams;
    }

    @Override
    protected void parseData(JSONObject resposne) throws JSONException {
        Utils.debugLog(""+resposne);
        JSONArray rows = resposne.getJSONArray("rows");
        JSONObject row = rows.getJSONObject(0);
        JSONArray elements = row.getJSONArray("elements");
//        for(int i = 0 ; i < )
        /* JSONObject data = resposne.getJSONObject("data");
        billingArrayList = new ArrayList<Billing>();
        ArrayList<Currency> currencyList = new ArrayList<>();
        currencyList.add(new Currency(1222333, "PLN", false, false, false, false));
        currencyList.add(new Currency(35000, "GBP", false, false, false, false));
        billingArrayList.add(new Billing(getString(R.string.main_account), "20 0000 1234 5678", "Opis własny karty (Firmowy: ORBIS S.A.)", R.mipmap.promo, currencyList));
        billingArrayList.add(new Billing(getString(R.string.helper_account),"20 0001 0023 5678", "Mikołajewski Filip", R.mipmap.promo, currencyList));
        billingArrayList.add(new Billing(getString(R.string.helper_account),"20 0002 1234 5678", "Popławski Bogumił", R.mipmap.promo, currencyList));*/
    }

    @Override
    public void onDoneRequest() {
        if(fragmentResponseListener != null)
            fragmentResponseListener.onDoneGetDestinationRoutePoints(destinationRoutePoints);
    }

    @Override
    public void onFailRequest(String msg, int statusCode) {
        if(fragmentResponseListener != null)
            fragmentResponseListener.onFailureListener(msg,statusCode);
    }

    public interface FragmentResponseListener {
        void onDoneGetDestinationRoutePoints(ArrayList<DestinationRoutePoint> destinationRoutePoints);
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
