package com.example.mateuszskolimowski.inzynierka.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mateuszskolimowski.inzynierka.utils.Utils;

/**
 * wykorzystywana na dwa sposoby.
 * 1. jako odleglosc miedzy punktem a jego destinationPlaceId
 * 2. jako suma czasow i odleglosci calej trasy
 */
public class Travel implements Parcelable {

    private static final long HALF_DAY = 12 * 60 * 60 * 1000;
    private static final long FOUR_AM = 4 * 60 * 60 * 1000;
    private static final long TWELVE_AM = 12 * 60 * 60 * 1000;
    private static final long TWO_PM = 14 * 60 * 60 * 1000;
    private static final long TEN_PM = 22 * 60 * 60 * 1000;
    /**
     * czas w ktorym znajduje sie trasa po odwiedzeniu danej ilosci punktow
     */
    private long routeTime;
    private long duration;
    private double distance;
    private String destinationPlaceId;
    private long failTime;
    private long routeStartTime;

    public Travel(long duration, double distance, String destinationPlaceId) {
        this.duration = duration;
        this.distance = distance;
        this.destinationPlaceId = destinationPlaceId;
    }

    public Travel(long duration, double distance, long routeTime, long failTime) {
        this.duration = duration;
        this.distance = distance;
        this.routeTime = routeTime;
        this.failTime = failTime;
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

    public Travel(Travel travel, long actualTime, boolean isTest) {
        if (!isTest)
            this.duration = (long) (travel.getDuration() * getMultiplier(actualTime));
        else
            this.duration = travel.getDuration();
        this.distance = travel.getDistance();
        this.destinationPlaceId = travel.getDestinationPlaceId();
    }

    private double getMultiplier(long actualTime) {
        if (actualTime <= FOUR_AM || (actualTime >= TWELVE_AM && actualTime <= TWO_PM) || actualTime >= TEN_PM) {
            return 1;
        } else if (actualTime <= HALF_DAY) {
            double d = 0.5 * Math.sin(0.125 * (actualTime / 1000 / 60 / 60 - 4) * Math.PI);
            return 1 + d;
        } else {
            double d = 0.5 * Math.sin(0.125 * (actualTime / 1000 / 60 / 60 - 14) * Math.PI);
            return 1 + d;
        }
    }

    protected Travel(Parcel in) {
        duration = in.readLong();
        distance = in.readDouble();
        destinationPlaceId = in.readString();
    }

    public void setRouteStartTime(long routeStartTime) {
        this.routeStartTime = routeStartTime;
    }

    public long getRouteStartTime() {
        return routeStartTime;
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

    public void addFailTime(long failTime) {
        this.failTime += failTime;
    }

    public long getFailTime() {
        return failTime;
    }
}
