package com.example.mateuszskolimowski.inzynierka.model;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

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
    private ArrayList<RoutePoint> routePointsIdList;

    public Route(String routeName, Time startTime, Time endTime, ArrayList<RoutePoint> routePointsIdList, int id) {
        this.routeName = routeName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.routePointsIdList = routePointsIdList;
        this.id = id;
    }

    protected Route(Parcel in) {
        id = in.readInt();
        routeName = in.readString();
        startTime = in.readParcelable(Time.class.getClassLoader());
        endTime = in.readParcelable(Time.class.getClassLoader());
        routePointsIdList = in.createTypedArrayList(RoutePoint.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(routeName);
        dest.writeParcelable(startTime, flags);
        dest.writeParcelable(endTime, flags);
        dest.writeTypedList(routePointsIdList);
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
        return routePointsIdList;
    }

    public static int createRouteId(Activity activity) {
        return Utils.getSQLiteHelper(activity).getRandomNotExistingRouteId();
    }

    public void setRoutePoints(ArrayList<RoutePoint> routePointsIdList) {
        this.routePointsIdList = routePointsIdList;
    }

    public void addRoutePointId(RoutePoint routePointPlaceId) {
        this.routePointsIdList.add(routePointPlaceId);
    }

    public void deleteRoutePointId(RoutePoint routePointPlaceId) {
        this.routePointsIdList.remove(routePointPlaceId);
    }
}
