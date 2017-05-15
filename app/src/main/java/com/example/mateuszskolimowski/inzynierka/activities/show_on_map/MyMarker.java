package com.example.mateuszskolimowski.inzynierka.activities.show_on_map;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Mateusz Skolimowski on 11.05.2017.
 */
public class MyMarker implements Parcelable{
    private final LatLng position;
    private final int markerNumber;
    private Marker marker;

    public MyMarker(Marker marker, int markerNumber) {
        this.marker = marker;
        this.position = marker.getPosition();
        this.markerNumber = markerNumber;
    }

    protected MyMarker(Parcel in) {
        position = in.readParcelable(LatLng.class.getClassLoader());
        markerNumber = in.readInt();
    }

    public static final Creator<MyMarker> CREATOR = new Creator<MyMarker>() {
        @Override
        public MyMarker createFromParcel(Parcel in) {
            return new MyMarker(in);
        }

        @Override
        public MyMarker[] newArray(int size) {
            return new MyMarker[size];
        }
    };

    public Marker getMarker() {
        return marker;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(position, i);
        parcel.writeInt(markerNumber);
    }
}
