package com.hjsoft.guestbooktaxi.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hjsoft on 7/1/17.
 */
public class FormattedAllRidesData implements Serializable{

    String requestId,fromLocation,toLocation,vehicleCategory,vehicleType,distanceTravelled;
    String rideStatus,rideStartTime,rideStopTime,totalAmount,driverName,travelType,bookingType,travelPackage,driverMobile,osBatta;
    Date rideDate;
    String driverPic,paymentMode,otherCharges,driverProfileId;

    public FormattedAllRidesData(Date rideDate, String requestId,String fromLocation,String toLocation,String vehicleCategory,String vehicleType,String distanceTravelled,
                                 String rideStatus,String rideStartTime,String rideStopTime,String totalAmount,String driverName,String driverPic,String travelType,String bookingType,String travelPackage,String driverMobile,String osBatta,String paymentMode,String otherCharges,
                                 String driverProfileId)
    {
        this.rideDate=rideDate;
        this.requestId=requestId;
        this.fromLocation=fromLocation;
        this.toLocation=toLocation;
        this.vehicleCategory=vehicleCategory;
        this.vehicleType=vehicleType;
        this.distanceTravelled=distanceTravelled;
        this.rideStatus=rideStatus;
        this.rideStartTime=rideStartTime;
        this.rideStopTime=rideStopTime;
        this.totalAmount=totalAmount;
        this.driverName=driverName;
        this.driverPic=driverPic;
        this.travelType=travelType;
        this.bookingType=bookingType;
        this.travelPackage=travelPackage;
        this.driverMobile=driverMobile;
        this.osBatta=osBatta;
        this.paymentMode=paymentMode;
        this.otherCharges=otherCharges;
        this.driverProfileId=driverProfileId;
    }

    public String getDriverPic() {
        return driverPic;
    }

    public void setDriverPic(String driverPic) {
        this.driverPic = driverPic;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
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

    public String getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(String distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getRideStartTime() {
        return rideStartTime;
    }

    public void setRideStartTime(String rideStartTime) {
        this.rideStartTime = rideStartTime;
    }

    public String getRideStopTime() {
        return rideStopTime;
    }

    public void setRideStopTime(String rideStopTime) {
        this.rideStopTime = rideStopTime;
    }

    public Date getRideDate() {
        return rideDate;
    }

    public void setRideDate(Date rideDate) {
        this.rideDate = rideDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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

    public String getTravelPackage() {
        return travelPackage;
    }

    public void setTravelPackage(String travelPackage) {
        this.travelPackage = travelPackage;
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        this.driverMobile = driverMobile;
    }

    public String getOsBatta() {
        return osBatta;
    }

    public void setOsBatta(String osBatta) {
        this.osBatta = osBatta;
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


