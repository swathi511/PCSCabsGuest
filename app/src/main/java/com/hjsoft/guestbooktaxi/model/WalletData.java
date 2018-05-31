package com.hjsoft.guestbooktaxi.model;

/**
 * Created by hjsoft on 3/2/18.
 */
public class WalletData {

    String amount,date,desc;

    public WalletData(String amount,String date,String desc)
    {
        this.amount=amount;
        this.date=date;
        this.desc=desc;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
