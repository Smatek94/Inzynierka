package com.example.mateuszskolimowski.inzynierka.activities.routes_list;

import android.content.Intent;
import android.support.v4.util.ArrayMap;

import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.model.Time;
import com.example.mateuszskolimowski.inzynierka.model.Travel;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.example.mateuszskolimowski.inzynierka.vns.VNS;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mateusz Skolimowski on 31.07.2017.
 */

public class Tests {

    public static void test(File file) {
        getDataFromFile(file);
    }

    private static void getDataFromFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
//            int nodesCount = Integer.parseInt(line);
            ArrayList<FileLine> fileLinesList = createFileLinesList(br);
            br.close();

            ArrayList<RoutePoint> routePoints = createRoutePointsList(fileLinesList);
            Route route = new Route(
                    "testName",
                    new Time(0,0),
                    new Time(0,0),
                    routePoints,
                    0);
            VNS.VNS(route,createFromDepot(fileLinesList),createDistMatrix(fileLinesList),true);
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }

    private static ArrayList<RoutePoint> createRoutePointsList(ArrayList<FileLine> fileLinesList) {
        ArrayList<RoutePoint> routePointsList = new ArrayList<>();
        for(int i = 1 ; i < fileLinesList.size() ; i++){
            FileLine fileLine = fileLinesList.get(i);
            RoutePoint routePoint = new RoutePoint(
                    fileLine.getId(),
                    new Time((int) fileLine.getReadyTime(),0),
                    new Time((int) fileLine.getDueTime(),0),
                    new LatLng(0,0), "test", 1, 1);
            routePointsList.add(routePoint);
        }
        return routePointsList;
    }

    private static ArrayList<FileLine> createFileLinesList(BufferedReader br) throws IOException {
        ArrayList<FileLine> fileLinesList = new ArrayList<>();
        while(true){
            FileLine fileLine = new FileLine(getDoublesFromLine(br));
            if(fileLine.getId().equals("999.0"))
                break;
            fileLinesList.add(fileLine);
        }
        return fileLinesList;
    }

    private static ArrayMap<String, ArrayMap<String, Travel>> createDistMatrix(ArrayList<FileLine> fileLinesList) {
        ArrayMap<String, ArrayMap<String, Travel>> distFromPointMap = new ArrayMap<>();
        for(int i = 1 ; i < fileLinesList.size() ; i++){
            FileLine fileLine = fileLinesList.get(i);
            ArrayMap<String, Travel> map = new ArrayMap<>();
            for(int j = 0 ; j < fileLinesList.size() ; j++){
                FileLine fileLine1 = fileLinesList.get(j);
                map.put(fileLine1.getId(),calculateDist(fileLine,fileLine1));
            }
            distFromPointMap.put(fileLine.getId(),map);
        }
        return distFromPointMap;
    }

    private static Travel calculateDist(FileLine fileLine, FileLine fileLine1) {
        double x = fileLine1.getXcord() - fileLine.getXcord();
        double y = fileLine1.getYcord() - fileLine.getYcord();
        if(x < 0)
            x = -x;
        if(y < 0)
            y = -y;
        double sqrt1 = Math.pow(x,2);
        double sqrt2 = Math.pow(y,2);
        double distAndDur = (double)((int) Math.pow(sqrt1 + sqrt2 ,0.5));
//        double distAndDur = Math.pow(sqrt1 + sqrt2 ,0.5);
//        Utils.debugLog("dist : " + distAndDur);
        return new Travel((long) distAndDur, distAndDur, fileLine1.getId());
    }

    private static class FileLine{

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
        for(int i = 1 ; i < fileLines.size() ; i++){
            routePointDestination.addTravel(calculateDist(fileLines.get(0),fileLines.get(i)));
        }
        return routePointDestination;
    }

    private static double[] getDoublesFromLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        String[] splited = line.trim().split("\\s+");
        double[] numbers = new double[splited.length];
        for(int i = 0;i < splited.length;i++) {
            numbers[i] = Double.parseDouble(splited[i]);
        }
        return numbers;
    }
}
