package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
//{"status":true,"message":"Email successfully sent to your registered email address"}
public class ResultResponse {
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
