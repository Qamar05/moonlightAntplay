package com.antplay.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.Payment;
import com.antplay.models.PaymentHistory_modal;
import com.antplay.models.UserDetailsModal;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.DateFormatterHelper;
import com.antplay.utils.SharedPreferenceUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout backLinear, logoutLinear, linear_Change, linearAgree, linearWebsite, linearAbout,
            linearPayment, linearEdit, linearDiscord, linearInstagram, linearPrivacyPolicy,linear_FAQ;

    TextView  tv_manageSubs, txtUserID,txtExpiryDate,txtCurrentPlan;
    String strEmailId,access_token;
    Context mContext;
    long days = 0, month = 0,year = 0;
    int[] daysOfMonths = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    RetrofitAPI retrofitAPI;
    ProgressBar loadingProgressBar;
    boolean planActive =  false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = ProfileActivity.this;
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));
        strEmailId = SharedPreferenceUtils.getString(mContext, Const.EMAIL_ID);
        access_token = SharedPreferenceUtils.getString(mContext, Const.ACCESS_TOKEN);

        backLinear = (LinearLayout) findViewById(R.id.back_linear);
        txtCurrentPlan =  findViewById(R.id.txtCurrentPlan);
        logoutLinear = (LinearLayout) findViewById(R.id.logout_linear);
        linearAgree = (LinearLayout) findViewById(R.id.linear_agreements);
        linear_Change = (LinearLayout) findViewById(R.id.linear_changePassword);
        linearWebsite = (LinearLayout) findViewById(R.id.linear_website);
        linearAbout = (LinearLayout) findViewById(R.id.linear_aboutUs);
        linearPayment = (LinearLayout) findViewById(R.id.linear_paymentHistory);
        linearDiscord = (LinearLayout) findViewById(R.id.linear_discord);
        linearInstagram = (LinearLayout) findViewById(R.id.linear_instagram);
        linearEdit = (LinearLayout) findViewById(R.id.linear_edit);
        tv_manageSubs = (TextView) findViewById(R.id.tv_manageSubscription);
        txtUserID = (TextView) findViewById(R.id.txtUserEmailID);
        txtExpiryDate = (TextView) findViewById(R.id.txtExpiryDate);
        linearPrivacyPolicy = (LinearLayout) findViewById(R.id.linear_privacyPolicy);
        loadingProgressBar =  findViewById(R.id.loadingProgressBar);
        linear_FAQ =  findViewById(R.id.linear_FAQ);

        tv_manageSubs.setOnClickListener(this);
        backLinear.setOnClickListener(this);
        logoutLinear.setOnClickListener(this);
        linear_Change.setOnClickListener(this);
        linearAgree.setOnClickListener(this);
        linearPrivacyPolicy.setOnClickListener(this);
        linear_FAQ.setOnClickListener(this);
        linearWebsite.setOnClickListener(this);
        linearDiscord.setOnClickListener(this);
        linearInstagram.setOnClickListener(this);
        linearAbout.setOnClickListener(this);
        linearPayment.setOnClickListener(this);
        linearEdit.setOnClickListener(this);

        if(AppUtils.isOnline(mContext)) {
            getUserDetails();
            callPaymentHistoryAPI();
        } else
            AppUtils.showInternetDialog(mContext);
    }

    private void logoutMethod() {
        try {
            Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_logout);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button txtNo = dialog.findViewById(R.id.txtNo);
            Button txtYes = dialog.findViewById(R.id.txtYes);
            txtYes.setOnClickListener(view -> {
                dialog.dismiss();
                SharedPreferenceUtils.saveUserLoggedIn(ProfileActivity.this, Const.IS_LOGGED_IN, false);
                SharedPreferenceUtils.saveString(ProfileActivity.this, Const.ACCESS_TOKEN, "");
                AppUtils.navigateScreen(ProfileActivity.this, LoginSignUpActivity.class);
                finishAffinity();
            });
            txtNo.setOnClickListener(view -> {
                dialog.dismiss();
            });
            dialog.show();
        }
        catch (Exception e){
            SharedPreferenceUtils.saveUserLoggedIn(ProfileActivity.this, Const.IS_LOGGED_IN, false);
            SharedPreferenceUtils.saveString(ProfileActivity.this, Const.ACCESS_TOKEN, "");
            AppUtils.navigateScreen(ProfileActivity.this, LoginSignUpActivity.class);
            finishAffinity();
        }
        }

    private String getDateFromSec(long expiryDateInSec) {
        String dayStr, monthStr, yearStr;
        long secInDay = 24 * 60 * 60;
        boolean dayStarted = false;
        // converting seconds into days
        days = expiryDateInSec / secInDay;

        //if some seconds are more
        if (expiryDateInSec % secInDay != 0) {
            dayStarted = true;
        }
        if (dayStarted || expiryDateInSec == 0) {
            days += 1;
        }
        //getting year
        days += 4;
        year = getYear(days);

        // getting month
        month = getMonthCount(days);
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = String.valueOf(month);
        }

        if (days < 10) {
            dayStr = "0" + days;
        } else {
            dayStr = String.valueOf(days);
        }

        // construct date
        String date = year + "-" + monthStr + "-" + dayStr+" 00:00:00";
       return DateFormatterHelper.parseDate(date,DateFormatterHelper.DATE_FORMAT_TWO);
    }
    private long getYear(long totalDays) {
        long expiryYear = 1600;
        long dayCount;
        while (true) {
            expiryYear += 1;
            dayCount = dayInYear(expiryYear);
            if (totalDays >= dayCount) {
                totalDays -= dayCount;
                days -= dayCount;
            } else {
                break;
            }
        }
        return expiryYear;
    }

    private long getMonthCount(long daysCount) {
        long expiryMonth = 0;
        if (daysCount == 0) {
            return 1;
        } else {
            expiryMonth = 1;
            if (dayInYear(year) == 366) {
                daysOfMonths[1] = 29;
            }
            for (int day : daysOfMonths) {
                if (day < daysCount) {
                    expiryMonth += 1;
                    daysCount -= day;
                    days -= day;
                } else {
                    break;
                }
            }
        }
        return expiryMonth;
    }

    private int dayInYear(long yearCount) {
        if (yearCount % 4 == 0) {
            return 366;
        } else {
            return 365;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_manageSubscription:
                AppUtils.navigateScreenWithoutFinish((Activity) mContext, SubscriptionPlanActivity.class);
                break;
            case R.id.back_linear:
                finish();
                break;
            case R.id.logout_linear:
                logoutMethod();
                break;
            case R.id.linear_changePassword:
                AppUtils.navigateScreenWithoutFinish((Activity)mContext, ChangePasswordActivity.class);
                break;
            case R.id.linear_agreements:
                AppUtils.navigateScreenSendValue((Activity) mContext, GeneralWebViewActivity.class,Const.REDIRECT_URL, Const.TERMS_AND_CONDITION_URL);
                break;
            case R.id.linear_privacyPolicy:
                AppUtils.navigateScreenSendValue((Activity) mContext, GeneralWebViewActivity.class,Const.REDIRECT_URL, Const.PRIVACY_POLICY_URL);
                    break;
            case R.id.linear_website:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.WEBSITE_URL));
                startActivity(browserIntent);
                break;
            case R.id.linear_discord:
                Intent disordIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.DISCORD_URL));
                startActivity(disordIntent);
                break;
            case R.id.linear_instagram:
                Intent instaIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.INSTAGRAM_URL));
                startActivity(instaIntent);
                break;
            case R.id.linear_aboutUs:
                AppUtils.navigateScreenSendValue((Activity) mContext, GeneralWebViewActivity.class,Const.REDIRECT_URL, Const.ABOUT_US_URL);
                break;
            case R.id.linear_paymentHistory:
                AppUtils.navigateScreenWithoutFinish((Activity) mContext, PaymentHistoryActivity.class);
                break;
            case R.id.linear_edit:
                AppUtils.navigateScreenWithoutFinish((Activity) mContext, EditProfileActivity.class);
                break;
            case R.id.linear_FAQ:
                Intent faqIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.FAQ_URL));
                startActivity(faqIntent);
                break;
        }
    }
    private void getUserDetails() {
        Call<UserDetailsModal> call = retrofitAPI.getUserDetails("Bearer " + access_token);
        call.enqueue(new Callback<UserDetailsModal>() {
            @Override
            public void onResponse(Call<UserDetailsModal> call, Response<UserDetailsModal> response) {
                if (response.code() == Const.SUCCESS_CODE_200) {
                    SharedPreferenceUtils.saveString(mContext, Const.FIRSTNAME, response.body().getFirstName());
                    SharedPreferenceUtils.saveString(mContext, Const.LASTNAME, response.body().getLastName());
                    SharedPreferenceUtils.saveString(mContext, Const.EMAIL_ID, response.body().getEmail());
                    SharedPreferenceUtils.saveString(mContext, Const.PHONE_NUMBER, response.body().getPhoneNumber());
                    SharedPreferenceUtils.saveString(mContext, Const.ADDRESS, response.body().getAddress());
                    SharedPreferenceUtils.saveString(mContext, Const.STATE, response.body().getState());
                    SharedPreferenceUtils.saveString(mContext, Const.CITY, response.body().getCity());
                    SharedPreferenceUtils.saveString(mContext, Const.USERNAME, response.body().getUsername());
                    SharedPreferenceUtils.saveString(mContext, Const.PINCODE, response.body().getPincode());
                    txtUserID.setText(response.body().getEmail());
                } else if (response.code() == Const.ERROR_CODE_404 ||
                        response.code() == Const.ERROR_CODE_500 ||
                        response.code() == Const.ERROR_CODE_400) {
                    try {
                        JSONObject jObj = new JSONObject(response.errorBody().string());
                        Toast.makeText(ProfileActivity.this, jObj.getString("detail"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<UserDetailsModal> call, Throwable t) {
            //    progressBar.setVisibility(View.GONE);
                AppUtils.showToast(Const.something_went_wrong, mContext);
            }
        });
    }

    private void callPaymentHistoryAPI() {
            loadingProgressBar.setVisibility(View.VISIBLE);
            Call<PaymentHistory_modal> call = retrofitAPI.getPaymentHistory("Bearer " + access_token);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<PaymentHistory_modal> call, Response<PaymentHistory_modal> response) {
                    loadingProgressBar.setVisibility(View.GONE);
                    if(response.code()==Const.SUCCESS_CODE_200) {
                        List<Payment> paymentHistory_list = response.body().getData();
                        try {
                            for (int i = 0; i < paymentHistory_list.size(); i++) {
                                if (paymentHistory_list.get(i).getPaymentStatus().equalsIgnoreCase("active")) {
                                   // txtCurrentPlan.setText(paymentHistory_list.get(i).getBillingPlan());
                                    convertDateToString(paymentHistory_list.get(i).getExpiry_date() ,paymentHistory_list.get(i).getBillingPlan());
                                   // txtExpiryDate.setText(newdate);
                                    break;
                                }
                                Log.i("test_time2" , "testttt");

                                txtCurrentPlan.setText("No Active Plan");
                                txtExpiryDate.setText("N/A");
                            }
                        } catch (Exception e) {
                        }
                    }
                    else if (response.code()==Const.ERROR_CODE_400||
                              response.code()==Const.ERROR_CODE_500 ||
                              response.code()==Const.ERROR_CODE_404){
                        Log.i("test_time3" , "testttt");
                            txtCurrentPlan.setText("No Active Plan");
                            txtExpiryDate.setText("N/A");
                    }
                }

                @Override
                public void onFailure(Call<PaymentHistory_modal> call, Throwable t) {
                    Log.e("Hello Get VM", "Failure");
                    // loadingPB.setVisibility(View.GONE);
                }
            });
    }

    private void  convertDateToString(String expiry_date, String billingPlan) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatterOut = new SimpleDateFormat(" dd MMM , yyyy");

        SimpleDateFormat formatterOutTime = new SimpleDateFormat(" HH:mm:ss");

        String getCurrentDateTime = formatterOut.format(c.getTime());
        String getCurrentTime = formatterOutTime.format(c.getTime());



        String convertedDate = null;
        String convertedTime = null;
        try {
            Date date = formatter.parse(expiry_date);
            Date date2 = formatter.parse(expiry_date);
//            System.out.println(date);
            convertedDate = formatterOut.format(date);
            convertedTime = formatterOutTime.format(date2);

            if (getCurrentDateTime.compareTo(convertedDate) < 0) {
                 txtCurrentPlan.setText(billingPlan);
                 txtExpiryDate.setText(convertedDate);
            }
            else if(getCurrentDateTime.compareTo(convertedDate)==0){
                if(getCurrentTime.compareTo(convertedTime)<0){
                    txtCurrentPlan.setText(billingPlan);
                        txtExpiryDate.setText(convertedDate);
                    }
                    else{
                        txtCurrentPlan.setText("No Active Plan");
                        txtExpiryDate.setText("Expired");
                    }
            }
            else {
                txtCurrentPlan.setText("No Active Plan");
                txtExpiryDate.setText("Expired");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}