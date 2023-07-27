package com.antplay.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.AllBillingPlanResp;
import com.antplay.models.BillingDataList;
import com.antplay.models.StartPaymentReq;
import com.antplay.models.StartPaymentResp;
import com.antplay.models.UserViewResponse;
import com.antplay.ui.adapter.SubscriptionPlanAdapter;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionPlanActivity extends Activity implements SubscriptionPlanAdapter.ButtonClickListener {
    RecyclerView rvSubscriptionPlans;
    SubscriptionPlanAdapter adapter;
    List<BillingDataList> planList;
    ProgressBar progressSubscriptionPlan;
    ImageView imgBack;

    TextView tvNoDataFound;
    String accessToken;
    SubscriptionPlanAdapter.ButtonClickListener buttonClickListener;
    RetrofitAPI retrofitAPI;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        mContext = SubscriptionPlanActivity.this;
        rvSubscriptionPlans = findViewById(R.id.rvSubscriptionPlans);
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        accessToken = SharedPreferenceUtils.getString(SubscriptionPlanActivity.this, Const.ACCESS_TOKEN);
        progressSubscriptionPlan = findViewById(R.id.progressSubscriptionPlan);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        imgBack = findViewById(R.id.imgBack);
        buttonClickListener = this;

        getPlanApi();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getPlanApi() {
        if (AppUtils.isOnline(mContext)) {
            progressSubscriptionPlan.setVisibility(View.VISIBLE);
            Call<AllBillingPlanResp> call = retrofitAPI.getBillingPlan("Bearer " + accessToken,"android");
            call.enqueue(new Callback<AllBillingPlanResp>() {
                @Override
                public void onResponse(Call<AllBillingPlanResp> call, Response<AllBillingPlanResp> response) {
                    progressSubscriptionPlan.setVisibility(View.GONE);
                    if (response != null) {
                        planList = response.body().getData();
                        if (planList.size() > 0) {
                            adapter = new SubscriptionPlanAdapter(SubscriptionPlanActivity.this, planList, buttonClickListener);
                            rvSubscriptionPlans.setAdapter(adapter);
                        } else {
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            tvNoDataFound.setText(getString(R.string.noDataFound));
                        }
                    }
                }
                @Override
                public void onFailure(Call<AllBillingPlanResp> call, Throwable t) {
                    progressSubscriptionPlan.setVisibility(View.GONE);
                    Log.i("Error: ", "" + t.getMessage());
                }
            });
        } else
            Toast.makeText(mContext, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClick(int idValue) {
        if (AppUtils.isOnline(mContext)) {
            progressSubscriptionPlan.setVisibility(View.VISIBLE);
            //start Payment APi  ...
            StartPaymentReq startPaymentReq = new StartPaymentReq(idValue);
            Call<StartPaymentResp> call = retrofitAPI.startPayment("Bearer " + accessToken, startPaymentReq);
            call.enqueue(new Callback<StartPaymentResp>() {
                @Override
                public void onResponse(Call<StartPaymentResp> call, Response<StartPaymentResp> response) {
                    progressSubscriptionPlan.setVisibility(View.GONE);
                    if (response != null) {
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
            Toast.makeText(mContext, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
    }
}

