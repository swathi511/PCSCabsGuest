package com.hjsoft.guestbooktaxi.model;

/**
 * Created by hjsoft on 16/11/16.
 */
public class LocationData {

    String veh_id,place,time_updated,marker,driver_name,driver_number;
    double lat,lng;

    public LocationData(String veh_id,double lat,double lng,String place,String time_updated,String marker,String driver_name,String driver_number)
    {
        this.veh_id=veh_id;
        this.lat=lat;
        this.lng=lng;
        this.place=place;
        this.time_updated=time_updated;
        this.marker=marker;
        this.driver_name=driver_name;
        this.driver_number=driver_number;
    }

    public String getVeh_id() {
        return veh_id;
    }

    public void setVeh_id(String veh_id) {
        this.veh_id = veh_id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime_updated() {
        return time_updated;
    }

    public void setTime_updated(String time_updated) {
        this.time_updated = time_updated;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_number() {
        return driver_number;
    }

    public void setDriver_number(String driver_number) {
        this.driver_number = driver_number;
    }
}
