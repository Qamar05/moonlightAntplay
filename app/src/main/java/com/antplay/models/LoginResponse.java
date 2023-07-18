package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//{"email":"shobhit.agarwal@vmstechs.com","data":{"refresh":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eX" +
//        "BlIjoicmVmcmVzaCIsImV4cCI6MTY5MDI4NTkwMiwiaWF0IjoxNjg5NjgxMTAyLCJqdGkiOiIxZWY3MzkwZjNhNmQ0NDVmYjI4YjZhY" +
//        "2U4OGEyOWI0ZCIsInVzZXJfaWQiOjIyOTR9.CMBDasOWJu-ciTsLFWUyxYUJS96P-p2z4Va7P9pNczU","access":"eyJ0eXAiOi" +
//        "JKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjg5NzY3NTAyLCJpYXQiOjE2ODk2ODEx" +
//        "MDIsImp0aSI6ImZlOTc0OWIwMjc2ZDQxMWI5MDQ2NWJlNjVhZGU3N2IxIiwidXNlcl9pZCI6MjI5NH0.G5SWMjFw_pIckG_ZQJE" +
//        "2G115c1LsVDfSNzMr6uWv2dc"}}

public class LoginResponse {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("data")
    @Expose
    private LoginData data;



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }
}
