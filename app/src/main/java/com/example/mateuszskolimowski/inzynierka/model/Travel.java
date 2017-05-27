package com.example.mateuszskolimowski.inzynierka.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * wykorzystywana na dwa sposoby.
 * 1. jako odleglosc miedzy punktem a jego destinationPlaceId
 * 2. jako suma czasow i odleglosci calej trasy
 */
public class Travel implements Parcelable{

    /**czas w ktorym znajduje sie trasa po odwiedzeniu danej ilosci punktow*/
    private  long routeTime;
    private long duration;
    private double distance;
    private String destinationPlaceId;

    public Travel(long duration, double distance, String destinationPlaceId) {
        this.duration = duration;
        this.distance = distance;
        this.destinationPlaceId = destinationPlaceId;
    }

    public Travel(long duration, double distance, long routeTime) {
        this.duration = duration;
        this.distance = distance;
        this.routeTime = routeTime;
    }

    public long getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public String getDestinationPlaceId() {
        return destinationPlaceId;
    }

    public long getRouteTime() {
        return routeTime;
    }

    protected Travel(Parcel in) {
        duration = in.readLong();
        distance = in.readDouble();
        destinationPlaceId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
        dest.writeDouble(distance);
        dest.writeString(destinationPlaceId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Travel> CREATOR = new Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };

    public void addDistance(double distance) {
        this.distance += distance;
    }

    public void addDuration(long duration) {
        this.duration += duration;
    }

    public void setRouteTime(long routeTime) {
        this.routeTime = routeTime;
    }
}
