package com.example.mateuszskolimowski.inzynierka.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mateusz Skolimowski on 17.05.2017.
 */
public class Travel implements Parcelable{

    private long duration;
    private double distance;
    private String destinationPlaceId;

    public Travel(long duration, double distance, String destinationPlaceId) {
        this.duration = duration;
        this.distance = distance;
        this.destinationPlaceId = destinationPlaceId;
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
}
