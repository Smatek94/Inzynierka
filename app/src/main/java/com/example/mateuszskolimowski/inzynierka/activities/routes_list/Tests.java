package com.example.mateuszskolimowski.inzynierka.activities.routes_list;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.ArrayMap;

import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.model.Time;
import com.example.mateuszskolimowski.inzynierka.model.Travel;
import com.example.mateuszskolimowski.inzynierka.utils.SharedPreferencesUtils;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.example.mateuszskolimowski.inzynierka.vns.VNS;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by Mateusz Skolimowski on 31.07.2017.
 */

public class Tests {

    public static void test(File file, Context context,int number) throws IOException {
        File[] files = file.listFiles();
        File textFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                (SharedPreferencesUtils.getAlgorithmWorkingTime(context)+1) + "sec " + "test" + number + "date" + System.currentTimeMillis() + ".txt");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(textFile);
            for (File file1 : files) {
                VNS.VNSRoute dataFromFile = getDataFromFile(file1, context);
//                VNS.VNSRoute dataFromFile = getDataFromFile(files[0], context);
                String s = dataFromFile.getTravel().getDuration() + "\n";
                stream.write(s.getBytes());
            }
        } finally {
            stream.close();
        }
        MediaScannerConnection.scanFile(context, new String[] {Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()}, null, null);
        alternativeScan(file.getAbsolutePath(),context);
    }

    private static void alternativeScan(final String filename, Context context) {
        final File file = new File(filename);
        final Uri fileUri = Uri.fromFile(file);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            context.getApplicationContext().sendBroadcast(new Intent("android.hardware.action.NEW_PICTURE", fileUri));
        }
        context.getApplicationContext().sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", fileUri));
        final Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(fileUri);
        context.getApplicationContext().sendBroadcast(intent);
    }

    private static VNS.VNSRoute getDataFromFile(File file, Context context) /**/ {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            int nodesCount = Integer.parseInt(line);
            RoutePointDestination routePointDestination = new RoutePointDestination("0");
            double[] doublesFromLine = getDoublesFromLine(br);
            for (int i = 1; i < doublesFromLine.length; i++) {
                routePointDestination.addTravel(new Travel((long) doublesFromLine[i], doublesFromLine[i], i + ""));
            }
            ArrayMap<String, ArrayMap<String, Travel>> distFromPointMap = new ArrayMap<>();
            for (int i = 1; i < nodesCount; i++) {
                doublesFromLine = getDoublesFromLine(br);
                ArrayMap<String, Travel> map = new ArrayMap<>();
                for (int j = 0; j < doublesFromLine.length; j++) {
                    map.put(j + "", new Travel((long) doublesFromLine[j], doublesFromLine[j], j + ""));
                }
                distFromPointMap.put(i + "", map);
            }
            br.readLine();
            ArrayList<RoutePoint> routePointsList = new ArrayList<>();
            for (int i = 1; i < nodesCount; i++) {
                doublesFromLine = getDoublesFromLine(br);
                RoutePoint routePoint = new RoutePoint(
                        i + "",
                        new Time((int) doublesFromLine[0], 0),
                        new Time((int) doublesFromLine[1], 0),
                        new LatLng(0, 0), "test", 1, 1);
                routePointsList.add(routePoint);
            }
            br.close();

            Route route = new Route(
                    "testName",
                    new Time(0, 0),
                    new Time(0, 0),
                    routePointsList,
                    0);
            return VNS.VNS(route, routePointDestination, distFromPointMap, true, context);
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return null;
    }

    private static ArrayList<RoutePoint> createRoutePointsList(ArrayList<FileLine> fileLinesList) {
        ArrayList<RoutePoint> routePointsList = new ArrayList<>();
        for (int i = 1; i < fileLinesList.size(); i++) {
            FileLine fileLine = fileLinesList.get(i);
            RoutePoint routePoint = new RoutePoint(
                    fileLine.getId(),
                    new Time((int) fileLine.getReadyTime(), 0),
                    new Time((int) fileLine.getDueTime(), 0),
                    new LatLng(0, 0), "test", 1, 1);
            routePointsList.add(routePoint);
        }
        return routePointsList;
    }

    private static ArrayList<FileLine> createFileLinesList(BufferedReader br) throws IOException {
        ArrayList<FileLine> fileLinesList = new ArrayList<>();
        while (true) {
            FileLine fileLine = new FileLine(getDoublesFromLine(br));
            if (fileLine.getId().equals("999.0"))
                break;
            fileLinesList.add(fileLine);
        }
        return fileLinesList;
    }

    private static ArrayMap<String, ArrayMap<String, Travel>> createDistMatrix(ArrayList<FileLine> fileLinesList) {
        ArrayMap<String, ArrayMap<String, Travel>> distFromPointMap = new ArrayMap<>();
        for (int i = 1; i < fileLinesList.size(); i++) {
            FileLine fileLine = fileLinesList.get(i);
            ArrayMap<String, Travel> map = new ArrayMap<>();
            for (int j = 0; j < fileLinesList.size(); j++) {
                FileLine fileLine1 = fileLinesList.get(j);
                map.put(fileLine1.getId(), calculateDist(fileLine, fileLine1));
            }
            distFromPointMap.put(fileLine.getId(), map);
        }
        return distFromPointMap;
    }

    private static Travel calculateDist(FileLine fileLine, FileLine fileLine1) {
        double x = fileLine1.getXcord() - fileLine.getXcord();
        double y = fileLine1.getYcord() - fileLine.getYcord();
        if (x < 0)
            x = -x;
        if (y < 0)
            y = -y;
        double sqrt1 = Math.pow(x, 2);
        double sqrt2 = Math.pow(y, 2);
        double distAndDur = (double) ((int) Math.pow(sqrt1 + sqrt2, 0.5));
//        double distAndDur = Math.pow(sqrt1 + sqrt2 ,0.5);
//        Utils.debugLog("dist : " + distAndDur);
        return new Travel((long) distAndDur, distAndDur, fileLine1.getId());
    }

    private static class FileLine {

        private String id;
        private double xCord;
        private double yCord;
        private double demand;
        private double readyTime;
        private double dueTime;
        private double serviceTime;


        public FileLine(double[] doublesFromLine) {
            id = String.valueOf(doublesFromLine[0]);
            xCord = doublesFromLine[1];
            yCord = doublesFromLine[2];
            demand = doublesFromLine[3];
            readyTime = doublesFromLine[4];
            dueTime = doublesFromLine[5];
            serviceTime = doublesFromLine[6];
        }

        public String getId() {
            return id;
        }

        public double getXcord() {
            return xCord;
        }

        public double getYcord() {
            return yCord;
        }

        public double getReadyTime() {
            return readyTime;
        }

        public double getDueTime() {
            return dueTime;
        }
    }

    private static RoutePointDestination createFromDepot(ArrayList<FileLine> fileLines) {
        RoutePointDestination routePointDestination = new RoutePointDestination(fileLines.get(0).getId());
        for (int i = 1; i < fileLines.size(); i++) {
            routePointDestination.addTravel(calculateDist(fileLines.get(0), fileLines.get(i)));
        }
        return routePointDestination;
    }

    private static double[] getDoublesFromLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        String[] splited = line.trim().split("\\s+");
        double[] numbers = new double[splited.length];
        for (int i = 0; i < splited.length; i++) {
            numbers[i] = Double.parseDouble(splited[i]);
        }
        return numbers;
    }
}
