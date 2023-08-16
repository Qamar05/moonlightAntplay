package com.antplay.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.SendOTPResponse;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.GenericTextWatcher;
import com.antplay.utils.SMSReceiver;
import com.antplay.utils.SharedPreferenceUtils;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOTP extends Activity implements View.OnClickListener {

    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four,otp_textbox_five,otp_textbox_six, et_newPassword, et_confirmPassword;
    TextView tv_verifyotp, tv_back, tv_timer, tv_sec, tv_resend,txtDidNotReceiveOTP,txtVerificationCodeSentTo;
    ImageView img_back;
    LinearLayout linearLayout;
    String getMobile;
    SMSReceiver smsBroadcastReceiver;
    Context mContext;
    RetrofitAPI retrofitAPI;


    private static final int REQ_USER_CONSENT = 200;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_otp);
        mContext =  VerifyOTP.this;
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);

        progressBar =  findViewById(R.id.progressBar);
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

        startSmartUserConsent();
        registerBroadcastReceiver();
        callTimer();


        tv_resend.setOnClickListener(this);
        tv_verifyotp.setOnClickListener(this);
        img_back.setOnClickListener(this);


    }

    private void callVerifyOTP() {
        progressBar.setVisibility(View.VISIBLE);
        String otp = otp_textbox_one.getText().toString()+otp_textbox_two.getText().toString()+otp_textbox_three.getText().toString()+otp_textbox_four.getText().toString()+otp_textbox_five.getText().toString()+otp_textbox_six.getText().toString();
        Call<ResponseBody> call = retrofitAPI.verifyOTP(getMobile,otp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.VISIBLE);
                if (response.code()==Const.SUCCESS_CODE_200) {
                    String responseValue;
                    try {
                        responseValue = response.body().string();
                        JSONObject jObj =  new JSONObject(responseValue);
                        if (jObj.getString("success").equals("True")) {
                            String accessToken = jObj.getJSONObject("data").getString("access");
                            SharedPreferenceUtils.saveUserLoggedIn(mContext, Const.IS_LOGGED_IN, true);
                            SharedPreferenceUtils.saveString(mContext, Const.ACCESS_TOKEN, accessToken);
                            AppUtils.navigateScreen((Activity) mContext, PcView.class);
                            finishAffinity();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else if (response.code() == Const.ERROR_CODE_500 || response.code() == Const.ERROR_CODE_400||response.code() == Const.ERROR_CODE_404) {
                    try {
                        JSONObject jObj = new JSONObject(response.errorBody().string());
                        String detailValue = jObj.getString("error");
                        Toast.makeText(VerifyOTP.this, detailValue, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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
                callSendOTP();
                break;
            case R.id.verifyOTP:
                if (validateOTPTextFields()){
                    if (AppUtils.isOnline(mContext))
                        callVerifyOTP();
                    else
                        AppUtils.showInternetDialog(mContext);
                }
                break;
            case R.id.imgBack:
                finish();
                break;
        }

    }

    private void registerBroadcastReceiver(){
        smsBroadcastReceiver = new SMSReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener = new SMSReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent,REQ_USER_CONSENT);
            }

            @Override
            public void onFailure() {

            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver,intentFilter);
    }

    private void startSmartUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()){
            String otpValue = matcher.group(0);
            otp_textbox_one.setText(String.valueOf(otpValue.charAt(0)));
            otp_textbox_two.setText(String.valueOf(otpValue.charAt(1)));
            otp_textbox_three.setText(String.valueOf(otpValue.charAt(2)));
            otp_textbox_four.setText(String.valueOf(otpValue.charAt(3)));
            otp_textbox_five.setText(String.valueOf(otpValue.charAt(4)));
            otp_textbox_six.setText(String.valueOf(otpValue.charAt(5)));

        }
    }
    private void callSendOTP() {
        progressBar.setVisibility(View.VISIBLE);
        Call<SendOTPResponse> call = retrofitAPI.sendOTP(Const.DEV_URL+"getuserbyphone/"+getMobile);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == Const.SUCCESS_CODE_200) {
                    callTimer();
                } else if (response.code() == Const.ERROR_CODE_404) {
                    Toast.makeText(mContext, getString(R.string.enter_registered_mobile), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SendOTPResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}