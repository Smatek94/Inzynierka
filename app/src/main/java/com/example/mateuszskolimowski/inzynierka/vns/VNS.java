package com.example.mateuszskolimowski.inzynierka.vns;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.model.Time;
import com.example.mateuszskolimowski.inzynierka.model.Travel;
import com.example.mateuszskolimowski.inzynierka.utils.SharedPreferencesUtils;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Mateusz Skolimowski on 23.07.2017.
 */

public class VNS {
    private static final int NUMBER_OF_TRIES = 100;
    private static int ALGORITHM_WORKING_TIME;
    private static ArrayMap<String, ArrayMap<String, Travel>> distanceMatrix;
    private static RoutePointDestination routePointDestinationFromYourLocalization;
    private static String isRouteFound;
    private static boolean isTest;

    public static String VNS(Route routeArg,
                             RoutePointDestination routePointDestinations,
                             ArrayMap<String, ArrayMap<String, Travel>> distMatrix, boolean isTest,Context context) {
        VNS.isTest = isTest;
        ALGORITHM_WORKING_TIME = SharedPreferencesUtils.getAlgorithmWorkingTime(context) * 1000;
        return VNS(routeArg, routePointDestinations, distMatrix);
    }

    public static String VNS(Route routeArg,
                             RoutePointDestination routePointDestinations,
                             ArrayMap<String, ArrayMap<String, Travel>> distMatrix) {
        distanceMatrix = distMatrix;
        routePointDestinationFromYourLocalization = routePointDestinations;
        ArrayList<RoutePoint> routePoints = createStartRoute(routeArg.getRoutePoints());
        VNSRoute vnsRoute = new VNSRoute(routePoints, routeArg.getStartTime(), routeArg.getEndTime());
        long time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < ALGORITHM_WORKING_TIME) {
            for (int k = 1; k < routePoints.size(); k++) {
                ArrayList<RoutePoint> tempRoutePoints = shake(routePoints, k);
                VNSRoute impovedRoute = improvment(new VNSRoute(tempRoutePoints, routeArg.getStartTime(), routeArg.getEndTime()));
                if (impovedRoute.getTravel() != null) {
                    isRouteFound = "found";
                    if (vnsRoute.getTravel() == null || impovedRoute.getTravel().getFailTime() == 0) {
                        if (impovedRoute.getTravel().getDistance() < vnsRoute.getTravel().getDistance()) {
                            vnsRoute = impovedRoute;
                            routePoints = tempRoutePoints;
                            break;
                        }
                    } else if (impovedRoute.getTravel().getFailTime() < vnsRoute.getTravel().getFailTime()) {
                        vnsRoute = impovedRoute;
                        routePoints = tempRoutePoints;
                        break;
                    }
                }
            }
        }
        if (vnsRoute.getTravel() != null) {
            Utils.debugLog("dist  = " + vnsRoute.getTravel().getDistance());
            Utils.debugLog("failTime  = " + vnsRoute.getTravel().getFailTime());
            String s = "";
            for (RoutePoint routePoint : vnsRoute.getRoutePoints()) {
                s += routePoint.getId().substring(0, routePoint.getId().length() - 2) + ";";
            }
            Utils.debugLog(s);
            routeArg.setRoutePoints(vnsRoute.getRoutePoints());
        }
        return isRouteFound;
    }

    /**
     * order routepoints based on end time
     */
    private static ArrayList<RoutePoint> createStartRoute(ArrayList<RoutePoint> routePoints) {
        for (int i = 0; i < routePoints.size(); i++) {
            for (int j = 0; j < routePoints.size() - i - 1; j++) {
                if (Time.compareTimes(routePoints.get(j).getEndTime(), routePoints.get(j + 1).getEndTime())) {
                    Collections.swap(routePoints, j, j + 1);
                }
            }
        }
        return routePoints;
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
        private final Time startTime;
        private final Time endTime;
        private ArrayList<RoutePoint> routePoints;
        private Travel travel;

        public VNSRoute(ArrayList<RoutePoint> routePoints, Time startTime, Time endTime) {
            this.routePoints = new ArrayList<>(routePoints);
            this.startTime = startTime;
            this.endTime = endTime;
            calculateDistance();
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
            if(!isTest) {
                calculateDistanceForDifferentTimeWindows();
            } else {
                travel = calculateRouteDistance(routePoints,0);
            }
            return travel;
        }

        private void calculateDistanceForDifferentTimeWindows() {
            calculateBestHour();
            calculateBestMinute();
        }

        private void calculateBestMinute() {
            Travel improvedTravel;
            long minute = 60*1000;
            for (long routeStartingMinute = travel.getRouteStartTime() - (45*minute); routeStartingMinute < travel.getRouteStartTime() + (45*minute); routeStartingMinute += (15*minute)) {
                improvedTravel = calculateRouteDistance(routePoints, routeStartingMinute);
                if (travel == null) {
                    travel = improvedTravel;
                } else if (improvedTravel.getFailTime() == 0) {
                    if (travel.getFailTime() != 0) {
                        travel = improvedTravel;
                    } else if (improvedTravel.getRouteTime() - improvedTravel.getRouteStartTime() < travel.getRouteTime() - travel.getRouteStartTime()) {
                        travel = improvedTravel;
                    }
                } else if (improvedTravel.getFailTime() < travel.getFailTime()) {
                    travel = improvedTravel;
                } else if (improvedTravel.getFailTime() == travel.getFailTime()) {
                    if (improvedTravel.getRouteTime() - improvedTravel.getRouteStartTime() < travel.getRouteTime() - travel.getRouteStartTime()) {
                        travel = improvedTravel;
                    }
                }
            }
        }

        private void calculateBestHour() {
            Travel improvedTravel;
            for (int routeStartingHour = startTime.getHour(); routeStartingHour < endTime.getHour(); routeStartingHour++) {
                improvedTravel = calculateRouteDistance(routePoints, routeStartingHour * 60 * 60 * 1000);
                if (travel == null) {
                    travel = improvedTravel;
                } else if (improvedTravel.getFailTime() == 0) {
                    if (travel.getFailTime() != 0) {
                        travel = improvedTravel;
                    } else if (improvedTravel.getRouteTime() - improvedTravel.getRouteStartTime() < travel.getRouteTime() - travel.getRouteStartTime()) {
                        travel = improvedTravel;
                    }
                } else if (improvedTravel.getFailTime() < travel.getFailTime()) {
                    travel = improvedTravel;
                } else if (improvedTravel.getFailTime() == travel.getFailTime()) {
                    if (improvedTravel.getRouteTime() - improvedTravel.getRouteStartTime() < travel.getRouteTime() - travel.getRouteStartTime()) {
                        travel = improvedTravel;
                    }
                }
            }
        }
    }

    public static Travel getTravelFromPointToPoint(String lastRoutePoint, String rp, long actualTime,boolean isTest) {
        Travel result = null;
        if (lastRoutePoint != null) {
            result = new Travel(distanceMatrix.get(lastRoutePoint).get(rp),actualTime,isTest);
        } else {
            ArrayList<Travel> travelToPointList = routePointDestinationFromYourLocalization.getTravelToPointList();
            for (Travel t : travelToPointList) {
                if (t.getDestinationPlaceId().equals(rp)) {
                    result = new Travel(t,actualTime,isTest);
                }
            }
        }
        return result;
    }

    public static Travel calculateRouteDistance(ArrayList<RoutePoint> foundRoute, long routeStartingHour) {
        Travel travel = new Travel(0, 0, routeStartingHour, 0);
        travel.setRouteStartTime(routeStartingHour);
        for (int i = -1; i < foundRoute.size() - 1; i++) {
            String fromRoutePointId;
            if (i == -1)
                fromRoutePointId = null;
            else
                fromRoutePointId = foundRoute.get(i).getId();
            RoutePoint toRoutePouint = foundRoute.get(i + 1);
            Travel travelFromPointToPoint = getTravelFromPointToPoint(fromRoutePointId, toRoutePouint.getId(),travel.getRouteTime(),isTest);
            long endTime = Time.convertTimeToLong(toRoutePouint.getEndTime(), isTest);
            long routeTimeAndDuration = travel.getRouteTime() + travelFromPointToPoint.getDuration();
            if (endTime < routeTimeAndDuration) {
                travel.addFailTime(routeTimeAndDuration - endTime);
            }
            long startTime = Time.convertTimeToLong(toRoutePouint.getStartTime(), isTest);
            if (startTime >= routeTimeAndDuration) {
                travel.setRouteTime(Time.convertTimeToLong(toRoutePouint.getStartTime(), isTest));
            } else {
                travel.setRouteTime(travel.getRouteTime() + travelFromPointToPoint.getDuration());
            }

            travel.addDistance(travelFromPointToPoint.getDistance());
            travel.addDuration(travelFromPointToPoint.getDuration());
        }
        if (isTest) {
            Travel travelBackToDepo = getTravelFromPointToPoint(foundRoute.get(foundRoute.size() - 1).getId(), "1.0",travel.getRouteTime(),isTest);
            travel.addDistance(travelBackToDepo.getDistance());
            travel.addDuration(travelBackToDepo.getDuration());
        }
        travel.setRouteTime(travel.getRouteTime() - Time.convertTimeToLong(foundRoute.get(0).getStartTime(), isTest));
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
