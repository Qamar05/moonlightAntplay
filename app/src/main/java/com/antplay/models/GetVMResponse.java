
package com.antplay.models;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// "message": "success",
//"data": [
//        {
//        "id": 115,
//        "vmid": 501,
//        "node": 7,
//        "vmname": "shobhit",
//        "time_remaining": 72000,
//        "is_connected": false,
//        "start_vm_call_count": 0,
//        "vmip": "103.182.65.183",
//        "status": "running"
//        }
//      ]

public class GetVMResponse {

    @SerializedName("data")
    @Expose
    private List<VMInfo> mData;
    @SerializedName("message")
    @Expose
    private String mMessage;

    public List<VMInfo> getData() {
        return mData;
    }

    public void setData(List<VMInfo> data) {
        mData = data;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

}
