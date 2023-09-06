package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.ResetEmailReq;
import com.antplay.models.ResultResponse;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends Activity {
    EditText edtEmail;
    Button btnResetPassword;
    String strEmail;
    ProgressBar  progressBar;
    Context  mContext;
    RetrofitAPI retrofitAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mContext =  ForgotPasswordActivity.this;
        edtEmail =  findViewById(R.id.edtEmail);
        btnResetPassword =  findViewById(R.id.btnResetPassword);
        progressBar =  findViewById(R.id.progressSignUp);
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        btnResetPassword.setOnClickListener(view -> {
            if (AppUtils.validateEmailField(mContext,edtEmail)) {
                if(AppUtils.isOnline(mContext))
                    callForgotPasswordApi();
                else
                    AppUtils.showInternetDialog(mContext);
            }
        });
    }

    private void callForgotPasswordApi() {
        if (AppUtils.isOnline(mContext)) {
            progressBar.setVisibility(View.VISIBLE);
            ResetEmailReq resetEmailReq = new ResetEmailReq(edtEmail.getText().toString());
            Call<ResultResponse> call = retrofitAPI.forgotPassword(resetEmailReq);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(mContext, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (response.code() == Const.SUCCESS_CODE_200) {
                        Toast.makeText(mContext, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        AppUtils.navigateScreen(ForgotPasswordActivity.this, LoginActivity.class);
                    } else if (response.code() == Const.ERROR_CODE_500 ||
                            response.code() == Const.ERROR_CODE_400 ||
                            response.code() == Const.ERROR_CODE_404) {
                        try {
                            JSONObject jObj = new JSONObject(response.errorBody().string());
                            Toast.makeText(ForgotPasswordActivity.this, jObj.getString("detail"), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResultResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.i("Error: ", "" + t.getMessage());
                }
            });
        }
        else
            Toast.makeText(mContext, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
    }
}