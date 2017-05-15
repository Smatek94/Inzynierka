package com.example.mateuszskolimowski.inzynierka.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SharedPreferencesUtils {

    public static final String CALENDAR_DIALOG_TAG = SharedPreferencesUtils.class.getName() + "CALENDAR_DIALOG_TAG";
    public static String PREFERENCES_KEY = "pref_key";

    public static String getStringFromSharedPreferences(Context context, String tag) {
        Utils.debugLog("pobieram string o tagu : " + tag + " z shared preferences");
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(tag, "");
    }

    public static void setStringToSharedPreferences(Context context, String tag, String stringToPut) {
        Utils.debugLog("wrzucam string : " + stringToPut + " o tagu : " + tag + " z shared preferences");
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tag, stringToPut);
        editor.commit();
    }

    public static Object getObjectFromSharedPreferences(Context context, String tag, Class classType) {
        Utils.debugLog("pobieram string o tagu : " + tag + " z shared preferences");
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        return gson.fromJson(sharedPreferences.getString(tag, ""), classType);
    }

    public static void setObjectToSharedPreferences(Context context, String tag, Object objectToPut) {
        Utils.debugLog("wrzucam Object : " + objectToPut + " o tagu : " + tag + " z shared preferences");
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(tag, gson.toJson(objectToPut));
        editor.commit();
    }

    public static boolean getBooleanFromSharedPreferences(Context context, String tag) {
        Utils.debugLog("pobieram boolean o tagu : " + tag + " z shared preferences");
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(tag, true);
    }

    public static void setBooleanToSharedPreferences(Context context, String tag, boolean booleanToPut) {
        Utils.debugLog("wrzucam boolean : " + booleanToPut + " o tagu : " + tag + " z shared preferences");
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(tag, booleanToPut);
        editor.commit();
    }

    public static boolean shouldCalendarDialogBeShown(Context context){
        return getBooleanFromSharedPreferences(context,CALENDAR_DIALOG_TAG);
    }

    public static void setShouldCalendarDialogBeShown(Context context, boolean shouldCalendarDialogBeShown){
        setBooleanToSharedPreferences(context,CALENDAR_DIALOG_TAG,shouldCalendarDialogBeShown);
    }
}
