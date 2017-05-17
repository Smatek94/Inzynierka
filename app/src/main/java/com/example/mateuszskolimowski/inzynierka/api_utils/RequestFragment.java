package com.example.mateuszskolimowski.inzynierka.api_utils;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.mateuszskolimowski.inzynierka.interfaces.OnDoneListener;
import com.example.mateuszskolimowski.inzynierka.interfaces.OnDoneObjectListener;
import com.example.mateuszskolimowski.inzynierka.utils.Config;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class RequestFragment extends Fragment {

    protected AsyncHttpClient asyncHttpClient;
    protected JsonHttpResponseHandlerHelper jsonHttpResponseHandlerHelper = new JsonHttpResponseHandlerHelper(new OnDoneObjectListener() {
        @Override
        public void onDone(final Object object, final int statusCode) {
           /* if (Config.threadSleep) {
                Utils.threadSleep(new OnDoneListener() {
                    @Override
                    public void onDone() {
                        parseResponse(object, statusCode);
                    }
                });
            } else { fixme ewentualnie odkomentowac jakbym jednak chcial sleepa ;)*/
                parseResponse(object, statusCode);
//            }
        }
    });

    private boolean paused;
    private boolean doOnDoneRequest;
    private boolean onFailRequest;
    private String failMsg;
    private int failStatusCode;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        request();
    }

    public abstract void request();

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
            onDoneRequestHandler();
        }
        if (onFailRequest) {
            onFail();
        }
    }

    protected void parseResponse(Object resposne, int statusCode){
        if (resposne == null) {
//            failMsg = getString(R.string.cant_connect_to_server);
            failMsg = "nie udalo sie pobrac danych";
            handleIfFragmentPausedRequestFail();
        } else if (resposne instanceof JSONObject) {
            parseJSONObject((JSONObject) resposne, statusCode);
        }
    }

    private void parseJSONObject(JSONObject resposne, int statusCode) {
        try {
            if (statusCode == 200) {
                parseData(resposne);
                handleIfFragmentPausedRequestSucced();
            } else {
                failMsg = resposne.getString("message");
                failStatusCode = statusCode;
                handleIfFragmentPausedRequestFail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected abstract void parseData(JSONObject resposne) throws JSONException;

    private void handleIfFragmentPausedRequestSucced() {
        if (paused) {
            doOnDoneRequest = true;
        } else {
            onDoneRequestHandler();
        }
    }

    private void handleIfFragmentPausedRequestFail() {
        if (paused) {
            onFailRequest = true;
        } else {
            onFail();
        }
    }

    private void onDoneRequestHandler() {
        onDoneRequest();
        if (getActivity() != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    public abstract void onDoneRequest();

    private void onFail() {
        onFailRequestHandler(failMsg,failStatusCode);
    }

    private void onFailRequestHandler(String msg, int statusCode) {
        onFailRequest(msg,statusCode);
        if (getActivity() != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    public abstract void onFailRequest(String msg, int statusCode);
}
