package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 4/1/17.
 */
public class CabArrivalTimePojo {

    @SerializedName("vehicle_category")
    @Expose
    private String vehicleCategory;
    @SerializedName("time")
    @Expose
    private String time;

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}