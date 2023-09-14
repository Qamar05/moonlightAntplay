package com.antplay.ui.activity;

import static com.antplay.utils.Const.emailPattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.UserRegisterRequest;
import com.antplay.models.UserRegisterResp;
import com.antplay.ui.adapter.StateListAdapter;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener,StateListAdapter.ButtonClickListener {

    EditText edtFirstName, edtLastName , edtPhoneNumber, edtEmail, edtPassword, edtConfirmPassword, edtAge, edtAddress, edtCity, edtPinCode;
    Button btnSignup;
    TextView  edTxtState;
    TextView txtAlreadyRegister, txtUserAgreement;
    CheckBox chkBoxUserAgreement;
    ProgressBar progressBar;
    boolean isUserAgreementChecked = false,isSubscribed = false,isNewUser=true,lastLogin=false;
    Spinner spinnerState;
    String strState, strFirstName, strMiddleName, strLastName, strPhoneNumber, strEmail, strPassword, strAge, strAddress, strPinCode, strCity;
    RetrofitAPI retrofitAPI;
    Context mContext;
    ArrayList<String> stateList;
    Dialog dialog;
    boolean showPassword = false;
    boolean showConfirmPassword = false;
    ImageView ivPasswordShow ,ivConfirmPasswordShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext= SignupActivity.this;
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edTxtState = (TextView) findViewById(R.id.edTxtState);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        edtAge = (EditText) findViewById(R.id.edtAge);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtPinCode = (EditText) findViewById(R.id.edtPinCode);
        spinnerState = (Spinner) findViewById(R.id.spinnerState);
        ivPasswordShow = (ImageView) findViewById(R.id.ivPasswordShow);
        ivConfirmPasswordShow = (ImageView) findViewById(R.id.ivConfirmPasswordShow);
        edtCity = (EditText) findViewById(R.id.edtCity);
        progressBar = (ProgressBar) findViewById(R.id.progressSignUp);
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);

        btnSignup = (Button) findViewById(R.id.btnSignUp);
        txtAlreadyRegister = (TextView) findViewById(R.id.txtAlreadyRegister);
        chkBoxUserAgreement = (CheckBox) findViewById(R.id.chkBoxUserAgreement);
        txtUserAgreement = findViewById(R.id.txtUserAgreement);

        chkBoxUserAgreement.setOnClickListener(this);
        txtAlreadyRegister.setOnClickListener(this);
        txtUserAgreement.setOnClickListener(this);
        edTxtState.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        ivPasswordShow.setOnClickListener(this);
        ivConfirmPasswordShow.setOnClickListener(this);
        stateList =  new ArrayList<>();
        stateList  =  AppUtils.stateList();
    }

    private void callRegisterApi(String strFirstName, String strMiddleName, String strLastName,
                                 String strEmail, String strPhoneNumber, boolean lastLogin, boolean isNewUser,
                                 boolean isSubscribed, String strPassword, String strAddress, String strAge,
                                 String strState, String strCity, String strPinCode) {

            progressBar.setVisibility(View.VISIBLE);
            UserRegisterRequest userRegisterRequestv = new UserRegisterRequest(strFirstName, strLastName, strEmail, strPhoneNumber,
                    strAddress, strAge, strState, strCity, strPinCode, strPassword);

            Call<UserRegisterResp> call = retrofitAPI.userRegister(userRegisterRequestv);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<UserRegisterResp> call, Response<UserRegisterResp> response) {
                    progressBar.setVisibility(View.GONE);
                        if (response.code()==Const.SUCCESS_CODE_200){
                            Toast.makeText(SignupActivity.this, getResources().getString(R.string.check_mail), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(SignupActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            AppUtils.navigateScreen((Activity) mContext, LoginActivity.class);
                        }
                        else if (response.code()==Const.ERROR_CODE_500 ||
                                response.code()==Const.ERROR_CODE_400 ||
                                response.code()==Const.ERROR_CODE_404) {
                            try {
                                JSONObject jObj = new JSONObject(response.errorBody().string());
                                Toast.makeText(SignupActivity.this, jObj.getString("detail"), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                }
                @Override
                public void onFailure(Call<UserRegisterResp> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
    }

    private boolean validateFormField() {
        if (edtFirstName.getText().toString().contains(" ")) {
            edtFirstName.setError(getString(R.string.remove_whitespace));
            edtFirstName.requestFocus();
            return false;
        }
        if (edtFirstName.length() == 0) {
            edtFirstName.setError(getString(R.string.error_firstname));
            edtFirstName.requestFocus();
            return false;
        }
        if (edtLastName.getText().toString().contains(" ")) {
            edtLastName.setError(getString(R.string.remove_whitespace));
            edtLastName.requestFocus();
            return false;
        }
        if (edtLastName.length() == 0) {
            edtLastName.setError(getString(R.string.error_lastname));
            edtLastName.requestFocus();
            return false;
        }
        if (edtPhoneNumber.getText().toString().contains(" ")) {
            edtPhoneNumber.setError(getString(R.string.remove_whitespace));
            edtPhoneNumber.requestFocus();
            return false;
        }
        if (edtPhoneNumber.length() == 0) {
            edtPhoneNumber.setError(getString(R.string.error_phone));
            edtPhoneNumber.requestFocus();
            return false;
        }
        if (edtEmail.getText().toString().contains(" ")) {
            edtEmail.setError(getString(R.string.remove_whitespace));
            edtEmail.requestFocus();
            return false;
        } else if (edtEmail.length() == 0) {
            edtEmail.setError(getString(R.string.error_email));
            edtEmail.requestFocus();
            return false;
        } else if (!edtEmail.getText().toString().matches(emailPattern)) {
            edtEmail.setError(getString(R.string.error_invalidEmail));
            edtEmail.requestFocus();
            return false;
        }
        if (edtPassword.getText().toString().contains(" ")) {
            edtPassword.setError(getString(R.string.remove_whitespace));
            edtPassword.requestFocus();
            return false;
        } else if (edtPassword.length() == 0) {
            edtPassword.setError(getString(R.string.error_password));
            edtPassword.requestFocus();
            return false;
        } else if (edtPassword.length() < 8) {
            edtPassword.setError(getString(R.string.error_pass_minimum));
            edtPassword.requestFocus();
            return false;
        } else if (!edtPassword.getText().toString().matches(Const.passwordRegex)) {
            edtPassword.setError(getString(R.string.pass_regex));
            edtPassword.requestFocus();
            return false;
        }
        if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            edtConfirmPassword.setError(getString(R.string.error_password_not_match));
            edtConfirmPassword.requestFocus();
            return false;
        }
        if (edtAge.getText().toString().trim().contains(" ")) {
            edtAge.setError(getString(R.string.remove_whitespace));
            edtAge.requestFocus();
            return false;
        } else if (edtAge.length() == 0) {
            edtAge.setError(getString(R.string.error_age));
            edtAge.requestFocus();
            return false;
        } else if (Integer.parseInt(edtAge.getText().toString().trim()) < 18) {
            edtAge.setError(getString(R.string.error_greater_18));
            edtAge.requestFocus();
            return false;
        }
        if (edtAddress.getText().toString().trim().length() == 0) {
            edtAddress.setError(getString(R.string.error_address));
            edtAddress.requestFocus();
            return false;
        }

        if (edTxtState.getText().toString().trim().length() == 0) {
            edtCity.setError(getString(R.string.error_state));
            edtCity.requestFocus();
            return false;
        }
        if (edtCity.getText().toString().trim().length() == 0) {
            edtCity.setError(getString(R.string.error_city));
            edtCity.requestFocus();
            return false;
        }
        if (edtPinCode.getText().toString().contains(" ")) {
            edtPinCode.setError(getString(R.string.remove_whitespace));
            edtPinCode.requestFocus();
            return false;
        }
        if (edtPinCode.length() == 0) {
            edtPinCode.setError(getString(R.string.error_pinCode));
            edtPinCode.requestFocus();
            return false;
        }
        if (!chkBoxUserAgreement.isChecked()) {
              Toast.makeText(this, getString(R.string.error_checkbox), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chkBoxUserAgreement:
                isUserAgreementChecked = chkBoxUserAgreement.isChecked();
                break;
            case R.id.txtAlreadyRegister:
                AppUtils.navigateScreen((Activity) mContext, LoginActivity.class);
                break;
            case R.id.txtUserAgreement:
                AppUtils.navigateScreenSendValue((Activity) mContext, GeneralWebViewActivity.class,Const.REDIRECT_URL, Const.TERMS_AND_CONDITION_URL);
                break;
            case R.id.edTxtState:
                openStateDialog();
                break;
            case R.id.ivPasswordShow:
                showHidePassword(edtPassword , ivPasswordShow);
                break;

            case R.id.ivConfirmPasswordShow:
                showHideConfirmPassword(edtConfirmPassword , ivConfirmPasswordShow);
                break;

            case R.id.btnSignUp:
                if (validateFormField()) {
                    if (AppUtils.isOnline(mContext)) {
                        strFirstName = edtFirstName.getText().toString().trim();
                        strLastName = edtLastName.getText().toString().trim();
                        strEmail = edtEmail.getText().toString().trim();
                        strPhoneNumber = edtPhoneNumber.getText().toString().trim();
                        strMiddleName = edtFirstName.getText().toString().trim();
                        strPassword = edtPassword.getText().toString().trim();
                        strAddress = edtAddress.getText().toString();
                        strAge = edtAge.getText().toString().trim();
                        strCity = edtCity.getText().toString().trim();
                        strPinCode = edtPinCode.getText().toString();
                        strState = edTxtState.getText().toString();
                        callRegisterApi(strFirstName, strMiddleName, strLastName,
                                strEmail, strPhoneNumber, lastLogin, isNewUser, isSubscribed,
                                strPassword, strAddress, strAge, strState, strCity, strPinCode);
                    }
                    else
                        AppUtils.showInternetDialog(mContext);
                }
                break;
        }
    }

    private void openStateDialog() {
        dialog = new Dialog(SignupActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.state_dialog_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rvStateList =(RecyclerView) dialog.findViewById(R.id.rvStateList);
        StateListAdapter.ButtonClickListener buttonClickListener = SignupActivity.this;
        if(stateList!=null) {
            StateListAdapter adapter = new StateListAdapter(mContext, stateList, buttonClickListener);
            rvStateList.setAdapter(adapter);
        }
        dialog.show();
    }

    @Override
    public void onButtonClick(int value) {
        dialog.dismiss();
        edTxtState.setText(stateList.get(value));
    }
    private void showHidePassword(EditText  passwordEditText , ImageView ivPassword) {
        if(!showPassword) {
            if (passwordEditText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ivPassword.setImageResource(R.drawable.visibile_icon);
                //Show Password
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPassword = true;
            }
        }
        else{
            ivPassword.setImageResource(R.drawable.visibility_off);
            //Hide Password
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showPassword = false;
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