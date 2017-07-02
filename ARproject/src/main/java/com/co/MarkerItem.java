package com.co;

/**
 * Created by hx2ryu on 2017-06-01.
 */

public class MarkerItem {
    String name;
    double lat, lng, dist;

    public MarkerItem(double lat, double lng, String name, double dist) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.dist = dist;
    }

    public String getName() {
        return name;
    }

    public double getDist() {
        return dist;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
