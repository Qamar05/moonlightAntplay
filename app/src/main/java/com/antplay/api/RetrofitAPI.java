package com.antplay.api;

import com.antplay.models.AllBillingPlanResp;
import com.antplay.models.ChangePassReq;
import com.antplay.models.ChangePasswordResp;
import com.antplay.models.GetVMResponse;
import com.antplay.models.LoginRequestModal;
import com.antplay.models.MessageResponse;
import com.antplay.models.PaymentHistory_modal;
import com.antplay.models.ResetEmailReq;
import com.antplay.models.ResultResponse;
import com.antplay.models.SendOTPResponse;
import com.antplay.models.StartPaymentReq;
import com.antplay.models.StartPaymentResp;
import com.antplay.models.UpdatePinRequestModal;
import com.antplay.models.UpdatePinResponseModal;
import com.antplay.models.UserDetailsModal;
import com.antplay.models.UserRegisterRequest;
import com.antplay.models.UserRegisterResp;
import com.antplay.models.UserUpdateRequestModal;
import com.antplay.models.UserUpdateResponseModal;
import com.antplay.models.UserViewResponse;
import com.antplay.models.VMTimerReq;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface RetrofitAPI {
    @GET("getbillingplan")
    Call<AllBillingPlanResp> getBillingPlan(@Header("Authorization") String token , @Query("user_type") String userType);
    @POST("vmauth/")
    Call<UpdatePinResponseModal> updatePin(@Header("Authorization") String token, @Body UpdatePinRequestModal updatePinRequestModal);
    @GET("getvm")
    Call<ResponseBody> getVMFromServer(@Header("Authorization") String token);
    @POST("login/")
    Call<ResponseBody> userLogin(@Body LoginRequestModal loginRequestModal);
    @POST("register/")
    Call<UserRegisterResp> userRegister(@Body UserRegisterRequest userRegisterRequest);
    @POST("request-reset-email/")
    Call<ResultResponse> forgotPassword(@Body ResetEmailReq resetEmailReq);
    @PUT("change_password")
    Call<ChangePasswordResp> changePassword(@Header("Authorization") String token , @Body ChangePassReq changePassReq);
    @GET("getpaymenthistory")
    Call<PaymentHistory_modal> getPaymentHistory(@Header("Authorization") String Token);
    @POST("start_payment/")
    Call<StartPaymentResp> startPayment(@Header("Authorization") String token , @Body StartPaymentReq startPaymentReq);
    @PUT("userupdate/")
    Call<UserUpdateResponseModal> userUpdate(@Header("Authorization") String Token, @Body UserUpdateRequestModal userUpdateRequestModal);
    @GET("userview")
    Call<UserDetailsModal> getUserDetails(@Header("Authorization") String Token);
    @GET
    Call<SendOTPResponse> sendOTP(@Url String url);
    @POST("verifyOTPView/")
    Call<ResponseBody> verifyOTP(@Query("phone_number") String phone, @Query("otp") String otp);
    @POST("startvmtime/")
    Call<MessageResponse> startVmTime(@Header("Authorization") String token, @Body VMTimerReq vmTimerReq);
    @POST("endvmtime/")
    Call<MessageResponse> endVmTime(@Header("Authorization") String token, @Body VMTimerReq vmTimerReq);
    @POST("shutdownvm/")
    Call<MessageResponse> shutDownVm(@Header("Authorization") String token, @Body VMTimerReq vmTimerReq);
    @POST("startvm/")
    Call<MessageResponse> startVm(@Header("Authorization") String token ,@Body VMTimerReq vmTimerReq);
    @POST("stopvm/")
    Call<MessageResponse> stopVM(@Header("Authorization") String token ,@Body VMTimerReq vmTimerReq);
    @GET("getvmip")
    Call<ResponseBody> getVMIP(@Header("Authorization") String token);
}
