package com.example.mateuszskolimowski.inzynierka.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mateusz Skolimowski on 17.05.2017.
 */
public class DestinationRoutePoint implements Parcelable{

    private long time;
    private double dist;
    private RoutePoint destinationRoutePoint;

    public DestinationRoutePoint(long time, double dist, RoutePoint destinationRoutePoint) {
        this.time = time;
        this.dist = dist;
        this.destinationRoutePoint = destinationRoutePoint;
    }

    protected DestinationRoutePoint(Parcel in) {
        time = in.readLong();
        dist = in.readDouble();
        destinationRoutePoint = in.readParcelable(RoutePoint.class.getClassLoader());
    }

    public static final Creator<DestinationRoutePoint> CREATOR = new Creator<DestinationRoutePoint>() {
        @Override
        public DestinationRoutePoint createFromParcel(Parcel in) {
            return new DestinationRoutePoint(in);
        }

        @Override
        public DestinationRoutePoint[] newArray(int size) {
            return new DestinationRoutePoint[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(time);
        parcel.writeDouble(dist);
        parcel.writeParcelable(destinationRoutePoint, i);
    }
}
