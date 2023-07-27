package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VMTimerReq {
    @SerializedName("vmid")
    @Expose
    private String vmid;


    public VMTimerReq(String vmid) {
        this.vmid = vmid;
    }

    public String getVmid() {
        return vmid;
    }

    public void setVmid(String vmid) {
        this.vmid = vmid;
    }
}
