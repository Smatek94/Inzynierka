package com.example.mateuszskolimowski.inzynierka.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.application.MyApplication;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.AreYouSureDialog;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.MsgDialog;
import com.example.mateuszskolimowski.inzynierka.sqlite.SQLiteHelper;

/**
 * Created by Mateusz Skolimowski on 07.03.2017.
 */
public class Utils {
    public static void debugLog(String msg) {
        if (Config.isDebug) {
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
            Log.e("DEBUGTEST " + className + "." + methodName + "():" + lineNumber, msg);
        }

    }

    public static void showSnackbar(View view, String msg) {
        Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show();
    }

    public static void initToolbarTitle(ActionBar supportActionBar, String title) {
        supportActionBar.setTitle(title);
    }


    public static String formatTime(int time){
        if(time > 9){
            return ""+time;
        } else {
            return "0"+time;
        }
    }

    public static SQLiteHelper getSQLiteHelper(Activity activity){
        MyApplication myApplication = (MyApplication) activity.getApplication();
        return myApplication.getSqLiteHelper();
    }

    public static void showMsgDialog(AppCompatActivity appCompatActivity, String msg) {
        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        MsgDialog msgDialog = (MsgDialog) fragmentManager.findFragmentByTag(MsgDialog.TAG);
        if (msgDialog == null) {
            msgDialog = MsgDialog.newInstance(msg);
            msgDialog.show(fragmentManager.beginTransaction(), MsgDialog.TAG);
        }
    }
}