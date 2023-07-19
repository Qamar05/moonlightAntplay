package com.antplay.models;

//{"id":1177,"transaction_id":"sub_MFdJ0JagpLdDEH","transaction_date":"2023-07-19T10:50:30.068474Z",
//        "isPaid":"created","payment_url":"https://rzp.io/i/Fu6L5ka",
//        "subscriptions":"f503ab9b-58e5-4211-9add-1d11619ad5bb"}

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StartPaymentResp {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("transaction_id")
    @Expose
    private String transaction_id;
    @SerializedName("transaction_date")
    @Expose
    private String transaction_date;
    @SerializedName("isPaid")
    @Expose
    private String isPaid;
    @SerializedName("payment_url")
    @Expose
    private String  payment_url;
    @SerializedName("subscriptions")
    @Expose
    private String subscriptions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }

    public String getPayment_url() {
        return payment_url;
    }

    public void setPayment_url(String payment_url) {
        this.payment_url = payment_url;
    }

    public String getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(String subscriptions) {
        this.subscriptions = subscriptions;
    }
}
