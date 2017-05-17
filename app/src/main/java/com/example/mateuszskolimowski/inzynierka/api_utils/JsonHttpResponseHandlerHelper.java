package com.example.mateuszskolimowski.inzynierka.api_utils;

import com.example.mateuszskolimowski.inzynierka.interfaces.OnDoneObjectListener;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Mateusz Skolimowski on 21.02.2017.
 */

public class JsonHttpResponseHandlerHelper extends JsonHttpResponseHandler {

    private final OnDoneObjectListener onDoneObjectListener;

    public JsonHttpResponseHandlerHelper(OnDoneObjectListener onDoneObjectListener) {
        this.onDoneObjectListener = onDoneObjectListener;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Utils.debugLog("response : " + response);
        onDoneObjectListener.onDone(response,statusCode);
        super.onSuccess(statusCode, headers, response);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Utils.debugLog("response : " + response);
        onDoneObjectListener.onDone(response,statusCode);
        super.onSuccess(statusCode, headers, response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Utils.debugLog("response : " + errorResponse);
        onDoneObjectListener.onDone(errorResponse,statusCode);
        super.onFailure(statusCode, headers, throwable, errorResponse);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Utils.debugLog("response : " + responseString);
        onDoneObjectListener.onDone(responseString,statusCode);
        super.onFailure(statusCode, headers, responseString, throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        Utils.debugLog("response : " + errorResponse);
        onDoneObjectListener.onDone(errorResponse,statusCode);
        super.onFailure(statusCode, headers, throwable, errorResponse);
    }

}
