package com.example.mateuszskolimowski.inzynierka.vns;

import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;

import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.model.Time;
import com.example.mateuszskolimowski.inzynierka.model.Travel;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;
import com.google.android.gms.analytics.HitBuilders;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Mateusz Skolimowski on 23.07.2017.
 */

public class VNS {
    private static final int NUMBER_OF_TRIES = 100;
    private static ArrayMap<String, ArrayMap<String, Travel>> distanceMatrix;
    private static RoutePointDestination routePointDestinationFromYourLocalization;
    private static String isRouteFound;
    private static boolean test;

    public static String VNS(Route routeArg,
                             RoutePointDestination routePointDestinations,
                             ArrayMap<String, ArrayMap<String, Travel>> distMatrix, boolean isTest) {
        test = isTest;
        return VNS(routeArg, routePointDestinations, distMatrix);
    }

    public static String VNS(Route routeArg,
                             RoutePointDestination routePointDestinations,
                             ArrayMap<String, ArrayMap<String, Travel>> distMatrix) {
        distanceMatrix = distMatrix;
        routePointDestinationFromYourLocalization = routePointDestinations;
        ArrayList<RoutePoint> routePoints = routeArg.getRoutePoints();
        for (int i = 0; i < routePoints.size(); i++) {
            for (int j = 0; j < routePoints.size() - i - 1; j++) {
                if (Time.compareTimes(routePoints.get(j).getEndTime(), routePoints.get(j + 1).getEndTime())) {
                    Collections.swap(routePoints, j, j + 1);
                }
            }
        }

      /*  swap(routePoints,0,"36.0");
        swap(routePoints,1,"14.0");
        swap(routePoints,2,"12.0");
        swap(routePoints,3,"9.0");
        swap(routePoints,4,"19.0");
        swap(routePoints,5,"43.0");
        swap(routePoints,6,"52.0");
        swap(routePoints,7,"27.0");
        swap(routePoints,8,"51.0");
        swap(routePoints,9,"13.0");
        swap(routePoints,10,"11.0");
        Travel travel = calculateRouteDistance(routePoints);*/
        VNSRoute vnsRoute = new VNSRoute(routePoints);
        for (int i = 0; i < NUMBER_OF_TRIES; i++) {
            if (i % 100 == 0) {
                Utils.debugLog("i = " + i);
            }
            for (int k = 1; k < routePoints.size(); k++) {
                ArrayList<RoutePoint> tempRoutePoints = shake(routePoints, k);
                VNSRoute impovedRoute = improvment(new VNSRoute(tempRoutePoints));
                if (impovedRoute.getTravel() != null) {
                    isRouteFound = "found";
                    if (vnsRoute.getTravel() == null || impovedRoute.getTravel().getDistance() < vnsRoute.getTravel().getDistance()) {
                        vnsRoute = impovedRoute;
                        routePoints = tempRoutePoints;
                        break;
                    }
                }
            }
        }
        if (vnsRoute.getTravel() != null) {
            Utils.debugLog("dist  = " + vnsRoute.getTravel().getDistance());
            String s = "";
            for (RoutePoint routePoint : vnsRoute.getRoutePoints()) {
                s += routePoint.getId().substring(0, routePoint.getId().length() - 2) + ";";
            }
            Utils.debugLog(s);
            routeArg.setRoutePoints(vnsRoute.getRoutePoints());
        }
        return isRouteFound;
    }

    private static void swap(ArrayList<RoutePoint> routePoints, int index, String s) {
        for (int i = index; i < routePoints.size(); i++) {
            if (routePoints.get(i).getId().equals(s)) {
                Collections.swap(routePoints, index, i);
                break;
            }
        }
    }

    private static VNSRoute improvment(VNSRoute vnsRoute) {
        double initDist = 0;
        if (vnsRoute.getTravel() != null) {
            initDist = vnsRoute.getTravel().getDistance();
        }
        for (int i = 0; i < vnsRoute.getRoutePoints().size() - 1; i++) {
            Collections.swap(vnsRoute.getRoutePoints(), i, i + 1);
            vnsRoute.calculateDistance();
            if (vnsRoute.getTravel() != null && (vnsRoute.getTravel().getDistance() < initDist || initDist == 0)) {
                initDist = vnsRoute.getTravel().getDistance();
            } else {
                Collections.swap(vnsRoute.getRoutePoints(), i + 1, i);
                vnsRoute.calculateDistance();
            }
        }
        return vnsRoute;
    }

