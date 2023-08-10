package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePassReq {
    @SerializedName("old_password")
    @Expose
    private String old_password;
    @SerializedName("new_password")
    @Expose
    private String new_password;
    @SerializedName("confirm_password")
    @Expose
    private String confirm_password;

    public ChangePassReq(String old_password, String new_password , String confirm_password){
        this.old_password =  old_password;
        this.new_password  =  new_password;
        this.confirm_password  =  confirm_password;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }
}
