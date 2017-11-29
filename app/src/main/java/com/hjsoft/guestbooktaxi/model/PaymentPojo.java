package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 16/3/17.
 */
public class PaymentPojo {

    @SerializedName("TotalwalletAmount")
    @Expose
    private String totalwalletAmount;
    @SerializedName("status")
    @Expose
    private String status;

    public String getTotalwalletAmount() {
        return totalwalletAmount;
    }

    public void setTotalwalletAmount(String totalwalletAmount) {
        this.totalwalletAmount = totalwalletAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
