package com.antplay.ui.activity;

import android.app.Activity;
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
import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.ChangePassReq;
import com.antplay.models.ChangePasswordResp;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends Activity implements View.OnClickListener {
    LinearLayout backLinear;
    EditText edTxtOldPassword, edTxtNewPassword, edTxtConfirmPassword;
    Button btnUpdate;
    private ProgressBar progressBar;
    String access_token;
    RetrofitAPI retrofitAPI;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));
        access_token = SharedPreferenceUtils.getString(ChangePasswordActivity.this, Const.ACCESS_TOKEN);
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        backLinear = (LinearLayout) findViewById(R.id.back_linear);
        edTxtOldPassword = findViewById(R.id.etOldPassword);
        edTxtNewPassword = findViewById(R.id.etNewPassword);
        edTxtConfirmPassword = findViewById(R.id.etConPassword);
        btnUpdate = findViewById(R.id.btnUpdatePassword);
        progressBar = findViewById(R.id.loading_changePass);
        backLinear.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdatePassword:
                if(CheckAllFields())
                    callChangePasswordAPI();
                break;
            case R.id.back_linear:
                 onBackPressed();
                 break;
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
        ChangePassReq changePasswordRequestModal = new ChangePassReq(edTxtOldPassword.getText().toString(),
                edTxtNewPassword.getText().toString(), edTxtConfirmPassword.getText().toString());
        Call<ChangePasswordResp> call = retrofitAPI.changePassword("Bearer "+access_token,changePasswordRequestModal);
        call.enqueue(new Callback<ChangePasswordResp>() {
            @Override
            public void onResponse(Call<ChangePasswordResp> call, Response<ChangePasswordResp> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == 200)
                    AppUtils.navigateScreen(ChangePasswordActivity.this, PcView.class);
                else if (response.code() == Const.ERROR_CODE_404 || response.code() == Const.ERROR_CODE_400 ||response.code() == Const.ERROR_CODE_500 )
                    Toast.makeText(ChangePasswordActivity.this,  response.message(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<ChangePasswordResp> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
               // AppUtils.showSnack(getWindow().getDecorView().getRootView(),R.color.black,Const.something_went_wrong, ChangePasswordActivity.this);
                Toast.makeText(ChangePasswordActivity.this,  Const.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }
}