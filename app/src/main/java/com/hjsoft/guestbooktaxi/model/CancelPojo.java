package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 29/4/17.
 */
public class CancelPojo {

    @SerializedName("cancelmessage")
    @Expose
    private String cancelmessage;
    @SerializedName("paycash")
    @Expose
    private String paycash;
    @SerializedName("walletamount")
    @Expose
    private String walletamount;

    public String getCancelmessage() {
        return cancelmessage;
    }

    public void setCancelmessage(String cancelmessage) {
        this.cancelmessage = cancelmessage;
    }

    public String getPaycash() {
        return paycash;
    }

    public void setPaycash(String paycash) {
        this.paycash = paycash;
    }

    public String getWalletamount() {
        return walletamount;
    }

    public void setWalletamount(String walletamount) {
        this.walletamount = walletamount;
    }

}