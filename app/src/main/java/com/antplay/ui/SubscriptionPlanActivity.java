package com.antplay.ui;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.AllBillingPlanResp;
import com.antplay.models.BillingDataList;
import com.antplay.ui.adapter.SubscriptionPlanAdapter;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionPlanActivity extends Activity {
    RecyclerView rvSubscriptionPlans;
    SubscriptionPlanAdapter adapter;
    List<BillingDataList> planList;
    ProgressBar progressSubscriptionPlan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        rvSubscriptionPlans = findViewById(R.id.rvSubscriptionPlans);
        progressSubscriptionPlan = findViewById(R.id.progressSubscriptionPlan);

        getPlanApi();
        adapter = new SubscriptionPlanAdapter(SubscriptionPlanActivity.this, planList);
        rvSubscriptionPlans.setAdapter(adapter);


    }

    private void getPlanApi() {
        progressSubscriptionPlan.setVisibility(View.VISIBLE);
        RetrofitAPI retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        String accessToken = SharedPreferenceUtils.getString(SubscriptionPlanActivity.this, Const.ACCESS_TOKEN);
        Call<AllBillingPlanResp> call = retrofitAPI.getBillingPlan(accessToken);
        call.enqueue(new Callback<AllBillingPlanResp>() {
            @Override
            public void onResponse(Call<AllBillingPlanResp> call, Response<AllBillingPlanResp> response) {
                progressSubscriptionPlan.setVisibility(View.GONE);
                planList    =  response.body().getData();

            }

            @Override
            public void onFailure(Call<AllBillingPlanResp> call, Throwable t) {
                progressSubscriptionPlan.setVisibility(View.GONE);
                Log.i("Error: ", "" + t.getMessage());
            }

        });

    }
}

