package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 1/5/17.
 */
public class MSeaterPojo {

    @SerializedName("veh_cat")
    @Expose
    private String vehCat;
    @SerializedName("veh_name")
    @Expose
    private String vehName;

    public String getVehCat() {
        return vehCat;
    }

    public void setVehCat(String vehCat) {
        this.vehCat = vehCat;
    }

    public String getVehName() {
        return vehName;
    }

    public void setVehName(String vehName) {
        this.vehName = vehName;
    }
}
