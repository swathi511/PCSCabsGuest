package com.hjsoft.guestbooktaxi.model;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by hjsoft on 28/11/16.
 */
public class CabData implements  Serializable{

    String profileId,latitude,longitude,vehRegNo,phoneNumber,cabCat,driverName,driverPic,dutyPerform;

    public CabData(String profileId,String vehRegNo,String phoneNumber,String cabCat,String latitude,String longitude,String driverName,String driverPic,String dutyPerform)
    {
        this.profileId=profileId;
        this.latitude=latitude;
        this.longitude=longitude;
        this.vehRegNo=vehRegNo;
        this.phoneNumber=phoneNumber;
        this.cabCat=cabCat;
        this.driverName=driverName;
        this.driverPic=driverPic;
        this.dutyPerform=dutyPerform;
    }

    public String getDriverPic() {
        return driverPic;
    }

    public void setDriverPic(String driverPic) {
        this.driverPic = driverPic;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getVehRegNo() {
        return vehRegNo;
    }

    public void setVehRegNo(String vehRegNo) {
        this.vehRegNo = vehRegNo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCabCat() {
        return cabCat;
    }

    public void setCabCat(String cabCat) {
        this.cabCat = cabCat;
    }

    public String getDutyPerform() {
        return dutyPerform;
    }

    public void setDutyPerform(String dutyPerform) {
        this.dutyPerform = dutyPerform;
    }
}
