package com.antplay.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.LoginRequestModal;
import com.google.gson.Gson;
import com.antplay.PcView;
import com.antplay.models.LoginResponse;
import com.antplay.utils.Const;
import com.antplay.utils.RestClient;
import com.antplay.utils.SharedPreferenceUtils;
import com.antplay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity implements View.OnClickListener {
    private String TAG = "ANT_PLAY";
    Button btnLetsGo;
    TextView tvForgetPass, tvSignupHere;
    EditText etEmail, etPass;
    boolean isAllFieldsChecked = false;
    String st_email, st_password;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvForgetPass = (TextView) findViewById(R.id.tv_forgetPass);
        tvSignupHere = (TextView) findViewById(R.id.tv_signupHere);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPass = (EditText) findViewById(R.id.et_password);
        btnLetsGo = (Button) findViewById(R.id.btn_signup);
        loadingPB = (ProgressBar) findViewById(R.id.loadingLogin_progress_xml);

        etEmail.setText("shobhit.agarwal@vmstechs.com");
        etPass.setText("Test@1234");

        setOnClickListener();
    }

    private void setOnClickListener() {
        tvForgetPass.setOnClickListener(this);
        tvSignupHere.setOnClickListener(this);
        btnLetsGo.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgetPass:
                Intent intentForgotPassword = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intentForgotPassword);
                break;
            case R.id.tv_signupHere:
                Intent intentSignup = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intentSignup);
                break;
            case R.id.btn_signup:
                // isAllFieldsChecked = CheckAllLoginFields();
                isAllFieldsChecked = true;


                if (isAllFieldsChecked) {
                    // we can call Api here
                    st_email = etEmail.getText().toString();
                    st_password = etPass.getText().toString();
                   // callLoginAPi(st_email,st_password);

                    callManualLoginAPI(st_email, st_password);
//                    Intent i = new Intent(LoginScreenActivity.this, MainActivity.class);
//                    startActivity(i);
                }
                break;
        }
    }


    private boolean CheckAllLoginFields() {
        // String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern = "^(?:\\d{10}|\\w+@\\w+\\.\\w{2,3})$";

        if (etEmail.getText().toString().trim().length() == 0) {
            etEmail.setError(getString(R.string.error_email));
            return false;
        } else if (!etEmail.getText().toString().matches(emailPattern)) {
            etEmail.setError(getString(R.string.error_invalidEmail));
            return false;
        }

        if (etPass.getText().toString().trim().length() == 0) {
            etPass.setError(getString(R.string.error_password));
            return false;
        } else if (etPass.length() < 8) {
            etPass.setError(getString(R.string.error_pass_minimum));
            return false;
        }
        return true;
    }

    /* private void callLoginAPI(String email, String password) {
         loadingPB.setVisibility(View.VISIBLE);
         RetrofitAPI retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
         LoginRequestModal loginRequestModal = new LoginRequestModal(email, password);
         Call<LoginResponseModel> call = retrofitAPI.loginUser(loginRequestModal);
         call.enqueue(new Callback<LoginResponseModel>() {
             @Override
             public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                 if (response.code() == 200) {
                     loadingPB.setVisibility(View.GONE);
                     assert response.body() != null;
                     Log.d(TAG, "Email : " + response.body().getEmail()+" Access Token : "+response.body().getData().getAccess());

                     SharedPreferenceUtils.saveBoolean(LoginActivity.this, Const.IS_LOGGED_IN, true);
                     SharedPreferenceUtils.saveString(LoginActivity.this, Const.ACCESS_TOKEN, response.body().getData().getAccess());
                     Intent i = new Intent(LoginActivity.this, PcView.class);
                     startActivity(i);
                     finish();

                 } else if (response.code() == 401) {
                     loadingPB.setVisibility(View.GONE);
                     Log.e(TAG, "Else condition");
                    // AppUtils.showSnack(getWindow().getDecorView().getRootView(), R.color.black, Const.password_error, LoginScreenActivity.this);
                 } else {
                     loadingPB.setVisibility(View.GONE);
                    // AppUtils.showSnack(getWindow().getDecorView().getRootView(), R.color.black, Const.no_records, LoginScreenActivity.this);

                 }
             }

             @Override
             public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                 Log.e(TAG, "Reason Of Failure : " + t);
                 loadingPB.setVisibility(View.GONE);
              //   AppUtils.showSnack(getWindow().getDecorView().getRootView(), R.color.black, Const.something_went_wrong, LoginScreenActivity.this);
             }
         });

     }
 */
    private void callManualLoginAPI(String email, String password) {
        loadingPB.setVisibility(View.VISIBLE);
        HashMap<String, String> loginMap = new HashMap<>();
        loginMap.put("email", email);
        loginMap.put("password", password);
        new RestClient(LoginActivity.this).postRequestWithoutMethod("new_holiday", "login/", loginMap, new RestClient.ResponseListenerUpdated() {
            @Override
            public void onResponse(String tag, int responseCode, String response) {
                // AlertUtil.hideProgressDialog();
                if (response != null) {
                    LoginResponse loginResponse = new Gson().fromJson(response, LoginResponse.class);

                    if (responseCode == 200) {
                        Log.d(TAG, "Response : " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String accessToken = jsonObject.getJSONObject("data").getString("access");
                            Log.d(TAG, "Access Token : " + accessToken);
                           // SharedPreferenceUtils.saveBoolean(LoginActivity.this, Const.IS_LOGGED_IN, true);
                            SharedPreferenceUtils.saveString(LoginActivity.this, Const.ACCESS_TOKEN, accessToken);
//                            Intent i = new Intent(LoginActivity.this, PcView.class);
                            Intent i = new Intent(LoginActivity.this, PaymentHistory.class);
                            startActivity(i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (responseCode == 401) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String details = jsonObject.getString("detail");
                            Log.d(TAG, "Response Details : " + details);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Something went wrong, please try again later.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new RestClient.ErrorListener() {
            @Override
            public void onError(String tag, String errorMsg, long statusCode) {
                loadingPB.setVisibility(View.GONE);
                Log.e(TAG, "Reason Of Failure : " + errorMsg);

            }
        });

    }

    private void callLoginAPi(String stEmail,String strPassword) {

        loadingPB.setVisibility(View.VISIBLE);
        LoginRequestModal loginRequestModal = new LoginRequestModal(stEmail,strPassword);
        RetrofitAPI retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        //

        Call<LoginResponse> call = retrofitAPI.userLogin(loginRequestModal);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loadingPB.setVisibility(View.GONE);
                if (response != null) {

                    if (response.code() == 200) {
                        Log.d(TAG, "Response : " + response.body());
                        try {
                            String accessToken = response.body().getData().getAccess();
                            SharedPreferenceUtils.saveString(LoginActivity.this, Const.ACCESS_TOKEN, accessToken);
                            Intent i = new Intent(LoginActivity.this, PcView.class);
                            startActivity(i);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == 401) {
                        try {
                            JSONObject jObj =  new JSONObject(response.errorBody().string());
                            String detailValue  =  jObj.getString("detail");
                            Toast.makeText(LoginActivity.this,  detailValue, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Something went wrong, please try again later.", Toast.LENGTH_LONG).show();
                    }
                }

            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loadingPB.setVisibility(View.GONE);
                Log.i("Error: ", ""+t.getMessage());
            }
        });
    }


}