package com.antplay.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.ChangePassReq;
import com.antplay.models.ChangePasswordResp;
import com.antplay.models.ChangePasswordResponseModal;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;
import com.google.android.material.textfield.TextInputEditText;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends Activity implements View.OnClickListener {

    LinearLayout backLinear, logoutLinear;
    boolean isAllFieldChecked = false;
    EditText edTxtOldPassword, edTxtNewPassword, edTxtConfirmPassword;
    Button btnUpdate;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));
        backLinear = (LinearLayout) findViewById(R.id.back_linear);

        edTxtOldPassword = findViewById(R.id.etOldPassword);
        edTxtNewPassword = findViewById(R.id.etNewPassword);
        edTxtConfirmPassword = findViewById(R.id.etConPassword);
        btnUpdate = findViewById(R.id.btnUpdatePassword);
        progressBar = findViewById(R.id.loading_changePass);

        setOnClickListener();

        backLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(ChangePassword.this, ProfileActivity.class);
//                startActivity(i);
//                finish();
            }
        });
    }

    private void setOnClickListener() {
        backLinear.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdatePassword:
              //  isAllFieldsChecked = CheckAllLoginFields();
                if(CheckAllFields()){
                    callChangePasswordAPI();
                }
                break;
            case R.id.back_linear: {
//                Intent i = new Intent(ChangePassword.this, ProfileActivity.class);
//                startActivity(i);
//                finish();
            }
        }
    }

    private boolean CheckAllFields() {
        if (edTxtOldPassword.getText().toString().trim().length() == 0) {
            edTxtOldPassword.setError(getString(R.string.error_old_password));
            return false;
        }
        if (edTxtNewPassword.getText().toString().trim().length()<8) {
            edTxtNewPassword.setError(getString(R.string.error_pass_minimum));
            return false;
        } else if (!edTxtNewPassword.getText().toString().matches(Const.passwordRegex)) {
            edTxtNewPassword.setError(getString(R.string.pass_regex));
            return false;
        }
        if (!edTxtNewPassword.getText().toString().equals(edTxtConfirmPassword.getText().toString())) {
            edTxtConfirmPassword.setError(getString(R.string.error_password_not_match));
            return false;
        }

        return true;
    }

    private void callChangePasswordAPI() {
        progressBar.setVisibility(View.VISIBLE);
        String access_token = SharedPreferenceUtils.getString(ChangePassword.this, Const.ACCESS_TOKEN);
        RetrofitAPI retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        ChangePassReq changePasswordRequestModal = new ChangePassReq(edTxtOldPassword.getText().toString(), edTxtNewPassword.getText().toString(), edTxtConfirmPassword.getText().toString());
        Call<ChangePasswordResp> call = retrofitAPI.changePassword("Bearer "+access_token,changePasswordRequestModal);
        call.enqueue(new Callback<ChangePasswordResp>() {
            @Override
            public void onResponse(Call<ChangePasswordResp> call, Response<ChangePasswordResp> response) {
                if (response.code() == 200) {
                    Log.d("BILLING_PLANjjj", "success" );
                    progressBar.setVisibility(View.GONE);
                    AppUtils.navigateScreen(ChangePassword.this, LoginActivity.class);

                }
                else if (response.code() == Const.ERROR_CODE_404){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChangePassword.this,  response.message(), Toast.LENGTH_SHORT).show();
                   // AppUtils.showSnack(getWindow().getDecorView().getRootView(),R.color.black,response.message(),ChangePassword.this);
                }
                else if (response.code() == Const.ERROR_CODE_500){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChangePassword.this,  response.message(), Toast.LENGTH_SHORT).show();

                   // AppUtils.showSnack(getWindow().getDecorView().getRootView(),R.color.black,response.message(),ChangePassword.this);
                }

                else if (response.code() == Const.ERROR_CODE_400){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChangePassword.this,  response.message(), Toast.LENGTH_SHORT).show();

                    //  AppUtils.showSnack(getWindow().getDecorView().getRootView(),R.color.black,getString(R.string.wrong_old_pass),ChangePassword.this);
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResp> call, Throwable t) {
                Log.d("BILLING_PLAN", "Failure");
                progressBar.setVisibility(View.GONE);
                AppUtils.showSnack(getWindow().getDecorView().getRootView(),R.color.black,Const.something_went_wrong,ChangePassword.this);

            }
        });
    }
}