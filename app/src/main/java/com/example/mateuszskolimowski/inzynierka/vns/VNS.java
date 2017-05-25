package com.example.mateuszskolimowski.inzynierka.vns;

import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;

import com.example.mateuszskolimowski.inzynierka.model.Route;
import com.example.mateuszskolimowski.inzynierka.model.RoutePoint;
import com.example.mateuszskolimowski.inzynierka.model.RoutePointDestination;
import com.example.mateuszskolimowski.inzynierka.model.Time;
import com.example.mateuszskolimowski.inzynierka.model.Travel;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Mateusz Skolimowski on 21.05.2017.
 */

public class VNS {

    private static final int NUMBER_OF_TRIES = 1000;
    private static int K_MAX;
    private static double minDist;
    private static int ilosc;
    private static Route route;
    private static AppCompatActivity appCompatActivity;
    private static ArrayMap<String, ArrayMap<String, Double>> distFromPointMap;

    /**funkcja inicializujaca algorytm VNS.*/
    private static void initVNS(Route routeArg, AppCompatActivity appCompatActivityArg) {
        route = routeArg;
        appCompatActivity = appCompatActivityArg;
        minDist = 0;
        createDistMatrix();
    }

    /**funkcja wyznaczajaca trase algorytmem vns*/
    public static Route VNS(Route routeArg, AppCompatActivity appCompatActivityArg){
        long time = System.currentTimeMillis();
        initVNS(routeArg,appCompatActivityArg);
        Route route = createInitRoute();
        vns(route.getRoutePoints());

        String s = "";
        for (RoutePoint routePoint : route.getRoutePoints()) {
            s += routePoint.getPlaceName().substring(0, 3) + ";";
        }
        Utils.debugLog("kolejnosc punktow : " + s);
        Utils.debugLog("policzona w " + ((System.currentTimeMillis() - time)/1000) + "sek");
        return route;
    }

    private static void vns(ArrayList<RoutePoint> routePoints) {
        K_MAX = routePoints.size();
        VNSRoute vnsRoute = new VNSRoute(routePoints);
        for(int i = 0 ; i < NUMBER_OF_TRIES ; i ++){
            for(int k = 2 ;k < K_MAX; k++){
                routePoints = shake(routePoints,k);
                VNSRoute impovedRoute = improvment(new VNSRoute(routePoints));
                if(impovedRoute.getDistance() < vnsRoute.getDistance()){
                    vnsRoute = impovedRoute;
                    break;
                }
            }
        }
        Utils.debugLog("dist  = " + vnsRoute.getDistance());
        route.setRoutePoints(vnsRoute.getRoutePoints());
    }

    private static VNSRoute improvment(VNSRoute vnsRoute) {
        double initDist = vnsRoute.getDistance();
        for(int i = 0 ; i < vnsRoute.getRoutePoints().size() -1 ; i++){
            Collections.swap(vnsRoute.getRoutePoints(),i,i+1);
            vnsRoute.calculateDistance();
            if(vnsRoute.getDistance() < initDist){
                initDist = vnsRoute.getDistance();
            } else {
                Collections.swap(vnsRoute.getRoutePoints(),i+1,i);
                vnsRoute.calculateDistance();
            }
        }
        return vnsRoute;
    }

    private static ArrayList<RoutePoint> shake(ArrayList<RoutePoint> routePoints, int k) {
       for(int i = 0 ; i < k ; i++){
           Random random = new Random(System.currentTimeMillis());
           int firstToSwap = random.nextInt(routePoints.size());
           int secondToSwap = firstToSwap;
           while(secondToSwap == firstToSwap){
               secondToSwap = random.nextInt(routePoints.size());
           }
           Collections.swap(routePoints,firstToSwap,secondToSwap);
       }
        return routePoints;
    }

    /** funkcja konstruujaca trase na podstawie ktorej bedzie dzialal algorytm VNS*/
    private static Route createInitRoute() {
//        ArrayList<RoutePoint> routePoints = sortRoutePoints(route.getRoutePoints());
//        return null; fixme najpierw trzeba posortowac punkty a pozniej stworzyc trase
        return createRandomRoute();
    }

    private static Route createRandomRoute() {
        Random random = new Random(System.currentTimeMillis());
        ArrayList<Integer> alreadyUsedPoints = new ArrayList<>();
        ArrayList<RoutePoint> randomizedRoutePoints = new ArrayList<>();
        while(alreadyUsedPoints.size() != route.getRoutePoints().size()){
            int rand = random.nextInt(route.getRoutePoints().size());
            if(!alreadyUsedPoints.contains(rand)){
                alreadyUsedPoints.add(rand);
                randomizedRoutePoints.add(route.getRoutePoints().get(rand));
            }
        }
        route.setRoutePoints(randomizedRoutePoints);
        return route;
    }

    /** funkcja ktora sortuje punkty trase na podstawie czasu zamkniecia okna czasowego dla danego punktu*/
    private static ArrayList<RoutePoint> sortRoutePoints(ArrayList<RoutePoint> routePoints) {
        RoutePoint temp = null;
        for(int i = 0 ; i < routePoints.size() ; i++){
            for(int j = 1 ; j < (routePoints.size() - i) ;j++){
                if(isRoutePointTimeHigher(routePoints.get(j-1),routePoints.get(j))){
                    temp = routePoints.get(j-1);
                    routePoints.set(j-1,routePoints.get(j));
                    routePoints.set(j,temp);
                }
            }
        }
        return copyRoutePointsList(routePoints);
    }

