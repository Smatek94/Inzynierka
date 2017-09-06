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
    private static int ALGORITHM_WORKING_TIME;
    private static ArrayMap<String, ArrayMap<String, Travel>> distanceMatrix;
    private static RoutePointDestination routePointDestinationFromYourLocalization;
    private static String isRouteFound;
    private static boolean isTest;

    public static VNSRoute VNS(Route routeArg,
                               RoutePointDestination routePointDestinations,
                               ArrayMap<String, ArrayMap<String, Travel>> distMatrix, boolean isTest, Context context) {
        VNS.isTest = isTest;
        ALGORITHM_WORKING_TIME = SharedPreferencesUtils.getAlgorithmWorkingTime(context) * 1000 + 1000;
        return VNS(routeArg, routePointDestinations, distMatrix);
    }

    public static VNSRoute VNS(Route routeArg,
                               RoutePointDestination routePointDestinations,
                               ArrayMap<String, ArrayMap<String, Travel>> distMatrix) {
        distanceMatrix = distMatrix;
        routePointDestinationFromYourLocalization = routePointDestinations;
        ArrayList<RoutePoint> routePoints = createStartRoute(routeArg.getRoutePoints());
        VNSRoute vnsRoute = new VNSRoute(routePoints, routeArg.getStartTime(), routeArg.getEndTime());
        long time = System.currentTimeMillis();
        int k = 1;
        while (System.currentTimeMillis() - time < ALGORITHM_WORKING_TIME) {
            if (k == routePoints.size()) {
                k = 1;
            }
            ArrayList<RoutePoint> tempRoutePoints = shake(routePoints, k);
            VNSRoute impovedRoute = improvment(new VNSRoute(tempRoutePoints, routeArg.getStartTime(), routeArg.getEndTime()));
            if (impovedRoute.getTravel() != null) {
                isRouteFound = "found";
                if (vnsRoute.getTravel() == null || impovedRoute.getTravel().getFailTime() == 0) {
                    if (impovedRoute.getTravel().getDistance() < vnsRoute.getTravel().getDistance()) {
                        vnsRoute = impovedRoute;
                        routePoints = tempRoutePoints;
                        k = 0;
                    }
                } else if (impovedRoute.getTravel().getFailTime() < vnsRoute.getTravel().getFailTime()) {
                    vnsRoute = impovedRoute;
                    routePoints = tempRoutePoints;
                    k = 0;
                }
            }
            k++;
        }
        if (vnsRoute.getTravel() != null) {
            Utils.debugLog("dist  = " + vnsRoute.getTravel().getDistance());
            String s = "";
            for (RoutePoint routePoint : vnsRoute.getRoutePoints()) {
//                s += routePoint.getId().substring(0, routePoint.getId().length() - 2) + ";";
                s += routePoint.getId() + ";";
            }
//            Utils.debugLog(s);
            routeArg.setRoutePoints(vnsRoute.getRoutePoints());
        }
        return vnsRoute;
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
        return opt2(vnsRoute);
//        return opt3(vnsRoute);
    }

    private static VNSRoute opt2(VNSRoute vnsRoute) {
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

    private static VNSRoute opt3(VNSRoute vnsRoute) {
        double initDist = 0;
        if (vnsRoute.getTravel() != null) {
            initDist = vnsRoute.getTravel().getDistance();
        }
        for (int i = 0; i < vnsRoute.getRoutePoints().size() - 2; i++) {
            Collections.swap(vnsRoute.getRoutePoints(), i + 1, i + 2);
            vnsRoute.calculateDistance();
            double OneThreeTwo = vnsRoute.getTravel().getDistance();
            Collections.swap(vnsRoute.getRoutePoints(), i, i + 2);
            vnsRoute.calculateDistance();
            double TwoThreeOne = vnsRoute.getTravel().getDistance();
            Collections.swap(vnsRoute.getRoutePoints(), i + 1, i + 2);
            vnsRoute.calculateDistance();
            double TwoOneThree = vnsRoute.getTravel().getDistance();
            Collections.swap(vnsRoute.getRoutePoints(), i, i + 2);
            vnsRoute.calculateDistance();
            double ThreeOneTwo = vnsRoute.getTravel().getDistance();
            Collections.swap(vnsRoute.getRoutePoints(), i + 1, i + 2);
            vnsRoute.calculateDistance();
            double ThreeTwoOne = vnsRoute.getTravel().getDistance();
            Collections.swap(vnsRoute.getRoutePoints(), i, i + 2);
            ArrayList<Double> configurationList = new ArrayList<>();
            configurationList.add(initDist);
            configurationList.add(OneThreeTwo);
            configurationList.add(TwoThreeOne);
            configurationList.add(TwoOneThree);
            configurationList.add(ThreeOneTwo);
            configurationList.add(ThreeTwoOne);
            if(lowest(initDist,configurationList)){
                break;
            }
            if(lowest(OneThreeTwo,configurationList)){
                Collections.swap(vnsRoute.getRoutePoints(), i + 1, i + 2);
                break;
            }
            if(lowest(TwoThreeOne,configurationList)){
                Collections.swap(vnsRoute.getRoutePoints(), i + 1, i + 2);
                Collections.swap(vnsRoute.getRoutePoints(), i, i + 2);
                break;
            }
            if(lowest(TwoOneThree,configurationList)){
                Collections.swap(vnsRoute.getRoutePoints(), i, i + 1);
                break;
            }
            if(lowest(ThreeOneTwo,configurationList)){
                Collections.swap(vnsRoute.getRoutePoints(), i + 1, i + 2);
                Collections.swap(vnsRoute.getRoutePoints(), i, i + 1);
                break;
            }
            if(lowest(ThreeTwoOne,configurationList)){
                Collections.swap(vnsRoute.getRoutePoints(), i, i + 2);
                break;
            }
        }
        return vnsRoute;
    }

    private static boolean lowest(double initDist, ArrayList<Double> configurationList) {
        for(Double config : configurationList){
            if(initDist > config){
                return false;
            }
        }
        return true;
    }

    public static class VNSRoute {
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
            if (!isTest) {
                calculateDistanceForDifferentTimeWindows();
            } else {
                travel = calculateRouteDistance(routePoints, 0);
            }
            return travel;
        }

        private void calculateDistanceForDifferentTimeWindows() {
            calculateBestHour();
            calculateBestMinute();
        }

        private void calculateBestMinute() {
            Travel improvedTravel;
            long minute = 60 * 1000;
            for (long routeStartingMinute = travel.getRouteStartTime() - (45 * minute); routeStartingMinute < travel.getRouteStartTime() + (45 * minute); routeStartingMinute += (15 * minute)) {
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
//            for (int routeStartingHour = startTime.getHour(); routeStartingHour < endTime.getHour(); routeStartingHour++) {
            for (int routeStartingHour = 0; routeStartingHour < endTime.getHour(); routeStartingHour++) {
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

    public static Travel getTravelFromPointToPoint(String lastRoutePoint, String rp, long actualTime, boolean isTest) {
        Travel result = null;
        if (lastRoutePoint != null) {
            if(distanceMatrix.get(lastRoutePoint).get(rp) != null)
                result = new Travel(distanceMatrix.get(lastRoutePoint).get(rp), actualTime, isTest);
            else
                result = new Travel(999999999,9999999,"");
        } else {
            ArrayList<Travel> travelToPointList = routePointDestinationFromYourLocalization.getTravelToPointList();
            for (Travel t : travelToPointList) {
                if (t.getDestinationPlaceId().equals(rp)) {
                    result = new Travel(t, actualTime, isTest);
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
            Travel travelFromPointToPoint = getTravelFromPointToPoint(fromRoutePointId, toRoutePouint.getId(), travel.getRouteTime(), isTest);
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
            Travel travelBackToDepo = getTravelFromPointToPoint(foundRoute.get(foundRoute.size() - 1).getId(), "0", travel.getRouteTime(), isTest);
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
