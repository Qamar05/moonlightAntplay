package com.antplay.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//{"message":"success","data":[{"id":1,"plan_type":"Antplay","gpu":"GDDR5, 6GB vGPU","cpu":8,"ram":12,"ssd":400,
//        "plan_name":"LIGHT","term":1,"hour_limit":2,"price":1.0}]}
public class AllBillingPlanResp {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<BillingDataList> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BillingDataList> getData() {
        return data;
    }

    public void setData(List<BillingDataList> data) {
        this.data = data;
    }
}
