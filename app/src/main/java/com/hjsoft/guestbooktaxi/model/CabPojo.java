package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 28/11/16.
 */
public class CabPojo {

    @SerializedName("Profileid")
    @Expose
    private String profileid;
    @SerializedName("vehichleregno")
    @Expose
    private String vehichleregno;
    @SerializedName("phonenumber")
    @Expose
    private String phonenumber;
    @SerializedName("vehicle_category")
    @Expose
    private String vehicleCategory;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latittude")
    @Expose
    private String latitude;
    @SerializedName("drivername")
    @Expose
    private String driverName;
    @SerializedName("driverpic")
    @Expose
    private String driverPic;
    @SerializedName("dutyperform")
    @Expose
    private String dutyPerform;

    public String getDriverPic() {
        return driverPic;
    }

    public void setDriverPic(String driverPic) {
        this.driverPic = driverPic;
    }

    /**
     *
     * @return
     * The profileid
     */
    public String getProfileid() {
        return profileid;
    }

    /**
     *
     * @param profileid
     * The Profileid
     */
    public void setProfileid(String profileid) {
        this.profileid = profileid;
    }

    /**
     *
     * @return
     * The vehichleregno
     */
    public String getVehichleregno() {
        return vehichleregno;
    }

    /**
     *
     * @param vehichleregno
     * The vehichleregno
     */
    public void setVehichleregno(String vehichleregno) {
        this.vehichleregno = vehichleregno;
    }

    /**
     *
     * @return
     * The phonenumber
     */
    public String getPhonenumber() {
        return phonenumber;
    }

    /**
     *
     * @param phonenumber
     * The phonenumber
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     *
     * @return
     * The vehicleCategory
     */
    public String getVehicleCategory() {
        return vehicleCategory;
    }

    /**
     *
     * @param vehicleCategory
     * The vehicle_category
     */
    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    /**
     *
     * @return
     * The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return
     * The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDutyPerform() {
        return dutyPerform;
    }

    public void setDutyPerform(String dutyPerform) {
        this.dutyPerform = dutyPerform;
    }
}
