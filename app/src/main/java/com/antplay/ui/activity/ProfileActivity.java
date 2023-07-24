package com.antplay.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.antplay.R;
import com.antplay.preferences.AddComputerManually;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.DateFormatterHelper;
import com.antplay.utils.SharedPreferenceUtils;

public class ProfileActivity extends AppCompatActivity {

    LinearLayout backLinear, logoutLinear, linear_Change, linearAgree, linearWebsite, linearAbout,
            linearPayment, linearEdit, linearDiscord, linearInstagram, linearPrivacyPolicy;

    TextView tv_changePassword, tv_manageSubs, txtUserID,txtExpiryDate;
    AlertDialog.Builder builder;
    String strEmailId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));
        strEmailId = SharedPreferenceUtils.getString(ProfileActivity.this, Const.EMAIL_ID);

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

        setData();

        tv_manageSubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, SubscriptionPlanActivity.class);
                startActivity(i);

            }
        });


        backLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
//                startActivity(i);
                finish();
            }
        });
        logoutLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutMethod();
            }
        });

        linear_Change.setOnClickListener(v -> AppUtils.navigateScreen(ProfileActivity.this, ChangePasswordActivity.class));


        linearAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent i = new Intent(ProfileActivity.this, Agreement_User.class);
                startActivity(i);*/
                Intent intent = new Intent(ProfileActivity.this, GeneralWebViewActivity.class);
                intent.putExtra(Const.REDIRECT_URL, Const.TERMS_AND_CONDITION_URL);
                startActivity(intent);

            }
        });

        linearPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, GeneralWebViewActivity.class);
                intent.putExtra(Const.REDIRECT_URL, Const.PRIVACY_POLICY_URL);
                startActivity(intent);
            }
        });

        linearWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://antplay.tech/"));
                startActivity(browserIntent);
            }
        });
        linearDiscord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/vGHsh8MYXX"));
                startActivity(browserIntent);
            }
        });
        linearInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/antplay.tech/"));
                startActivity(browserIntent);
            }
        });


        linearAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent i = new Intent(ProfileActivity.this, AboutUs.class);
                startActivity(i);*/

                Intent intent = new Intent(ProfileActivity.this, GeneralWebViewActivity.class);
                intent.putExtra(Const.REDIRECT_URL, Const.ABOUT_US_URL);
                startActivity(intent);
            }
        });
        linearPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, PaymentHistoryActivity.class);
                startActivity(i);
            }
        });
        linearEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(i);
            }
        });
    }

    private void logoutMethod() {
        builder.setTitle(getResources().getString(R.string.logout))
                .setMessage(getResources().getString(R.string.are_you_sure))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    SharedPreferenceUtils.saveUserLoggedIn(ProfileActivity.this, Const.IS_LOGGED_IN, false);
                    SharedPreferenceUtils.saveString(ProfileActivity.this, Const.ACCESS_TOKEN, "");
                    SharedPreferenceUtils.saveString(ProfileActivity.this, Const.EMAIL_ID, "");
                    AppUtils.navigateScreen(ProfileActivity.this, LoginActivity.class);
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> {
                    dialog.cancel();
                });

        builder.show();
    }

    private void setData() {
//        String firstName = SharedPreferenceUtils.getString(ProfileActivity.this, Const.FIRSTNAME);
//        String lastName = SharedPreferenceUtils.getString(ProfileActivity.this, Const.LASTNAME);
//        String email = SharedPreferenceUtils.getString(ProfileActivity.this, Const.EMAIL_ID);
//        String phoneNumber = SharedPreferenceUtils.getString(ProfileActivity.this, Const.PHONE_NUMBER);
//        String address = SharedPreferenceUtils.getString(ProfileActivity.this, Const.ADDRESS);
//        String state = SharedPreferenceUtils.getString(ProfileActivity.this, Const.STATE);
//        String city = SharedPreferenceUtils.getString(ProfileActivity.this, Const.CITY);
//        String userName = SharedPreferenceUtils.getString(ProfileActivity.this, Const.USERNAME);
//        String expiryDate = SharedPreferenceUtils.getString(ProfileActivity.this, Const.USER_EXPIRY_DATE);
//
//        if (expiryDate!=null){
//            txtExpiryDate.setText(getDateFromSec(Long.parseLong(expiryDate)));
//        }
//        txtUserID.setText(userName);
        //txtExpiryDate.setText(getDateFromSec(Long.parseLong(expiryDate)));
        //getDateFromSec(Long.parseLong(expiryDate));
       // getDateFromSec(42076800);

    }


    long days = 0;
    long month = 0;
    long year = 0;
    int[] daysOfMonths = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

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
}