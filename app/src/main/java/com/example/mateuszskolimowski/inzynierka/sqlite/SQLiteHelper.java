package com.example.mateuszskolimowski.inzynierka.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;

import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

import static android.R.attr.id;

/**
 * Created by Mateusz Skolimowski on 03.04.2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int LATELY_ADDED_ROUTE_POINTS_LIMIT = 5;
    private static final String DATABASE_NAME = SQLiteHelper.class.getName() + "ROUTES_DATABASE";
    private static final int DATA_BASE_VERSION = 1;
    private static final int DATABASE_OPEN_CONNECTION_NUMBER_OF_TRIES = 1;

    private static final String ROUTE_AS_JSON_COLUMN_NAME = "ROUTE_AS_JSON_COLUMN_NAME";
    private static final String ROUTES_TABLE_NAME = "ROUTES_TABLE_NAME";
    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String CREATE_ROUTES_TABLE = "CREATE TABLE " + ROUTES_TABLE_NAME +
            "(" + KEY_ID + " " + ID_OPTIONS + ", " +
            ROUTE_AS_JSON_COLUMN_NAME + " TEXT NOT NULL);";

    private static final String ROUTE_POINTS_DESTINATION_TABLE_NAME = "ROUTE_POINTS_DESTINATION_TABLE_NAME";
    private static final String ROUTE_POINTS_DESTINATION_AS_JSON_COLUMN_NAME = "ROUTE_POINTS_DESTINATION_AS_JSON_COLUMN_NAME";
    private static final String CREATE_ROUTE_POINTS_DESTINATION_TABLE = "CREATE TABLE " + ROUTE_POINTS_DESTINATION_TABLE_NAME +
            "(" + ROUTE_POINTS_DESTINATION_AS_JSON_COLUMN_NAME + " TEXT NOT NULL);";


    private SQLiteDatabase sqliteDatabase;

    public SQLiteHelper(Context context){
        super(context,DATABASE_NAME,null,DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Utils.debugLog("create Database");
        sqLiteDatabase.execSQL(CREATE_ROUTES_TABLE);
        sqLiteDatabase.execSQL(CREATE_ROUTE_POINTS_DESTINATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    private boolean tryOpeningDatabaseConnection(int databaseOpenConnectionNumberOfTries) {
        int i = 0;
        while(i < databaseOpenConnectionNumberOfTries){
            if(sqliteDatabase != null && sqliteDatabase.isOpen()){
                return true;
            } else {
                sqliteDatabase = getWritableDatabase();
                if(sqliteDatabase.isOpen()){
                    return true;
                }
            }
        }
        return false;
    }

    public void insertNewRoute(Route newRouteDataFromCallback) {
        if(connectDataBase()){
            ContentValues contentValues = new ContentValues();
            Gson gson = new Gson();
            contentValues.put(ROUTE_AS_JSON_COLUMN_NAME,gson.toJson(newRouteDataFromCallback));
            sqliteDatabase.insert(ROUTES_TABLE_NAME,null,contentValues);
        }
    }

    public void deleteRoute(Route routeToDelete){
        if(connectDataBase()){
            Gson gson = new Gson();
            sqliteDatabase.delete(ROUTES_TABLE_NAME,ROUTE_AS_JSON_COLUMN_NAME + "=?" ,new String[]{gson.toJson(routeToDelete)});
        }
    }

    public void updateRoute(Route routeResultDataFromCallback) {
        if(connectDataBase()){
            Route routeThatWasUpdated = findRouteToUpdate(routeResultDataFromCallback);
            ContentValues contentValues = new ContentValues();
            Gson gson = new Gson();
            contentValues.put(ROUTE_AS_JSON_COLUMN_NAME,gson.toJson(routeResultDataFromCallback));
            int r = sqliteDatabase.update(ROUTES_TABLE_NAME,
                    contentValues,
                    ROUTE_AS_JSON_COLUMN_NAME + "=?",
                    new String[]{gson.toJson(routeThatWasUpdated)});
            Utils.debugLog("r " + r);
        }
    }

    private Route findRouteToUpdate(Route routeResultDataFromCallback) {
        ArrayList<Route> routes = getRoutes();
        for(Route r : routes){
            if(r.getId() == routeResultDataFromCallback.getId()){
                return r;
            }
        }
        return null;
    }

    public ArrayList<Route> getRoutes() {
        ArrayList<Route> resultList = new ArrayList<>();
        if(connectDataBase()){
            String[] columns = {ROUTE_AS_JSON_COLUMN_NAME,KEY_ID};
            Cursor cursor = sqliteDatabase.query(ROUTES_TABLE_NAME,columns,null,null,null,null,null);
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    Gson gson = new Gson();
                    String fromDataBase = cursor.getString(cursor.getColumnIndex(ROUTE_AS_JSON_COLUMN_NAME));
                    Route route = gson.fromJson(fromDataBase, Route.class);
                    resultList.add(route);
                } while (cursor.moveToNext());
            }
            return resultList;
        } else {
            return new ArrayList<>();
        }
    }

    public boolean connectDataBase(){
        if(tryOpeningDatabaseConnection(DATABASE_OPEN_CONNECTION_NUMBER_OF_TRIES)){
            return true;
        } else {
            Utils.debugLog("nie udalo sie otworzyc bazy danych");
            return false;
        }
    }

    public void updateRoutePoints(Route route) {
        if(connectDataBase()){
            ArrayList<Route> routes = getRoutes();
            Route routeThatWasUpdated = null;
            for(Route r : routes){
                if(r.getId() == route.getId()){
                    routeThatWasUpdated = r;
                    break;
                }
            }
            ContentValues contentValues = new ContentValues();
            Gson gson = new Gson();
            contentValues.put(ROUTE_AS_JSON_COLUMN_NAME,gson.toJson(route));
            sqliteDatabase.update(ROUTES_TABLE_NAME,
                    contentValues,
                    ROUTE_AS_JSON_COLUMN_NAME + "=?",
                    new String[]{gson.toJson(routeThatWasUpdated)});
        }
    }

    public int getRandomNotExistingRouteId() {
        Random random = new Random(System.currentTimeMillis());
        ArrayList<Route> routes = getRoutes();
        return generateRandomId(random.nextInt(9999999),routes,random);
    }

    private int generateRandomId(int id, ArrayList<Route> routes, Random random) {
        if(routeIdExist(routes)){
            return generateRandomId(random.nextInt(9999999),routes,random);
        } else {
            return id;
        }
    }

    private boolean routeIdExist(ArrayList<Route> routes) {
        for(Route r : routes){
            if(r.getId() == id){
                return true;
            }
        }
        return false;
    }

    public ArrayList<RoutePointDestination> getRoutePointsDestinationList() {
        ArrayList<RoutePointDestination> resultList = new ArrayList<>();
        if(connectDataBase()){
            String[] columns = {ROUTE_POINTS_DESTINATION_AS_JSON_COLUMN_NAME};
            Cursor cursor = sqliteDatabase.query(ROUTE_POINTS_DESTINATION_TABLE_NAME,columns,null,null,null,null,null);
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    Gson gson = new Gson();
                    String fromDataBase = cursor.getString(cursor.getColumnIndex(ROUTE_POINTS_DESTINATION_AS_JSON_COLUMN_NAME));
                    RoutePointDestination routePointDestination = gson.fromJson(fromDataBase, RoutePointDestination.class);
                    resultList.add(routePointDestination);
                } while (cursor.moveToNext());
            }
            return resultList;
        } else {
            return new ArrayList<>();
        }
    }

    public void addRoutePointDestination(String selectedPlaceId) {
        if(connectDataBase()){
            ContentValues contentValues = new ContentValues();
            Gson gson = new Gson();
            contentValues.put(ROUTE_POINTS_DESTINATION_AS_JSON_COLUMN_NAME,gson.toJson(new RoutePointDestination(selectedPlaceId)));
            sqliteDatabase.insert(ROUTE_POINTS_DESTINATION_TABLE_NAME,null,contentValues);
        }
    }

    public RoutePointDestination getRoutePointDestinationFromDataBase(String selectedPlaceId) {
        ArrayList<RoutePointDestination> routePointDestinationArrayList = getRoutePointsDestinationList();
        for(RoutePointDestination rp : routePointDestinationArrayList){
            if(rp.getRoutePointPlaceId().equals(selectedPlaceId)){
                return rp;
            }
        }
        return null;
    }

    public void updateRoutePointsDestination(RoutePointDestination routePointDestinations) {
        if(connectDataBase()){
            RoutePointDestination routePointDestinationThatWasUpdated = findRoutePointDestinationToUpdate(routePointDestinations);
            ContentValues contentValues = new ContentValues();
            Gson gson = new Gson();
            contentValues.put(ROUTE_POINTS_DESTINATION_AS_JSON_COLUMN_NAME,gson.toJson(routePointDestinations));
            sqliteDatabase.update(ROUTE_POINTS_DESTINATION_TABLE_NAME,
                    contentValues,
                    ROUTE_POINTS_DESTINATION_AS_JSON_COLUMN_NAME + "=?",
                    new String[]{gson.toJson(routePointDestinationThatWasUpdated)});
        }
    }

    private RoutePointDestination findRoutePointDestinationToUpdate(RoutePointDestination routePointDestinations) {
        ArrayList<RoutePointDestination> routePointsDestinationList = getRoutePointsDestinationList();
        for(RoutePointDestination rpd : routePointsDestinationList){
            if(rpd.getRoutePointPlaceId().equals(routePointDestinations.getRoutePointPlaceId())){
                return rpd;
            }
        }
        return null;
    }

    public void updateRoutePointsDestination(ArrayList<RoutePointDestination> routePointDestinationsList) {
        for(RoutePointDestination rpd : routePointDestinationsList){
            updateRoutePointsDestination(rpd);
        }
    }

    public RoutePoint getRoutePoint(String destinationPlaceId) {
        for(Route route : getRoutes()){
            for(RoutePoint routePoint : route.getRoutePoints()){
                if (routePoint.getId().equals(destinationPlaceId)){
                    return routePoint;
                }
            }
        }
        return null;
    }
}
