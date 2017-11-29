package com.hjsoft.guestbooktaxi.model;

/**
 * Created by hjsoft on 22/12/16.
 */
public class UserRidesData {

    String tripId,pickup,drop,rideDate,rideStatus;

    public UserRidesData(String tripId,String pickup,String drop,String rideDate,String rideStatus)
    {
        this.tripId=tripId;
        this.pickup=pickup;
        this.drop=drop;
        this.rideDate=rideDate;
        this.rideStatus=rideStatus;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getRideDate() {
        return rideDate;
    }

    public void setRideDate(String rideDate) {
        this.rideDate = rideDate;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}
