package com.antplay.api;

import com.antplay.models.ChangePassReq;
import com.antplay.models.ChangePasswordResp;
import com.antplay.models.GetVMResponse;
import com.antplay.models.LoginRequestModal;
import com.antplay.models.LoginResponse;
import com.antplay.models.ResetEmailReq;
import com.antplay.models.ResultResponse;
import com.antplay.models.UpdatePinRequestModal;
import com.antplay.models.UpdatePinResponseModal;
import com.antplay.models.UserRegisterRequest;
import com.antplay.models.UserRegisterResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface RetrofitAPI {

    @POST("vmauth/")
    Call<UpdatePinResponseModal> updatePin(@Header("Authorization") String token, @Body UpdatePinRequestModal updatePinRequestModal);

    @GET("getvm/")
    Call<GetVMResponse> getVMFromServer(@Header("Authorization") String token);

    @POST("login/")
    Call<LoginResponse> userLogin(@Body LoginRequestModal loginRequestModal);


    @POST("register/")
    Call<UserRegisterResp> userRegister(@Body UserRegisterRequest userRegisterRequest);

    @POST("request-reset-email/")
    Call<ResultResponse> forgotPassword(@Body ResetEmailReq resetEmailReq);

    @POST("change_password/")
    Call<ChangePasswordResp> changePassword(@Body ChangePassReq changePassReq);

    @POST("userupdate/")
    Call<ChangePasswordResp> userUpdate(@Body UserRegisterRequest userRegisterRequest);





}
