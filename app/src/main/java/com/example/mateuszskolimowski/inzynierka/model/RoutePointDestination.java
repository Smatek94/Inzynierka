package com.example.mateuszskolimowski.inzynierka.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Mateusz Skolimowski on 26.03.2017.
 */
public class RoutePointDestination implements Parcelable{

    private String routePointPlaceId;
    private ArrayList<Travel> travelToPointList;

    public String getRoutePointPlaceId() {
        return routePointPlaceId;
    }

    public ArrayList<Travel> getTravelToPointList() {
        return travelToPointList;
    }

    public RoutePointDestination(String routePointPlaceId) {
        this.routePointPlaceId = routePointPlaceId;
        travelToPointList = new ArrayList<>();
    }

    protected RoutePointDestination(Parcel in) {
        routePointPlaceId = in.readString();
        travelToPointList = in.createTypedArrayList(Travel.CREATOR);
    }

    public static final Creator<RoutePointDestination> CREATOR = new Creator<RoutePointDestination>() {
        @Override
        public RoutePointDestination createFromParcel(Parcel in) {
            return new RoutePointDestination(in);
        }

        @Override
        public RoutePointDestination[] newArray(int size) {
            return new RoutePointDestination[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(routePointPlaceId);
        parcel.writeTypedList(travelToPointList);
    }

    public void addTravel(Travel travel) {
        this.travelToPointList.add(travel);
    }
}