    private static class VNSRoute {
        private ArrayList<RoutePoint> routePoints;
        private Travel travel;

        public VNSRoute(ArrayList<RoutePoint> routePoints) {
            this.routePoints = new ArrayList<>(routePoints);
            this.travel = calculateRouteDistance(routePoints);
        }

        public void setRoutePoints(ArrayList<RoutePoint> routePoints) {
            this.routePoints = routePoints;
        }

        public void setTravel(Travel travel) {
            this.travel = travel;
        }

        public ArrayList<RoutePoint> getRoutePoints() {
            return routePoints;
        }

        public Travel getTravel() {
            return travel;
        }

        public Travel calculateDistance() {
            this.travel = calculateRouteDistance(routePoints);
            return travel;
        }
    }

    public static Travel getTravelFromPointToPoint(String lastRoutePoint, String rp) {
        if (lastRoutePoint != null) {
            return distanceMatrix.get(lastRoutePoint).get(rp);
        } else {
            ArrayList<Travel> travelToPointList = routePointDestinationFromYourLocalization.getTravelToPointList();
            for (Travel t : travelToPointList) {
                if (t.getDestinationPlaceId().equals(rp)) {
                    return t;
                }
            }
            return null;
        }
    }

    public static Travel calculateRouteDistance(ArrayList<RoutePoint> foundRoute) {
        Travel travel = new Travel(0, 0, 0);
        for (int i = -1; i < foundRoute.size() - 1; i++) {
            String fromRoutePointId;
            if (i == -1)
                fromRoutePointId = null;
            else
                fromRoutePointId = foundRoute.get(i).getId();
            RoutePoint toRoutePouint = foundRoute.get(i + 1);
            Travel travelFromPointToPoint = getTravelFromPointToPoint(fromRoutePointId, toRoutePouint.getId());
            long endTime = Time.convertTimeToLong(toRoutePouint.getEndTime(), test);
            long routeTimeAndDuration = travel.getRouteTime() + travelFromPointToPoint.getDuration();
            if (endTime < routeTimeAndDuration) {
                return null;
            } else {
                long startTime = Time.convertTimeToLong(toRoutePouint.getStartTime(), test);
                if (startTime >= routeTimeAndDuration) {
                    travel.setRouteTime(Time.convertTimeToLong(toRoutePouint.getStartTime(), test));
                } else {
                    travel.setRouteTime(travel.getRouteTime() + travelFromPointToPoint.getDuration());
                }
            }
            travel.addDistance(travelFromPointToPoint.getDistance());
            travel.addDuration(travelFromPointToPoint.getDuration());
        }
        if (test) {
            Travel travelBackToDepo = getTravelFromPointToPoint(foundRoute.get(foundRoute.size() - 1).getId(), "1.0");
            travel.addDistance(travelBackToDepo.getDistance());
            travel.addDuration(travelBackToDepo.getDuration());
        }
        travel.setRouteTime(travel.getRouteTime() - Time.convertTimeToLong(foundRoute.get(0).getStartTime(), test));
        return travel;
    }

    private static ArrayList<RoutePoint> shake(ArrayList<RoutePoint> routePoints, int k) {
        ArrayList<RoutePoint> tempRoutePoints = new ArrayList<>(routePoints);
        for (int i = 0; i < k; i++) {
            Random random = new Random(System.currentTimeMillis());
            int firstToSwap = random.nextInt(tempRoutePoints.size());
            int secondToSwap = firstToSwap;
            while (secondToSwap == firstToSwap) {
                secondToSwap = random.nextInt(tempRoutePoints.size());
            }
            Collections.swap(tempRoutePoints, firstToSwap, secondToSwap);
        }
        return tempRoutePoints;
    }

    /**
     * funkcja sprawdza czy wogole jest mozliwosc stworzenia trasy ktora spelnia wymagania uzytkownika
     * np. punkt trasy moze miec okno czasowe poza oknem czasowym calej trasy
     */
    public static boolean checkIfRouteIsFeasible(Route route) {
        for (RoutePoint routePoint : route.getRoutePoints()) {
            if (Time.compareTimes(route.getStartTime(), routePoint.getEndTime()) ||
                    Time.compareTimes(routePoint.getStartTime(), route.getEndTime())) {
                return false;
            }
        }
        return true;
    }
}
