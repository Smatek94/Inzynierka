package com.example.mateuszskolimowski.inzynierka.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;


/**
 * Created by Mateusz Skolimowski on 10.11.2016.
 *
 * jak korzyststac z klasy:
 * 1. Skopiowac cala klase do swojego projektu.
 *    W gradlu dodac compile 'com.android.support:appcompat-v7:25.0.0' z najnowsza wersja.
 *
 * 2. Dodac permissiony o ktore chcesz zapytac przez klase do swojego manifestu
 *  (!!! UWAGA NA SCIEZKE danego permissiona, nie ktore maja inne np. com.android.voicemail.permission.ADD_VOICEMAIL, wszystkie
 *    sciezki mozna sprawdzic na https://developer.android.com/reference/android/Manifest.permission.html !!!)
 *
 * 3. w miejscu gdzie chcesz otrzymac permissiony nalezy uzyc funkcji
 *    boolean requestPermission(Context context, Activity activity, String[] permissions, int resultCode)
 *    gdzie :
 *    permission - lista permissionow o ktore pytamy
 *    resultCode - resultCode ktory obslgujujemy pozniej w onRequestPermissionsResult
 *    funkcja zwraca true jezeli wszystkie permissiony sa juz dodane i false jezeli musi o nie zapytac.
 *    Przykladowe uzycie:
     if(PermissionsUtils.requestPermission(MainActivity.this,MainActivity.this,permissionsRequested,resultCode)){
           doSomethingBecauseAllPermissionsAreGranted();
     }
 *
 * 4. w activity z ktorego wykonujemy zapytanie zaimplementowac onRequestPermissionsResult w nastepujacy sposob
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       if(requestCode == resultCode){
           PermissionsUtils.handleRequestPermissionResult(grantResults,MainActivity.this,permissionsRequested,whyPermissionShouldBeAcceptedMessages,"messageThatPermissionIsMustHaveMessage",
               new PermissionsUtils.OnPermissionResultListener() {
                   @Override
                   public void onDone() {
                       doSomethingBecauseAllPermissionsAreGranted();
                   }
               });
       }
  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
 *
 * funkcja  public static void handleRequestPermissionResult(
 * int[] grantResults,
 * final Activity activity,
 * String[] permissions,
 * String[] whyPermissionShouldBeAcceptedMessage, - mozna podac tablice stringow ktora okresla dla kazdego permissiona jaka wiadomosc mamy pokazac lub jeden String ktory pokazujemy dla wszystkich
 * final String messageThatPermissionIsMustHaveMessage,
 * final OnPermissionResultListener onPermissionResultListener) {
 *
 */

public class PermissionsUtils {

    public static boolean requestPermission(Context context, Activity activity, String[] permissions, int resultCode) {
        if (!checkIfAllPermissionsGranted(context, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, resultCode);
            return false;
        }
        return true;
    }

    public static boolean checkIfAllPermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    /**different why permission should be accepted messages for all permissions*/
    public static void handleRequestPermissionResult(int[] grantResults, final Activity activity, String[] permissions, String[] whyPermissionShouldBeAcceptedMessage, final String messageThatPermissionIsMustHaveMessage, final OnPermissionResultListener onPermissionResultListener) {
        if (checkIfAllGrantedResultsArePositive(grantResults)) {
            onPermissionResultListener.onDone();
        } else {
            checkIfShouldAskAgainForPermissions(permissions,grantResults,activity,whyPermissionShouldBeAcceptedMessage,messageThatPermissionIsMustHaveMessage);
        }
    }

    /**same why permission should be accepted messages for all permissions*/
    public static void handleRequestPermissionResult(int[] grantResults, final Activity activity, String[] permissions, String whyPermissionShouldBeAcceptedMessage, final String messageThatPermissionIsMustHaveMessage, final OnPermissionResultListener onPermissionResultListener) {
        if (checkIfAllGrantedResultsArePositive(grantResults)) {
            onPermissionResultListener.onDone();
        } else {
            checkIfShouldAskAgainForPermissions(permissions,grantResults,activity,whyPermissionShouldBeAcceptedMessage,messageThatPermissionIsMustHaveMessage);
        }
    }

    /**different why permission should be accepted messages for all permissions*/
    private static void checkIfShouldAskAgainForPermissions(String[] permissions, int[] grantResults, Activity activity, String[] whyPermissionShouldBeAcceptedMessage, String messageThatPermissionIsMustHaveMessage) {
        int resultIndex = 0;
        for(String permission : permissions){
            checkIfShouldAskAgainForPermission(permission,grantResults[resultIndex],activity,whyPermissionShouldBeAcceptedMessage[resultIndex],messageThatPermissionIsMustHaveMessage);
            resultIndex++;
        }
    }

    /**same why permission should be accepted messages for all permissions*/
    private static void checkIfShouldAskAgainForPermissions(String[] permissions, int[] grantResults, Activity activity, String whyPermissionShouldBeAcceptedMessage, String messageThatPermissionIsMustHaveMessage) {
        int resultIndex = 0;
        for(String permission : permissions){
            checkIfShouldAskAgainForPermission(permission,grantResults[resultIndex],activity,whyPermissionShouldBeAcceptedMessage,messageThatPermissionIsMustHaveMessage);
            resultIndex++;
        }
    }

    private static void checkIfShouldAskAgainForPermission(String permission, int grantResult, final Activity activity, String whyPermissionShouldBeAcceptedMessage, final String messageThatPermissionIsMustHaveMessage) {
        if(grantResult != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showDialogOK(whyPermissionShouldBeAcceptedMessage, activity,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        showDialogExitApp(activity, messageThatPermissionIsMustHaveMessage);
                                        break;
                                }
                            }
                        });
            }
            else {
                showDialogExitApp(activity, messageThatPermissionIsMustHaveMessage);
            }
        }
    }

    private static boolean checkIfAllGrantedResultsArePositive(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static void showDialogOK(String message, Activity activity, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("Tak", okListener)
                .setNegativeButton("Nie", okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    private static void showDialogExitApp(final Activity activity, String dialogExitAppMessage) {
        new AlertDialog.Builder(activity)
                .setMessage(dialogExitAppMessage)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finishAffinity();
                    }
                })
                .create()
                .show();
    }

    public interface OnPermissionResultListener {
        public void onDone();
    }
}
