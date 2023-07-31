package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//{"id":1177,"transaction_id":"sub_MFdJ0JagpLdDEH","transaction_date":"2023-07-19T10:50:30.068474Z",
//        "isPaid":"created","payment_url":"https://rzp.io/i/Fu6L5ka",
//        "subscriptions":"f503ab9b-58e5-4211-9add-1d11619ad5bb"}

public class StartPaymentReq {
    @SerializedName("billingplan_id")
    @Expose
    private int billingplan_id;
    @SerializedName("user_type")
    @Expose
    private String user_type;


    public  StartPaymentReq(int billingplan_id , String user_type){
        this.billingplan_id  =  billingplan_id;
        this.user_type =  user_type;

    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }



    public int getBillingplan_id() {
        return billingplan_id;
    }

    public void setBillingplan_id(int billingplan_id) {
        this.billingplan_id = billingplan_id;
    }
}
