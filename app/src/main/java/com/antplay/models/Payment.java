package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment {

    @SerializedName("payment_id")
    @Expose
    private String paymentId;
    @SerializedName("payment_date")
    @Expose
    private String paymentDate;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("billing_plan")
    @Expose
    private String billingPlan;
    @SerializedName("billing_price")
    @Expose
    private Double billingPrice;

    @SerializedName("expiry_date")
    @Expose
    private String expiry_date;

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBillingPlan() {
        return billingPlan;
    }

    public void setBillingPlan(String billingPlan) {
        this.billingPlan = billingPlan;
    }

    public Double getBillingPrice() {
        return billingPrice;
    }

    public void setBillingPrice(Double billingPrice) {
        this.billingPrice = billingPrice;
    }



}
