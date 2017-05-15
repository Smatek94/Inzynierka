package com.example.mateuszskolimowski.inzynierka.application;

import android.app.Application;

import com.example.mateuszskolimowski.inzynierka.sqlite.SQLiteHelper;

/**
 * Created by Mateusz Skolimowski on 05.04.2017.
 */
public class MyApplication extends Application {
    private SQLiteHelper sqLiteHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        initDatabase();
    }

    private void initDatabase() {
        sqLiteHelper = new SQLiteHelper(this);
    }

    public SQLiteHelper getSqLiteHelper(){
        return sqLiteHelper;
    }
}
