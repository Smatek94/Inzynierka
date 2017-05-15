package com.example.mateuszskolimowski.inzynierka.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.sql.*;

/**
 * Created by Mateusz Skolimowski on 26.03.2017.
 */
public class RoutePoint implements Parcelable{

    public static final int VISITED = 1;
    private static final int NOT_VISITED = 0;
    private LatLng routePointLatLng;
    private String routePointPlaceName;
    private String routePointPlaceId;
    private Time routePointStartTime;
    private Time routePointEndTime;
    private long date;
    private int visited;

    protected RoutePoint(Parcel in) {
        routePointLatLng = in.readParcelable(LatLng.class.getClassLoader());
        routePointPlaceName = in.readString();
        routePointPlaceId = in.readString();
        routePointStartTime = in.readParcelable(Time.class.getClassLoader());
        routePointEndTime = in.readParcelable(Time.class.getClassLoader());
        date = in.readLong();
        visited = in.readInt();
    }

    public static final Creator<RoutePoint> CREATOR = new Creator<RoutePoint>() {
        @Override
        public RoutePoint createFromParcel(Parcel in) {
            return new RoutePoint(in);
        }

        @Override
        public RoutePoint[] newArray(int size) {
            return new RoutePoint[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        RoutePoint routePoint = (RoutePoint) obj;
        return (routePoint.getRoutePointName().equals(getRoutePointName())
                && routePoint.getRoutePointLatLng().equals(getRoutePointLatLng())
                && routePoint.getRoutePointEndTime().equals(getRoutePointEndTime())
                && routePoint.getRoutePointPlaceId().equals(getRoutePointPlaceId())
                && routePoint.getRoutePointStartTime().equals(getRoutePointStartTime())
        );
    }

    public RoutePoint(Place routePointPlace, Time routePointStartTime, Time routePointEndTime) {
        this.routePointPlaceName = "" + routePointPlace.getName();
        this.routePointPlaceId = routePointPlace.getId();
        this.routePointLatLng = routePointPlace.getLatLng();
        this.routePointStartTime = routePointStartTime;
        this.routePointEndTime = routePointEndTime;
    }

    public RoutePoint(LatLng routePointLatLng, String routePointPlaceName, String routePointPlaceId, Time routePointStartTime, Time routePointEndTime) {
        this.routePointLatLng = routePointLatLng;
        this.routePointPlaceName = routePointPlaceName;
        this.routePointPlaceId = routePointPlaceId;
        this.routePointStartTime = routePointStartTime;
        this.routePointEndTime = routePointEndTime;
    }

    public void setAddedDate(long date) {
        this.date = date;
    }

    public Time getRoutePointStartTime() {
        return routePointStartTime;
    }

    public Time getRoutePointEndTime() {
        return routePointEndTime;
    }

    public long getDate() {
        return date;
    }

    public void setRoutePointStartTime(Time routePointStartTime) {
        this.routePointStartTime = routePointStartTime;
    }

    public void setRoutePointEndTime(Time routePointEndTime) {
        this.routePointEndTime = routePointEndTime;
    }

    public String getRoutePointName() {
        return this.routePointPlaceName;
    }

    public String getRoutePointPlaceId() {
        return routePointPlaceId;
    }

    public LatLng getRoutePointLatLng() {
        return routePointLatLng;
    }

    public void setVisited(boolean visited) {
        if(visited)
            this.visited = VISITED;
        else
            this.visited = NOT_VISITED;
    }

    public int getVisited() {
        return visited;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(routePointLatLng, i);
        parcel.writeString(routePointPlaceName);
        parcel.writeString(routePointPlaceId);
        parcel.writeParcelable(routePointStartTime, i);
        parcel.writeParcelable(routePointEndTime, i);
        parcel.writeLong(date);
        parcel.writeInt(visited);
    }
}
