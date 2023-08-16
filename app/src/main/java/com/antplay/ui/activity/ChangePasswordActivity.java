package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout backLinear;
    EditText edTxtOldPassword, edTxtNewPassword, edTxtConfirmPassword;
    Button btnUpdate;
    private ProgressBar progressBar;
    String access_token;
    RetrofitAPI retrofitAPI;
    ImageView ivOldPasswordShow,ivNewPasswordShow,ivConfirmPasswordShow;
    boolean showOldPassword = false,showNewPassword = false,showConfirmPassword = false;
    Context mContext;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));
        mContext  =  ChangePasswordActivity.this;
        access_token = SharedPreferenceUtils.getString(ChangePasswordActivity.this, Const.ACCESS_TOKEN);
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        backLinear = (LinearLayout) findViewById(R.id.back_linear);
        edTxtOldPassword = findViewById(R.id.etOldPassword);
        edTxtNewPassword = findViewById(R.id.etNewPassword);
        edTxtConfirmPassword = findViewById(R.id.etConPassword);
        btnUpdate = findViewById(R.id.btnUpdatePassword);
        progressBar = findViewById(R.id.loading_changePass);

        ivOldPasswordShow = findViewById(R.id.ivOldPasswordShow);
        ivNewPasswordShow = findViewById(R.id.ivNewPasswordShow);
        ivConfirmPasswordShow = findViewById(R.id.ivConfirmPasswordShow);
        backLinear.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        ivOldPasswordShow.setOnClickListener(this);
        ivNewPasswordShow.setOnClickListener(this);
        ivConfirmPasswordShow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdatePassword:
                if(CheckAllFields()) {
                    if (AppUtils.isOnline(mContext))
                        callChangePasswordAPI();
                    else
                        AppUtils.showInternetDialog(mContext);
                }
                break;
            case R.id.back_linear:
                 onBackPressed();
                 break;
            case R.id.ivOldPasswordShow:
                showHideOldPassword(edTxtOldPassword , ivOldPasswordShow);
                 break;
            case R.id.ivNewPasswordShow:
                showHideNewPassword(edTxtNewPassword,ivNewPasswordShow);
                 break;
            case R.id.ivConfirmPasswordShow:
                showHideConfirmPassword(edTxtConfirmPassword , ivConfirmPasswordShow);
                break;
            }
    }

    private boolean CheckAllFields() {
        if (edTxtOldPassword.getText().toString().trim().length() == 0) {
            edTxtOldPassword.setError(getString(R.string.error_old_password));
            edTxtOldPassword.requestFocus();
            return false;
        }
        if (edTxtNewPassword.getText().toString().trim().length()<8) {
            edTxtNewPassword.setError(getString(R.string.error_pass_minimum));
            edTxtNewPassword.requestFocus();
            return false;
        } else if (!edTxtNewPassword.getText().toString().matches(Const.passwordRegex)) {
            edTxtNewPassword.setError(getString(R.string.pass_regex));
            edTxtNewPassword.requestFocus();
            return false;
        }
        if (!edTxtNewPassword.getText().toString().equals(edTxtConfirmPassword.getText().toString())) {
            edTxtConfirmPassword.setError(getString(R.string.error_password_not_match));
            edTxtConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void callChangePasswordAPI() {
        progressBar.setVisibility(View.VISIBLE);
        ChangePassReq changePasswordRequestModal = new ChangePassReq(edTxtOldPassword.getText().toString(),
                edTxtNewPassword.getText().toString(), edTxtConfirmPassword.getText().toString());
        Call<ChangePasswordResp> call = retrofitAPI.changePassword("Bearer "+access_token,changePasswordRequestModal);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ChangePasswordResp> call, Response<ChangePasswordResp> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == Const.SUCCESS_CODE_200) {
                    Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    AppUtils.navigateScreen(ChangePasswordActivity.this, PcView.class);
                } else if (response.code() == Const.ERROR_CODE_400 ||
                        response.code() == Const.ERROR_CODE_500 ||
                        response.code() == Const.ERROR_CODE_404) {
                    try {
                        JSONObject jObj = new JSONObject(response.errorBody().string());
                        String value = jObj.getString("old_password");
                        JSONArray jsonArray = new JSONArray(value);
                        String finalValue = jsonArray.get(0).toString();
                        Toast.makeText(ChangePasswordActivity.this, "" + finalValue, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ChangePasswordResp> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChangePasswordActivity.this, Const.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showHideOldPassword(EditText  passwordEditText , ImageView ivPassword) {
        if(!showOldPassword) {
            if (passwordEditText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ivPassword.setImageResource(R.drawable.visibile_icon);
                //Show Password
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showOldPassword = true;
            }
        }
        else{
            ivPassword.setImageResource(R.drawable.visibility_off);
            //Hide Password
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showOldPassword = false;
        }
    }
    private void showHideNewPassword(EditText  passwordEditText , ImageView ivPassword) {
        if(!showNewPassword) {
            if (passwordEditText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ivPassword.setImageResource(R.drawable.visibile_icon);
                //Show Password
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showNewPassword = true;
            }
        }
        else{
            ivPassword.setImageResource(R.drawable.visibility_off);
            //Hide Password
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showNewPassword = false;
        }
    }

    private void showHideConfirmPassword(EditText  passwordEditText , ImageView ivPassword) {
        if(!showConfirmPassword) {
            if (passwordEditText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ivPassword.setImageResource(R.drawable.visibile_icon);
                //Show Password
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showConfirmPassword = true;
            }
        }
        else{
            ivPassword.setImageResource(R.drawable.visibility_off);
            //Hide Password
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showConfirmPassword = false;
        }
    }
}