package com.example.mateuszskolimowski.inzynierka.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
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


    private static final String LATELY_ADDED_ROUTE_POINTS_TABLE_NAME = "LATELY_ADDED_ROUTE_POINTS_TABLE_NAME";
    private static final String LATELY_ADDED_ROUTE_POINTS_AS_JSON_COLUMN_NAME = "LATELY_ADDED_ROUTE_POINTS_AS_JSON_COLUMN_NAME";
//    private static String LATELY_ADDED_ROUTE_POINTS_DATE_COLUMN_NAME = "LATELY_ADDED_ROUTE_POINTS_DATE_COLUMN_NAME";
    private static final String CREATE_LATELY_ADDED_ROUTE_POINTS_TABLE = "CREATE TABLE " + LATELY_ADDED_ROUTE_POINTS_TABLE_NAME +
            "(" + KEY_ID + " " + ID_OPTIONS + ", " +
//            LATELY_ADDED_ROUTE_POINTS_DATE_COLUMN_NAME + " REAL, " +
            LATELY_ADDED_ROUTE_POINTS_AS_JSON_COLUMN_NAME + " TEXT NOT NULL);";


    private SQLiteDatabase sqliteDatabase;

    public SQLiteHelper(Context context){
        super(context,DATABASE_NAME,null,DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Utils.debugLog("create Database");
        sqLiteDatabase.execSQL(CREATE_ROUTES_TABLE);
        sqLiteDatabase.execSQL(CREATE_LATELY_ADDED_ROUTE_POINTS_TABLE);
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

    public void addNewLatelyAddedRoutePoint(RoutePoint routePoint) {
        if(connectDataBase()){
            ArrayList<RoutePoint> latelyAddedRoutePoints = getLatelyAddedRoutePoints();
            if(latelyAddedRoutePoints.size() >= LATELY_ADDED_ROUTE_POINTS_LIMIT){
                deleteOldestRoutePoint(latelyAddedRoutePoints);
            }
            ContentValues contentValues = new ContentValues();
            Gson gson = new Gson();
            routePoint.setAddedDate(System.currentTimeMillis());
            contentValues.put(LATELY_ADDED_ROUTE_POINTS_AS_JSON_COLUMN_NAME,gson.toJson(routePoint));
//            contentValues.put(LATELY_ADDED_ROUTE_POINTS_DATE_COLUMN_NAME,System.currentTimeMillis());
            sqliteDatabase.insert(LATELY_ADDED_ROUTE_POINTS_TABLE_NAME,null,contentValues);
        }
    }

    private void deleteOldestRoutePoint(ArrayList<RoutePoint> latelyAddedRoutePoints) {
        long minDate = 99999999999999l;
        RoutePoint routePointToDelete = null;
        for(RoutePoint routePoint : latelyAddedRoutePoints){
            if(routePoint.getDate() < minDate){
                minDate = routePoint.getDate();
                routePointToDelete = routePoint;
            }
        }
        if(routePointToDelete != null) {
            Gson gson = new Gson();
            sqliteDatabase.delete(LATELY_ADDED_ROUTE_POINTS_TABLE_NAME,LATELY_ADDED_ROUTE_POINTS_AS_JSON_COLUMN_NAME + "=?" ,new String[]{gson.toJson(routePointToDelete)});
        }
    }

    public ArrayList<RoutePoint> getLatelyAddedRoutePoints() {
        ArrayList<RoutePoint> resultList = new ArrayList<>();
        if(connectDataBase()){
//            String[] columns = {LATELY_ADDED_ROUTE_POINTS_AS_JSON_COLUMN_NAME,LATELY_ADDED_ROUTE_POINTS_DATE_COLUMN_NAME};
            String[] columns = {LATELY_ADDED_ROUTE_POINTS_AS_JSON_COLUMN_NAME};
            Cursor cursor = sqliteDatabase.query(LATELY_ADDED_ROUTE_POINTS_TABLE_NAME,columns,null,null,null,null,null);
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    Gson gson = new Gson();
                    RoutePoint routePoint = gson.fromJson(cursor.getString(cursor.getColumnIndex(LATELY_ADDED_ROUTE_POINTS_AS_JSON_COLUMN_NAME)), RoutePoint.class);
//                    routePoint.setAddedDate(cursor.getLong(cursor.getColumnIndex(LATELY_ADDED_ROUTE_POINTS_AS_JSON_COLUMN_NAME)));
                    resultList.add(routePoint);
                } while (cursor.moveToNext());
            }
            return resultList;
        } else {
            return new ArrayList<>();
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
}
