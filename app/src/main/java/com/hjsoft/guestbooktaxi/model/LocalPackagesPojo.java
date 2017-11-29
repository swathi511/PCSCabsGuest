package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 26/4/17.
 */
public class LocalPackagesPojo{


    @SerializedName("slabhours")
    @Expose
    private String slabhours;
    @SerializedName("slabkms")
    @Expose
    private String slabkms;
    @SerializedName("slabrate")
    @Expose
    private String slabrate;
    @SerializedName("vehcategory")
    @Expose
    private String vehcategory;
    @SerializedName("tariffname")
    @Expose
    private String tariffname;

    public String getSlabhours() {
        return slabhours;
    }

    public void setSlabhours(String slabhours) {
        this.slabhours = slabhours;
    }

    public String getSlabkms() {
        return slabkms;
    }

    public void setSlabkms(String slabkms) {
        this.slabkms = slabkms;
    }

    public String getSlabrate() {
        return slabrate;
    }

    public void setSlabrate(String slabrate) {
        this.slabrate = slabrate;
    }

    public String getVehcategory() {
        return vehcategory;
    }

    public void setVehcategory(String vehcategory) {
        this.vehcategory = vehcategory;
    }

    public String getTariffname() {
        return tariffname;
    }

    public void setTariffname(String tariffname) {
        this.tariffname = tariffname;
    }

}

