package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//"data":[{"id":1,"plan_type":"Antplay","gpu":"GDDR5, 6GB vGPU","cpu":8,"ram":12,"ssd":400,
////        "plan_name":"LIGHT","term":1,"hour_limit":2,"price":1.0}]}
public class BillingDataList {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("plan_type")
    @Expose
    private String plan_type;
    @SerializedName("plan_name")
    @Expose
    private String plan_name;
    @SerializedName("gpu")
    @Expose
    private String gpu;

    @SerializedName("cpu")
    @Expose
    private String cpu;
    @SerializedName("ram")
    @Expose
    private String ram;
    @SerializedName("ssd")
    @Expose
    private String ssd;
    @SerializedName("term")
    @Expose
    private String term;

    @SerializedName("hour_limit")
    @Expose
    private String hour_limit;
    @SerializedName("price")
    @Expose
    private String price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlan_type() {
        return plan_type;
    }

    public void setPlan_type(String plan_type) {
        this.plan_type = plan_type;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getGpu() {
        return gpu;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getSsd() {
        return ssd;
    }

    public void setSsd(String ssd) {
        this.ssd = ssd;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getHour_limit() {
        return hour_limit;
    }

    public void setHour_limit(String hour_limit) {
        this.hour_limit = hour_limit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
