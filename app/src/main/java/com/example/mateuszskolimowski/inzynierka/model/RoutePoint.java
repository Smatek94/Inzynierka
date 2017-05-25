package com.example.mateuszskolimowski.inzynierka.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mateusz Skolimowski on 17.05.2017.
 */
public class RoutePoint implements Parcelable{

    public static final int VISITED = 1;
    public static final int NOT_VISITED = 0;
    private String id;
    private Time startTime;
    private Time endTime;
    private LatLng latLng;
    private String placeName;
    private long date;
    private int visited;

    public RoutePoint(String id, Time startTime, Time endTime, LatLng latLng, String placeName, long date, int visited) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.latLng = latLng;
        this.placeName = placeName;
        this.date = date;
        this.visited = visited;
    }

    public String getId() {
        return id;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getPlaceName() {
        return placeName;
    }

    public long getDate() {
        return date;
    }

    public int getVisited() {
        return visited;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setVisited(boolean visited) {
        if(visited)
            this.visited = VISITED;
        else
            this.visited = NOT_VISITED;
    }

    protected RoutePoint(Parcel in) {
        id = in.readString();
        startTime = in.readParcelable(Time.class.getClassLoader());
        endTime = in.readParcelable(Time.class.getClassLoader());
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        placeName = in.readString();
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeParcelable(startTime, i);
        parcel.writeParcelable(endTime, i);
        parcel.writeParcelable(latLng, i);
        parcel.writeString(placeName);
        parcel.writeLong(date);
        parcel.writeInt(visited);
    }
}
