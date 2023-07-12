
package com.antplay.models;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


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
