package com.hjsoft.guestbooktaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hjsoft on 17/3/17.
 */
public class HashPojo {

    @SerializedName("Payment_Hash")
    @Expose
    private String paymentHash;
    @SerializedName("IBIBOCodesHash")
    @Expose
    private String iBIBOCodesHash;
    @SerializedName("VAS_FOR_Mobile_SDK")
    @Expose
    private String vASFORMobileSDK;
    @SerializedName("PaymentRelated_Details")
    @Expose
    private String paymentRelatedDetails;
    @SerializedName("Delete_User_Card")
    @Expose
    private String deleteUserCard;
    @SerializedName("Get_User_Card")
    @Expose
    private String getUserCard;
    @SerializedName("Edit_User_Card")
    @Expose
    private String editUserCard;
    @SerializedName("Save_User_Card")
    @Expose
    private String saveUserCard;

    public String getPaymentHash() {
        return paymentHash;
    }

    public void setPaymentHash(String paymentHash) {
        this.paymentHash = paymentHash;
    }

    public String getIBIBOCodesHash() {
        return iBIBOCodesHash;
    }

    public void setIBIBOCodesHash(String iBIBOCodesHash) {
        this.iBIBOCodesHash = iBIBOCodesHash;
    }

    public String getVASFORMobileSDK() {
        return vASFORMobileSDK;
    }

    public void setVASFORMobileSDK(String vASFORMobileSDK) {
        this.vASFORMobileSDK = vASFORMobileSDK;
    }

    public String getPaymentRelatedDetails() {
        return paymentRelatedDetails;
    }

    public void setPaymentRelatedDetails(String paymentRelatedDetails) {
        this.paymentRelatedDetails = paymentRelatedDetails;
    }

    public String getDeleteUserCard() {
        return deleteUserCard;
    }

    public void setDeleteUserCard(String deleteUserCard) {
        this.deleteUserCard = deleteUserCard;
    }

    public String getGetUserCard() {
        return getUserCard;
    }

    public void setGetUserCard(String getUserCard) {
        this.getUserCard = getUserCard;
    }

    public String getEditUserCard() {
        return editUserCard;
    }

    public void setEditUserCard(String editUserCard) {
        this.editUserCard = editUserCard;
    }

    public String getSaveUserCard() {
        return saveUserCard;
    }

    public void setSaveUserCard(String saveUserCard) {
        this.saveUserCard = saveUserCard;
    }

}

