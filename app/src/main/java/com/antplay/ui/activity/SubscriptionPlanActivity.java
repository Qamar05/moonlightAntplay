package com.antplay.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.antplay.AppView;
import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.AllBillingPlanResp;
import com.antplay.models.BillingDataList;
import com.antplay.models.Payment;
import com.antplay.models.PaymentHistory_modal;
import com.antplay.models.StartPaymentReq;
import com.antplay.models.StartPaymentResp;
import com.antplay.models.UserViewResponse;
import com.antplay.ui.adapter.PaymentHistoryAdapter;
import com.antplay.ui.adapter.SubscriptionPlanAdapter;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionPlanActivity extends AppCompatActivity implements SubscriptionPlanAdapter.ButtonClickListener {
    RecyclerView rvSubscriptionPlans;
    SubscriptionPlanAdapter adapter;
    List<BillingDataList> planList;
    ProgressBar progressSubscriptionPlan;
    ImageView imgBack;

    TextView backText;

    TextView tvNoDataFound;
    String accessToken;
    SubscriptionPlanAdapter.ButtonClickListener buttonClickListener;
    RetrofitAPI retrofitAPI;
    Context mContext;
    LinearLayout back_linear;

    boolean paymentStatus =  false;

    boolean isSUBScriptionScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        mContext = SubscriptionPlanActivity.this;
        back_linear =  findViewById(R.id.back_linear);
        rvSubscriptionPlans = findViewById(R.id.rvSubscriptionPlans);
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        accessToken = SharedPreferenceUtils.getString(SubscriptionPlanActivity.this, Const.ACCESS_TOKEN);
        progressSubscriptionPlan = findViewById(R.id.progressSubscriptionPlan);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        imgBack = findViewById(R.id.imgBack);
        backText =  findViewById(R.id.backText);
        SwipeRefreshLayout swipeLayout = findViewById(R.id.refreshLayout);
        swipeLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                swipeLayout.setRefreshing(false);
                if (AppUtils.isOnline(mContext))
                    getPlanApi();
                else
                    AppUtils.showInternetDialog(mContext);
            }, 1000);
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.teal_700));
        imgBack.setOnClickListener(v -> finish());
        back_linear.setOnClickListener(view -> {
            AppUtils.navigateScreen((Activity) mContext,PcView.class);
            finishAffinity();
        });

        buttonClickListener = this;
        if (AppUtils.isOnline(mContext))
            getPlanApi();
        else
            AppUtils.showInternetDialog(mContext);


    }

    private void getPlanApi() {
        progressSubscriptionPlan.setVisibility(View.VISIBLE);
        Call<AllBillingPlanResp> call = retrofitAPI.getBillingPlan("Bearer " + accessToken, Const.ANDROID);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AllBillingPlanResp> call, Response<AllBillingPlanResp> response) {
                progressSubscriptionPlan.setVisibility(View.GONE);
                if (response.code()==200) {
                    planList = response.body().getData();
                    if (response != null && planList.size() > 0) {
                        adapter = new SubscriptionPlanAdapter(SubscriptionPlanActivity.this, planList, buttonClickListener);
                        rvSubscriptionPlans.setAdapter(adapter);
                    } else {
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        tvNoDataFound.setText(getString(R.string.noDataFound));
                    }
                }
                else if (response.code() == Const.ERROR_CODE_400 ||
                        response.code()==Const.ERROR_CODE_500||
                        response.code()==Const.ERROR_CODE_404 ||
                        response.code()==401) {
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    tvNoDataFound.setText(getString(R.string.noDataFound));
                }
            }

            @Override
            public void onFailure(Call<AllBillingPlanResp> call, Throwable t) {
                progressSubscriptionPlan.setVisibility(View.GONE);
                Log.i("Error: ", "" + t.getMessage());
            }
        });
    }

    @Override
    public void onButtonClick(int idValue) {
        if (AppUtils.isOnline(mContext)) {
            progressSubscriptionPlan.setVisibility(View.VISIBLE);
            //start Payment APi  ...
            StartPaymentReq startPaymentReq = new StartPaymentReq(idValue,Const.ANDROID);
            Call<StartPaymentResp> call = retrofitAPI.startPayment("Bearer " + accessToken, startPaymentReq);
            call.enqueue(new Callback<StartPaymentResp>() {
                @Override
                public void onResponse(Call<StartPaymentResp> call, Response<StartPaymentResp> response) {
                    progressSubscriptionPlan.setVisibility(View.GONE);
                    if (response.code()==Const.SUCCESS_CODE_200) {
                        if (response.body().getPayment_url() != null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.body().getPayment_url()));
                            startActivity(browserIntent);
                        }
                    }
                }
                @Override
                public void onFailure(Call<StartPaymentResp> call, Throwable t) {
                    progressSubscriptionPlan.setVisibility(View.GONE);
                    Log.i("Error: ", "" + t.getMessage());
                }
            });
        } else
           AppUtils.showInternetDialog(mContext);
    }

    @Override
    public void onBackPressed() {
        AppUtils.navigateScreen((Activity) mContext,PcView.class);
        finishAffinity();
    }


}

