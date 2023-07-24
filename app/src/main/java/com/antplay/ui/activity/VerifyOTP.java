package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.VerifyOTPResponseModal;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.GenericTextWatcher;
import com.antplay.utils.SharedPreferenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOTP extends Activity implements View.OnClickListener {

    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four,otp_textbox_five,otp_textbox_six, et_newPassword, et_confirmPassword;
    TextView tv_verifyotp, tv_back, tv_timer, tv_sec, tv_resend,txtDidNotReceiveOTP,txtVerificationCodeSentTo;
    ImageView img_back;
    LinearLayout linearLayout;
    String getMobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_otp);

        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);
        otp_textbox_five = findViewById(R.id.otp_edit_box5);
        otp_textbox_six = findViewById(R.id.otp_edit_box6);
        tv_back = findViewById(R.id.txtBack);
        img_back = findViewById(R.id.imgBack);
        linearLayout = findViewById(R.id.linear_timer);
        tv_timer = findViewById(R.id.tv_timer);
        tv_sec = findViewById(R.id.tv_second);
        tv_resend = findViewById(R.id.tv_resend);
        tv_verifyotp = (TextView) findViewById(R.id.verifyOTP);
        txtVerificationCodeSentTo = (TextView) findViewById(R.id.txtCodeHasSent);
        txtDidNotReceiveOTP = (TextView) findViewById(R.id.tv_didnot_received);
        if (getIntent().hasExtra("mobile")){
            getMobile = getIntent().getStringExtra("mobile");
        }
        String verificationCodeSent = getResources().getString(R.string.verification_cade_sent_to)+" "+getMobile;
        txtVerificationCodeSentTo.setText(verificationCodeSent);
       // Log.e("mobile get number", getMobile);


        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six};

        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));
        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit));

        callTimer();


        tv_resend.setOnClickListener(this);
        tv_verifyotp.setOnClickListener(this);
        img_back.setOnClickListener(this);


    }

    private void callVerifyOTP() {
        String otp = otp_textbox_one.getText().toString()+otp_textbox_two.getText().toString()+otp_textbox_three.getText().toString()+otp_textbox_four.getText().toString()+otp_textbox_five.getText().toString()+otp_textbox_six.getText().toString();
        Log.e("otp get", otp);
        RetrofitAPI retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        Call<VerifyOTPResponseModal> call = retrofitAPI.verifyOTP(getMobile,otp);
        call.enqueue(new Callback<VerifyOTPResponseModal>() {
            @Override
            public void onResponse(Call<VerifyOTPResponseModal> call, Response<VerifyOTPResponseModal> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equals("True")) {
                        SharedPreferenceUtils.saveUserLoggedIn(VerifyOTP.this, Const.IS_LOGGED_IN, true);
                        SharedPreferenceUtils.saveString(VerifyOTP.this, Const.ACCESS_TOKEN, response.body().getData().getAccess());
                        AppUtils.navigateScreen(VerifyOTP.this, PcView.class);
                    }
                }
                else if (response.code() == Const.ERROR_CODE_500 || response.code() == Const.ERROR_CODE_400||response.code() == Const.ERROR_CODE_404)
                    Toast.makeText(VerifyOTP.this, getString(R.string.error_wrong_otp), Toast.LENGTH_SHORT).show();
                  //  AppUtils.showSnack(getWindow().getDecorView().getRootView(),R.color.black,getString(R.string.error_wrong_otp), VerifyOTP.this);


                else
                    Toast.makeText(VerifyOTP.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                   //AppUtils.showSnack(getWindow().getDecorView().getRootView(),R.color.black,response.body().getMessage(), VerifyOTP.this);


            }

            @Override
            public void onFailure(Call<VerifyOTPResponseModal> call, Throwable t) {
                AppUtils.showToast(Const.something_went_wrong, VerifyOTP.this);

            }
        });

    }

    private boolean validateOTPTextFields() {
        if (otp_textbox_one.getText().length() < 1 || otp_textbox_two.getText().length() < 1 || otp_textbox_three.getText().length() < 1 || otp_textbox_four.getText().length() < 1 || otp_textbox_five.getText().length() < 1 || otp_textbox_six.getText().length() < 1) {
            Toast.makeText(VerifyOTP.this, getResources().getString(R.string.enter_otp), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void callTimer() {
        new CountDownTimer(40000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_timer.setText(String.valueOf(millisUntilFinished / 1000));
                // logic to set the EditText could go here
            }

            public void onFinish() {
                tv_resend.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
                txtDidNotReceiveOTP.setVisibility(View.INVISIBLE);
                //tv_timer.setText("Resend!");
            }

        }.start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_resend:
                linearLayout.setVisibility(View.VISIBLE);
                tv_resend.setVisibility(View.INVISIBLE);
                txtDidNotReceiveOTP.setVisibility(View.VISIBLE);
                callTimer();
                break;
            case R.id.verifyOTP:
                if (validateOTPTextFields())
                    callVerifyOTP();
                break;
            case R.id.imgBack:
                finish();
                break;
        }

    }
}