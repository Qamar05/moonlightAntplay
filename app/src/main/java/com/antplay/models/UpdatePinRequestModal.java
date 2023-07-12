package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdatePinRequestModal {
    @SerializedName("pin")
    @Expose
    private String pin;

    public UpdatePinRequestModal(String pin) {
        this.pin = pin;
    }

}
