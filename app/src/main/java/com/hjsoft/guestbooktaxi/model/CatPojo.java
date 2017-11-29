package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 25/11/16.
 */
public class CatPojo {

    @SerializedName("vehcatgoryid")
    @Expose
    private String vehcatgoryid;
    @SerializedName("vehcategory")
    @Expose
    private String vehcategory;

    /**
     *
     * @return
     * The vehcatgoryid
     */
    public String getVehcatgoryid() {
        return vehcatgoryid;
    }

    /**
     *
     * @param vehcatgoryid
     * The vehcatgoryid
     */
    public void setVehcatgoryid(String vehcatgoryid) {
        this.vehcatgoryid = vehcatgoryid;
    }

    /**
     *
     * @return
     * The vehcategory
     */
    public String getVehcategory() {
        return vehcategory;
    }

    /**
     *
     * @param vehcategory
     * The vehcategory
     */
    public void setVehcategory(String vehcategory) {
        this.vehcategory = vehcategory;
    }

}