    private static boolean isRoutePointTimeHigher(RoutePoint firstRoutePoint, RoutePoint secondRoutePoint) {
        if(firstRoutePoint.getEndTime().getHour() > secondRoutePoint.getEndTime().getHour()){
            return true;
        }
        if(firstRoutePoint.getEndTime().getHour() == secondRoutePoint.getEndTime().getHour()){
            if(firstRoutePoint.getEndTime().getMinute() > secondRoutePoint.getEndTime().getMinute()){
                return true;
            }
        }
        return false;
    }

    public static Route optimal(Route routeArg, AppCompatActivity appCompatActivityArg){
        initVNS(routeArg,appCompatActivityArg);
        ArrayList<RoutePoint> routePoints = route.getRoutePoints();
        ilosc = silnia(routePoints.size());
        getOptimalRoute(routePoints,new ArrayList<RoutePoint>());
        return route;
    }

    private static void getOptimalRoute(ArrayList<RoutePoint> routePointsForIterations, ArrayList<RoutePoint> alreadyVisitedRoutePoints ) {
        for (int i = 0 ; i < routePointsForIterations.size() ; i++){
            //kopiuje juz odwiedzone punkty zeby w kolejnych wykonaniach funckji wiedziec jaka to trasa
            ArrayList<RoutePoint> alreadyVisitedRoutePointsCopy = copyRoutePointsList(alreadyVisitedRoutePoints);
            alreadyVisitedRoutePointsCopy.add(routePointsForIterations.get(i));
            //kopiuje punkty po ktorych iteruje aby w kolejnych wykonaniach funkcji wiedziec po czym iterowac
            ArrayList<RoutePoint> routePointsForIterationsCopy = copyRoutePointsList(routePointsForIterations);
            routePointsForIterationsCopy.remove(routePointsForIterations.get(i));
            if(routePointsForIterationsCopy.size() != 0){
                getOptimalRoute(routePointsForIterationsCopy,alreadyVisitedRoutePointsCopy);
            } else {
                handleRouteFound(alreadyVisitedRoutePointsCopy);
            }
        }
    }

    private static void handleRouteFound(ArrayList<RoutePoint> foundRoute) {
        handleProgress();
        double routeDist = calculateRouteDistance(foundRoute);
        if(routeDist < minDist || minDist == 0){
            fasterRoundFound(routeDist,foundRoute);
        }
    }

    private static void fasterRoundFound(double routeDist, ArrayList<RoutePoint> foundRoute) {
        minDist = routeDist;
        Utils.debugLog("routeDIst = " + minDist);
        String s = "";
        for (RoutePoint routePoint : foundRoute) {
            s += routePoint.getPlaceName().substring(0, 3) + ";";
        }
        Utils.debugLog("kolejnosc punktow : " + s);
        route.setRoutePoints(foundRoute);
    }

    private static void handleProgress() {
        ilosc --;
        if(ilosc % 100000 == 0){
            Utils.debugLog("zostalo jeszcze " + ilosc + " tras");
        }
    }

    private static double calculateRouteDistance(ArrayList<RoutePoint> foundRoute) {
        double dist = 0;
        for(int i = 0 ; i < foundRoute.size() - 1 ; i++){
            dist += distance(foundRoute.get(i).getId(), foundRoute.get(i+1).getId());
        }
        return dist;
    }

    /**funkcja tworzaca mape odleglosc z danego punktu do danego punktu.
        mapa przyjmuje id miejsca z ktorego chcemy liczyc odleglosc, id miejsca do ktorego chcemy liczyc odleglosc
        oraz zwraca dystans tej trasy*/
    private static void createDistMatrix() {
        distFromPointMap = new ArrayMap<>();
        for(RoutePoint routePoint : route.getRoutePoints()){
            ArrayMap<String, Double> distMap = new ArrayMap<>();
            RoutePointDestination routePointDestinationFromDataBase = Utils.getSQLiteHelper(appCompatActivity).getRoutePointDestinationFromDataBase(routePoint.getId());
            for(Travel travel : routePointDestinationFromDataBase.getTravelToPointList()){
                distMap.put(travel.getDestinationPlaceId(),travel.getDistance());
            }
            distFromPointMap.put(routePoint.getId(),distMap);
        }
    }

    private static int silnia(int size) {
        int silnia = 1;
        for(int i = 1 ; i < size ; i++){
            silnia *= i + 1;
        }
        return silnia;
    }

    private static ArrayList<RoutePoint> copyRoutePointsList(ArrayList<RoutePoint> lastRoutePointsId) {
        ArrayList<RoutePoint> resultList = new ArrayList<>();
        for(RoutePoint id : lastRoutePointsId){
            resultList.add(id);
        }
        return resultList;
    }

    /**funkcja zwracajaca odleglosc z jednego punktu do drugiego*/
    public static double distance(String lastRoutePoint, String rp) {
        if(lastRoutePoint != null){
           return distFromPointMap.get(lastRoutePoint).get(rp);
        } else {
            return 100.0; // fixme odleglosc od miejsca pobytu do danego punktu
        }
    }

    private static class VNSRoute {
        private ArrayList<RoutePoint> routePoints;
        private double distance;

        public VNSRoute(ArrayList<RoutePoint> routePoints) {
            this.routePoints = new ArrayList<>(routePoints);
            this.distance = calculateRouteDistance(routePoints);
        }

        public void setRoutePoints(ArrayList<RoutePoint> routePoints) {
            this.routePoints = routePoints;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public ArrayList<RoutePoint> getRoutePoints() {
            return routePoints;
        }

        public double getDistance() {
            return distance;
        }

        public double calculateDistance() {
            this.distance = calculateRouteDistance(routePoints);
            return distance;
        }
    }
}
