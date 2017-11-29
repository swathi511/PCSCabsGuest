package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 4/1/17.
 */
public class TariffRatePojo {

    @SerializedName("base_fare")
    @Expose
    private String baseFare;
    @SerializedName("O_to_15km")
    @Expose
    private String oTo15km;
    @SerializedName("After15km")
    @Expose
    private String after15km;
    @SerializedName("ridetimecharges")
    @Expose
    private String ridetimecharges;
    @SerializedName("minimumfare")
    @Expose
    private String minimumfare;
    @SerializedName("peaktimecharges")
    @Expose
    private String peaktimecharges;

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getOTo15km() {
        return oTo15km;
    }

    public void setOTo15km(String oTo15km) {
        this.oTo15km = oTo15km;
    }

    public String getAfter15km() {
        return after15km;
    }

    public void setAfter15km(String after15km) {
        this.after15km = after15km;
    }

    public String getRidetimecharges() {
        return ridetimecharges;
    }

    public void setRidetimecharges(String ridetimecharges) {
        this.ridetimecharges = ridetimecharges;
    }

    public String getMinimumfare() {
        return minimumfare;
    }

    public void setMinimumfare(String minimumfare) {
        this.minimumfare = minimumfare;
    }

    public String getPeaktimecharges() {
        return peaktimecharges;
    }

    public void setPeaktimecharges(String peaktimecharges) {
        this.peaktimecharges = peaktimecharges;
    }

}
