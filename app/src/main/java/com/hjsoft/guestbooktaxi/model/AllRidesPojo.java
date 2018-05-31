package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hjsoft on 7/1/17.
 */
public class AllRidesPojo implements Serializable {

    @SerializedName("requestid")
    @Expose
    private String requestid;
    @SerializedName("ridedate")
    @Expose
    private String ridedate;
    @SerializedName("fromlocation")
    @Expose
    private String fromlocation;
    @SerializedName("tolocation")
    @Expose
    private String tolocation;
    @SerializedName("vehicle_category")
    @Expose
    private String vehicleCategory;
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;
    @SerializedName("distancetravelled")
    @Expose
    private String distancetravelled;
    @SerializedName("statusofride")
    @Expose
    private String statusofride;
    @SerializedName("ridestarttime")
    @Expose
    private String ridestarttime;
    @SerializedName("ridestoptime")
    @Expose
    private String ridestoptime;
    @SerializedName("totalamount")
    @Expose
    private String totalamount;
    @SerializedName("drivername")
    @Expose
    private String drivername;
    @SerializedName("driverpic")
    @Expose
    private String driverpic;
    @SerializedName("travelType")
    @Expose
    private String travelType;
    @SerializedName("bookingType")
    @Expose
    private String bookingType;
    @SerializedName("travelpackage")
    @Expose
    private String travelpackage;
    @SerializedName("drivermobile")
    @Expose
    private String drivermobile;
    @SerializedName("DriverBattaAmt")
    @Expose
    private String driverBattaAmt;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("othercharges")
    @Expose
    private String otherCharges;
    @SerializedName("driverProfileId")
    @Expose
    private String driverProfileId;


    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public String getRidedate() {
        return ridedate;
    }

    public void setRidedate(String ridedate) {
        this.ridedate = ridedate;
    }

    public String getFromlocation() {
        return fromlocation;
    }

    public void setFromlocation(String fromlocation) {
        this.fromlocation = fromlocation;
    }

    public String getTolocation() {
        return tolocation;
    }

    public void setTolocation(String tolocation) {
        this.tolocation = tolocation;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDistancetravelled() {
        return distancetravelled;
    }

    public void setDistancetravelled(String distancetravelled) {
        this.distancetravelled = distancetravelled;
    }

    public String getStatusofride() {
        return statusofride;
    }

    public void setStatusofride(String statusofride) {
        this.statusofride = statusofride;
    }

    public String getRidestarttime() {
        return ridestarttime;
    }

    public void setRidestarttime(String ridestarttime) {
        this.ridestarttime = ridestarttime;
    }

    public String getRidestoptime() {
        return ridestoptime;
    }

    public void setRidestoptime(String ridestoptime) {
        this.ridestoptime = ridestoptime;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getDriverpic() {
        return driverpic;
    }

    public void setDriverpic(String driverpic) {
        this.driverpic = driverpic;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getTravelpackage() {
        return travelpackage;
    }

    public void setTravelpackage(String travelpackage) {
        this.travelpackage = travelpackage;
    }

    public String getDrivermobile() {
        return drivermobile;
    }

    public void setDrivermobile(String drivermobile) {
        this.drivermobile = drivermobile;
    }

    public String getDriverBattaAmt() {
        return driverBattaAmt;
    }

    public void setDriverBattaAmt(String driverBattaAmt) {
        this.driverBattaAmt = driverBattaAmt;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(String otherCharges) {
        this.otherCharges = otherCharges;
    }

    public String getDriverProfileId() {
        return driverProfileId;
    }

    public void setDriverProfileId(String driverProfileId) {
        this.driverProfileId = driverProfileId;
    }
}


