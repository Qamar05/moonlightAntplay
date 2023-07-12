
package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VMInfo {

    @SerializedName("vmip")
    @Expose
    private String mVmIp;

    public String getVmIp() {
        return mVmIp;
    }

    public void setVmIp(String vmIp) {
        mVmIp = vmIp;
    }

}
