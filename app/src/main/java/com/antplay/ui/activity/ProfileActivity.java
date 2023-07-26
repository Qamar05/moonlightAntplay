package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.UserDetailsModal;
import com.antplay.preferences.AddComputerManually;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.DateFormatterHelper;
import com.antplay.utils.SharedPreferenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout backLinear, logoutLinear, linear_Change, linearAgree, linearWebsite, linearAbout,
            linearPayment, linearEdit, linearDiscord, linearInstagram, linearPrivacyPolicy;

    TextView  tv_manageSubs, txtUserID,txtExpiryDate;
    AlertDialog.Builder builder;
    String strEmailId,access_token;
    Context mContext;
    long days = 0, month = 0,year = 0;
    int[] daysOfMonths = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    RetrofitAPI retrofitAPI;

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

        builder = new AlertDialog.Builder(this);
        backLinear = (LinearLayout) findViewById(R.id.back_linear);
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

        txtUserID.setText(strEmailId);

        tv_manageSubs.setOnClickListener(this);
        backLinear.setOnClickListener(this);
        logoutLinear.setOnClickListener(this);
        linear_Change.setOnClickListener(this);
        linearAgree.setOnClickListener(this);
        linearPrivacyPolicy.setOnClickListener(this);
        linearWebsite.setOnClickListener(this);
        linearDiscord.setOnClickListener(this);
        linearInstagram.setOnClickListener(this);
        linearAbout.setOnClickListener(this);
        linearPayment.setOnClickListener(this);
        linearEdit.setOnClickListener(this);
        getUserDetails();

    }



    private void logoutMethod() {
        builder.setTitle(getResources().getString(R.string.logout))
                .setMessage(getResources().getString(R.string.are_you_sure))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    SharedPreferenceUtils.saveUserLoggedIn(ProfileActivity.this, Const.IS_LOGGED_IN, false);
                    SharedPreferenceUtils.saveString(ProfileActivity.this, Const.ACCESS_TOKEN, "");
                    SharedPreferenceUtils.saveString(ProfileActivity.this, Const.EMAIL_ID, "");
                    AppUtils.navigateScreen(ProfileActivity.this, LoginSignUpActivity.class);
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> {
                    dialog.cancel();
                });

        builder.show();
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
        }
    }
    private void getUserDetails() {
        Call<UserDetailsModal> call = retrofitAPI.getUserDetails("Bearer " + access_token);
        call.enqueue(new Callback<UserDetailsModal>() {
            @Override
            public void onResponse(Call<UserDetailsModal> call, Response<UserDetailsModal> response) {
                if (response.isSuccessful()) {

                   // progressBar.setVisibility(View.GONE);
                    SharedPreferenceUtils.saveString(mContext, Const.FIRSTNAME, response.body().getFirstName());
                    SharedPreferenceUtils.saveString(mContext, Const.LASTNAME, response.body().getLastName());
                    SharedPreferenceUtils.saveString(mContext, Const.EMAIL_ID, response.body().getEmail());
                    SharedPreferenceUtils.saveString(mContext, Const.PHONE_NUMBER, response.body().getPhoneNumber());
                    SharedPreferenceUtils.saveString(mContext, Const.ADDRESS, response.body().getAddress());
                    SharedPreferenceUtils.saveString(mContext, Const.STATE, response.body().getState());
                    SharedPreferenceUtils.saveString(mContext, Const.CITY, response.body().getCity());
                    SharedPreferenceUtils.saveString(mContext, Const.USERNAME, response.body().getUsername());
                    SharedPreferenceUtils.saveString(mContext, Const.PINCODE, response.body().getPincode());
                    String expiryDate =  response.body().getExpire();
                    if (expiryDate!=null)
                        txtExpiryDate.setText(getDateFromSec(Long.parseLong(expiryDate)));
                    txtUserID.setText(response.body().getEmail());
                } else {
                    //  progressBar.setVisibility(View.GONE);
                    AppUtils.showToast(Const.no_records, mContext);
                }
            }
            @Override
            public void onFailure(Call<UserDetailsModal> call, Throwable t) {
            //    progressBar.setVisibility(View.GONE);
                AppUtils.showToast(Const.something_went_wrong, mContext);
            }
        });

    }
}