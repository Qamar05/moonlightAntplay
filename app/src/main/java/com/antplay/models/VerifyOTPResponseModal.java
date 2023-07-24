package com.antplay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//{"success":"True","message":"OTP Verification success",
//        "data":{"refresh":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2t" +
//        "lbl90eXBlIjoicmVmcmVzaCIsImV4cCI6MTY5MDc5Nzk4MiwiaWF0IjoxNjkwMTkzMTgy" +
//        "LCJqdGkiOiI3NzNiYjIzZjA4ZmQ0NTYxODVlM2ZlYTUxODNjZWMxYyIsInVzZXJfaWQiOjEw" +
//        "OTB9.C877KJXVHm-5xlcx3KUgIl8ODKi8kamALCDFgmdlV5o",
//        "access":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2t" +
//        "lbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjkwMjc5NTgyLCJpYXQiOjE2OTAxOT" +
//        "MxODIsImp0aSI6IjAyN2RlM2Y3NjljMjQ1MWM4MzMwZDkzNTE1Y2NkMTQxIiwidXNlcl9pZC" +
//        "I6MTA5MH0.Yvj1X2p6DyBDsgEnq0NZqzvGktJ8IUEsljQv5XRTKYY"}}
public class VerifyOTPResponseModal {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("refresh")
        @Expose
        private String refresh;
        @SerializedName("access")
        @Expose
        private String access;

        public String getRefresh() {
            return refresh;
        }

        public void setRefresh(String refresh) {
            this.refresh = refresh;
        }

        public String getAccess() {
            return access;
        }

        public void setAccess(String access) {
            this.access = access;
        }
    }
}
