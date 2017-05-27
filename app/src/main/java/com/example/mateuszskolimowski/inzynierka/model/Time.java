package com.example.mateuszskolimowski.inzynierka.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mateuszskolimowski.inzynierka.utils.Utils;

/**
 * Created by Mateusz Skolimowski on 30.03.2017.
 */
public class Time implements Parcelable{
    private int hour;
    private int minute;

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }


    @Override
    public boolean equals(Object obj) {
        Time comparedTime = (Time) obj;
        if(comparedTime.getHour() == this.getHour() && comparedTime.getMinute() == this.getMinute())
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return Utils.formatTime(getHour()) + ":" + Utils.formatTime(getMinute());
    }

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    protected Time(Parcel in) {
        hour = in.readInt();
        minute = in.readInt();
    }

    public static final Creator<Time> CREATOR = new Creator<Time>() {
        @Override
        public Time createFromParcel(Parcel in) {
            return new Time(in);
        }

        @Override
        public Time[] newArray(int size) {
            return new Time[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(hour);
        parcel.writeInt(minute);
    }
    /** funkcja porownuje dwa czasy. jezeli pierwszy jest "wiekszy" to zwraca true, jezeli drugi to false*/
    public static boolean compareTimes(Time firstTime, Time secondTime) {
        if(firstTime.getHour() > secondTime.getHour()){
            return true;
        } else if(firstTime.getHour() == secondTime.getHour()){
            if(firstTime.getMinute() > secondTime.getMinute()){
                return true;
            }
        }
        return false;
    }

    public static long convertTimeToLong(Time startTime) {
        return (startTime.getHour()*60 + startTime.getMinute())*60*1000;
    }
}
