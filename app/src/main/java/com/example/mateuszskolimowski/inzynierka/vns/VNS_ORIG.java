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
import java.util.Collections;
import java.util.Random;

/**
 * Created by Mateusz Skolimowski on 21.05.2017.
 */

public class VNS_ORIG {

   /* private static final int NUMBER_OF_TRIES = 10000;
    private static int K_MAX;
    private static double minDist;
    private static int ilosc;
    private static Route route;
    private static AppCompatActivity appCompatActivity;
    private static ArrayMap<String, ArrayMap<String, Travel>> distFromPointMap;
    private static RoutePointDestination routePointDestinationFromYourLocalization;
    private static ArrayList<RoutePoint> routePoints;
    private static String isRouteFound;

    *//**funkcja inicializujaca algorytm VNS_ORIG.*//*
    private static void initVNS(Route routeArg, AppCompatActivity appCompatActivityArg, RoutePointDestination routePointDestinations,ArrayMap<String, ArrayMap<String, Travel>> distMatrix) {
        route = routeArg;
        appCompatActivity = appCompatActivityArg;
        minDist = 0;
        isRouteFound = null;
        routePointDestinationFromYourLocalization = routePointDestinations;
        if(distMatrix == null) {
            createDistMatrix();
        } else {
            distFromPointMap = distMatrix;//JEZELI TESTUJE Z PLIKU
        }
    }

    private static String checkIfDistMatrixIsComplete() {
        routePoints = route.getRoutePoints();
        for(int i = 0 ; i < routePoints.size() ; i++){
            RoutePoint routePoint = routePoints.get(i);
            String id = routePoint.getId();
            ArrayMap<String, Travel> stringTravelArrayMap = distFromPointMap.get(routePoints.get(i).getId());
            if(stringTravelArrayMap != null) {
                for (int j = 0; j < routePoints.size(); j++) {
                    if (i != j) {
                        if (stringTravelArrayMap.get(routePoints.get(j).getId()) == null){
                            return routePoints.get(j).getId();
                        }
                    }
                }
            } else {
                return routePoints.get(i).getId();
            }
        }
        return null;
    }

    *//**funkcja wyznaczajaca trase algorytmem VNS*//*
    public static String VNS(Route routeArg, AppCompatActivity appCompatActivityArg, RoutePointDestination routePointDestinations,ArrayMap<String, ArrayMap<String, Travel>> distMatrix){
        long time = System.currentTimeMillis();
        initVNS(routeArg,appCompatActivityArg,routePointDestinations,distMatrix);
//        String routePointIdWithoutDirections = checkIfDistMatrixIsComplete();
//        if(routePointIdWithoutDirections != null){
//            return routePointIdWithoutDirections;
//        }
        ArrayList<RoutePoint> routePointArrayList = routeArg.getRoutePoints();
        String s = "";
        for (RoutePoint routePoint : routePointArrayList) {
            s += routePoint.getId() + ";";
        }
        int[] tab = new int[]{1,17,10,20,18,19,11,6,16,2,12,13,7,14,8,3,5,9,21,4,15};
        for(int i = 0 ; i < tab.length ; i++){
            swap(i,tab[i],routePointArrayList);
        }
        s = "";
        for (RoutePoint routePoint : routePointArrayList) {
            s += routePoint.getId() + ";";
        }
        VNS_ORIG.calculateRouteDistance(routePointArrayList);

        Route route = createInitRoute();
        vns(route.getRoutePoints());
        s = "";
        for (RoutePoint routePoint : route.getRoutePoints()) {
            s += routePoint.getId() + ";";
        }
        calculateRouteDistance(route.getRoutePoints());
        Utils.debugLog("kolejnosc punktow : " + s);
        Utils.debugLog("policzona w " + ((System.currentTimeMillis() - time)/1000) + "sek");
        return isRouteFound;
    }

    private static void swap(int index, int val, ArrayList<RoutePoint> routePointArrayList) {
        for(int i = 0 ; i < routePoints.size(); i++){
            if(Integer.valueOf(routePoints.get(i).getId()) == val) {
                Collections.swap(routePointArrayList, index - 1, i);
                return;
            }
        }
    }

    private static void vns(ArrayList<RoutePoint> routePoints) {
        K_MAX = routePoints.size();
        VNSRoute vnsRoute = new VNSRoute(routePoints);
        for(int i = 0 ; i < NUMBER_OF_TRIES ; i ++){
            if(i%100 == 0){
                Utils.debugLog("i = " + i);
            }
            for(int k = 2 ;k < K_MAX; k++){
//                printRoutePoints("before",routePoints);
                routePoints = shake(routePoints,k);
//                printRoutePoints("after",routePoints);
                VNSRoute impovedRoute = improvment(new VNSRoute(routePoints));
//                printRoutePoints("improved",routePoints);
                if(impovedRoute.getTravel() != null){
                    isRouteFound = "found";
                    if(vnsRoute.getTravel() == null || impovedRoute.getTravel().getDistance() < vnsRoute.getTravel().getDistance()){
                        vnsRoute = impovedRoute;
                        break;
                    }
                }
            }
        }
        Utils.debugLog("dist  = " + vnsRoute.getTravel().getDistance());
        route.setRoutePoints(vnsRoute.getRoutePoints());
    }

    private static void printRoutePoints(String before, ArrayList<RoutePoint> routePoints) {
        String s = before;
        for (RoutePoint routePoint : routePoints) {
            s += routePoint.getId() + ";";
        }
        Travel travel = calculateRouteDistance(routePoints);
        if(travel != null) {
            s += " = " + travel.getDistance();
        }
        Utils.debugLog(s);
    }

    private static VNSRoute improvment(VNSRoute vnsRoute) {
        double initDist = 0;
        if(vnsRoute.getTravel() != null) {
            initDist = vnsRoute.getTravel().getDistance();
        }
        for(int i = 0 ; i < vnsRoute.getRoutePoints().size() -1 ; i++){
            Collections.swap(vnsRoute.getRoutePoints(),i,i+1);
            vnsRoute.calculateDistance();
            if(vnsRoute.getTravel() != null && (vnsRoute.getTravel().getDistance() < initDist || initDist == 0)){
                initDist = vnsRoute.getTravel().getDistance();
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

    *//** funkcja konstruujaca trase na podstawie ktorej bedzie dzialal algorytm VNS_ORIG*//*
    private static Route createInitRoute() {
        return createSortedRoute();
//        return createRandomRoute();
    }

    private static Route createSortedRoute() {
        ArrayList<RoutePoint> sortedRoutePoints = route.getRoutePoints();
        for(int i = 0 ; i < sortedRoutePoints.size() -1 ; i++){
            for(int j = 0 ; j < sortedRoutePoints.size() - 1 - i ; j++){
                if(Time.compareTimes(sortedRoutePoints.get(j).getEndTime(),sortedRoutePoints.get(j+1).getEndTime())){
                    Collections.swap(sortedRoutePoints,j,j+1);
                }
            }
        }
        route.setRoutePoints(sortedRoutePoints);
        return route;
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

    *//** funkcja ktora sortuje punkty trase na podstawie czasu zamkniecia okna czasowego dla danego punktu*//*
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

    public static String optimal(Route routeArg, AppCompatActivity appCompatActivityArg, RoutePointDestination routePointDestinations){
        initVNS(routeArg,appCompatActivityArg,routePointDestinations,null);
        ArrayList<RoutePoint> routePointsList = route.getRoutePoints();
        ilosc = silnia(routePointsList.size()+1);
        getOptimalRoute(routePointsList, new ArrayList<RoutePoint>());
        return isRouteFound;
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
        Travel routeTravel = calculateRouteDistance(foundRoute);
        if(routeTravel != null) {
            if (routeTravel.getDistance() < minDist || minDist == 0) {
                fasterRoundFound(routeTravel, foundRoute);
            }
        }
    }

    private static void fasterRoundFound(Travel routeTravel, ArrayList<RoutePoint> foundRoute) {
        minDist = routeTravel.getDistance();
        Utils.debugLog("distance = " + minDist);
        Utils.debugLog("duration = " + routeTravel.getDuration());
        String s = "";
        for (RoutePoint routePoint : foundRoute) {
            s += routePoint.getPlaceName().substring(0, 3) + ";";
        }
        Utils.debugLog("kolejnosc punktow : " + s);
        route.setRoutePoints(foundRoute);
        isRouteFound = "found";
    }

    private static void handleProgress() {
        ilosc --;
        if(ilosc % 100000 == 0){
            Utils.debugLog("zostalo jeszcze " + ilosc + " tras");
        }
    }

    public static Travel calculateRouteDistance(ArrayList<RoutePoint> foundRoute) {
        Travel travel = new Travel(0,0,0);
        for(int i = -1 ; i < foundRoute.size() - 1 ; i++){
            String fromRoutePointId;
            if(i == -1)
                fromRoutePointId = null;
            else
                fromRoutePointId = foundRoute.get(i).getId();
            RoutePoint toRoutePouint = foundRoute.get(i+1);
            Travel travelFromPointToPoint = getTravelFromPointToPoint(fromRoutePointId, toRoutePouint.getId());

            long endTime = Time.convertTimeToLong(toRoutePouint.getEndTime());
            long routeTimeAndDuration = travel.getRouteTime() + travelFromPointToPoint.getDuration();
            if(endTime < routeTimeAndDuration){
                return null;
            } else {
                long startTime = Time.convertTimeToLong(toRoutePouint.getStartTime());
                if(startTime >= routeTimeAndDuration){
                    travel.setRouteTime(Time.convertTimeToLong(toRoutePouint.getStartTime()));
                } else {
                    long t = travel.getRouteTime();
                    long t2 = (long) travelFromPointToPoint.getDistance();//fixme get duration tutaj.
                    long sum = t + t2;
//                    sum += 522;
                    travel.setRouteTime(sum);
                }
            }
            travel.addDistance(travelFromPointToPoint.getDistance());
            travel.addDuration(travelFromPointToPoint.getDuration());
        }
        String first = foundRoute.get(foundRoute.size()-1).getId();
        String second = "0";
        Travel fromEndToDepot = getTravelFromPointToPoint(first,second);//FIXME TYLKO NA TESTY
        travel.addDistance(fromEndToDepot.getDistance());//FIXME TYLKO NA TESTY
        travel.setRouteTime(travel.getRouteTime() - Time.convertTimeToLong(foundRoute.get(0).getStartTime()));
        return travel;
    }

    *//**funkcja tworzaca mape odleglosc z danego punktu do danego punktu.
        mapa przyjmuje id miejsca z ktorego chcemy liczyc odleglosc, id miejsca do ktorego chcemy liczyc odleglosc
        oraz zwraca dystans tej trasy*//*
    private static void createDistMatrix() {
        distFromPointMap = new ArrayMap<>();
        for(RoutePoint routePoint : route.getRoutePoints()){
            ArrayMap<String, Travel> distMap = new ArrayMap<>();
            RoutePointDestination routePointDestinationFromDataBase = Utils.getSQLiteHelper(appCompatActivity).getRoutePointDestinationFromDataBase(routePoint.getId());
            for(Travel travel : routePointDestinationFromDataBase.getTravelToPointList()){
                distMap.put(travel.getDestinationPlaceId(),travel);
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

    *//**funkcja zwracajaca odleglosc z jednego punktu do drugiego*//*
    public static Travel getTravelFromPointToPoint(String lastRoutePoint, String rp) {
        if(lastRoutePoint != null){
           return distFromPointMap.get(lastRoutePoint).get(rp);
        } else {
            ArrayList<Travel> travelToPointList = routePointDestinationFromYourLocalization.getTravelToPointList();
            for(Travel t : travelToPointList){
                if(t.getDestinationPlaceId().equals(rp)){
                    return t;
                }
            }
            return null;
        }
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
    }*/
}
