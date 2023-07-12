package com.antplay.api;

import com.antplay.models.GetVMResponse;
import com.antplay.models.UpdatePinRequestModal;
import com.antplay.models.UpdatePinResponseModal;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface RetrofitAPI {

    @POST("vmauth/")
    Call<UpdatePinResponseModal> updatePin(@Header("Authorization") String token, @Body UpdatePinRequestModal updatePinRequestModal);


  /*  @POST("login/") //login2/
    Call<LoginResponseModel> loginUser(@Body LoginRequestModal dataModal);
*/
    @GET("getvm/")
    Call<GetVMResponse> getVMFromServer(@Header("Authorization") String token);
}
