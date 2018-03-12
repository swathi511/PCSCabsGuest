package com.hjsoft.guestbooktaxi.model;

/**
 * Created by hjsoft on 22/2/17.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OutStationPojo {

    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;
    @SerializedName("outsidekmsrate")
    @Expose
    private String outsidekmsrate;
    @SerializedName("basefare")
    @Expose
    private String basefare;
    @SerializedName("slabkm")
    @Expose
    private String slabkm;
    @SerializedName("slabkmrate")
    @Expose
    private String slabkmrate;
    @SerializedName("afterslabkm")
    @Expose
    private String afterslabkm;
    @SerializedName("ridetimepermin")
    @Expose
    private String ridetimepermin;
    @SerializedName("minimumfare")
    @Expose
    private String minimumfare;
    @SerializedName("totalfare")
    @Expose
    private String totalfare;
    @SerializedName("servicetax")
    @Expose
    private String servicetax;
    @SerializedName("min_kms")
    @Expose
    private String minKms;
    @SerializedName("allowed_hrs")
    @Expose
    private String allowedHrs;
    @SerializedName("extra_rate_per_min")
    @Expose
    private String extraRatePerMin;
    @SerializedName("driver_allowance")
    @Expose
    private String driverAllowance;
    @SerializedName("peakhoursdata")
    @Expose
    private String peakhoursdata;


    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getOutsidekmsrate() {
        return outsidekmsrate;
    }

    public void setOutsidekmsrate(String outsidekmsrate) {
        this.outsidekmsrate = outsidekmsrate;
    }

    public String getBasefare() {
        return basefare;
    }

    public void setBasefare(String basefare) {
        this.basefare = basefare;
    }

    public String getSlabkm() {
        return slabkm;
    }

    public void setSlabkm(String slabkm) {
        this.slabkm = slabkm;
    }

    public String getSlabkmrate() {
        return slabkmrate;
    }

    public void setSlabkmrate(String slabkmrate) {
        this.slabkmrate = slabkmrate;
    }

    public String getAfterslabkm() {
        return afterslabkm;
    }

    public void setAfterslabkm(String afterslabkm) {
        this.afterslabkm = afterslabkm;
    }

    public String getRidetimepermin() {
        return ridetimepermin;
    }

    public void setRidetimepermin(String ridetimepermin) {
        this.ridetimepermin = ridetimepermin;
    }

    public String getMinimumfare() {
        return minimumfare;
    }

    public void setMinimumfare(String minimumfare) {
        this.minimumfare = minimumfare;
    }

    public String getTotalfare() {
        return totalfare;
    }

    public void setTotalfare(String totalfare) {
        this.totalfare = totalfare;
    }

    public String getServicetax() {
        return servicetax;
    }

    public void setServicetax(String servicetax) {
        this.servicetax = servicetax;
    }

    public String getMinKms() {
        return minKms;
    }

    public void setMinKms(String minKms) {
        this.minKms = minKms;
    }

    public String getAllowedHrs() {
        return allowedHrs;
    }

    public void setAllowedHrs(String allowedHrs) {
        this.allowedHrs = allowedHrs;
    }

    public String getExtraRatePerMin() {
        return extraRatePerMin;
    }

    public void setExtraRatePerMin(String extraRatePerMin) {
        this.extraRatePerMin = extraRatePerMin;
    }

    public String getDriverAllowance() {
        return driverAllowance;
    }

    public void setDriverAllowance(String driverAllowance) {
        this.driverAllowance = driverAllowance;
    }

    public String getPeakhoursdata() {
        return peakhoursdata;
    }

    public void setPeakhoursdata(String peakhoursdata) {
        this.peakhoursdata = peakhoursdata;
    }

}
