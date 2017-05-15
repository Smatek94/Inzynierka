package com.example.mateuszskolimowski.inzynierka.model;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.mateuszskolimowski.inzynierka.activities.routes_list.AddOrUpdateNewRouteActivity;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Mateusz Skolimowski on 26.03.2017.
 */
public class Route implements Parcelable{

    private int id;
    private String routeName;
    private Time startTime;
    private Time endTime;
    private ArrayList<RoutePoint> routePoints;

    public Route(String routeName, Time startTime, Time endTime, ArrayList<RoutePoint> routePoints,int id) {
        this.routeName = routeName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.routePoints = routePoints;
        this.id = id;
    }

    protected Route(Parcel in) {
        id = in.readInt();
        routeName = in.readString();
        startTime = in.readParcelable(Time.class.getClassLoader());
        endTime = in.readParcelable(Time.class.getClassLoader());
        routePoints = in.createTypedArrayList(RoutePoint.CREATOR);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public void setRoutePoints(ArrayList<RoutePoint> routePoints) {
        this.routePoints = routePoints;
    }

    public int getId() {

        return id;
    }

    public String getRouteName() {
        return routeName;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public ArrayList<RoutePoint> getRoutePoints() {
        return routePoints;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(routeName);
        dest.writeParcelable(startTime, flags);
        dest.writeParcelable(endTime, flags);
        dest.writeTypedList(routePoints);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public static int createRouteId(Activity activity) {
        return Utils.getSQLiteHelper(activity).getRandomNotExistingRouteId();
    }

    public void addRoutePoint(RoutePoint routePoint) {
        this.routePoints.add(routePoint);
    }

    public void deleteRoutePoint(RoutePoint routePoint) {
        this.routePoints.remove(routePoint);
    }
}
