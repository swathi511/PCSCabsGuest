package com.hjsoft.guestbooktaxi.model;

/**
 * Created by hjsoft on 3/12/16.
 */
public class PlaceData {

    double latitude,longitude;
    String place;

    public PlaceData(double latitude,double longitude,String place)
    {
        this.latitude=latitude;
        this.longitude=longitude;
        this.place=place;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
