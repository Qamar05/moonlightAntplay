package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdatePinResponseModal {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private PinResponseData pinData;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PinResponseData getPinData() {
        return pinData;
    }

    public void setPinData(PinResponseData pinData) {
        this.pinData = pinData;
    }
}
