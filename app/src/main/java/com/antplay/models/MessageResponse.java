package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//{"success":"True","message":"Your vm time End"}
public class MessageResponse {
    @SerializedName("success")
    @Expose
    private String  success;
    @SerializedName("message")
    @Expose
    private String message;


    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
