package com.antplay.ui;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.AllBillingPlanResp;
import com.antplay.models.BillingDataList;
import com.antplay.models.ResultResponse;
import com.antplay.models.StartPaymentReq;
import com.antplay.models.StartPaymentResp;
import com.antplay.ui.adapter.SubscriptionPlanAdapter;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionPlanActivity extends Activity implements SubscriptionPlanAdapter.ButtonClickListener {
    RecyclerView rvSubscriptionPlans;
    SubscriptionPlanAdapter adapter;
    List<BillingDataList> planList;
    ProgressBar progressSubscriptionPlan;

    TextView tvNoDataFound;
    String accessToken;
    SubscriptionPlanAdapter.ButtonClickListener buttonClickListener;
    RetrofitAPI retrofitAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        rvSubscriptionPlans = findViewById(R.id.rvSubscriptionPlans);
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        accessToken = SharedPreferenceUtils.getString(SubscriptionPlanActivity.this, Const.ACCESS_TOKEN);
        progressSubscriptionPlan = findViewById(R.id.progressSubscriptionPlan);
        tvNoDataFound =  findViewById(R.id.tvNoDataFound);
        buttonClickListener =  this;
        getPlanApi();
    }

    private void getPlanApi() {
        progressSubscriptionPlan.setVisibility(View.VISIBLE);
        Call<AllBillingPlanResp> call = retrofitAPI.getBillingPlan("Bearer "+ accessToken);
        call.enqueue(new Callback<AllBillingPlanResp>() {
            @Override
            public void onResponse(Call<AllBillingPlanResp> call, Response<AllBillingPlanResp> response) {
                progressSubscriptionPlan.setVisibility(View.GONE);
                if(response!=null) {
                    planList = response.body().getData();
                    if(planList.size()>0) {
                        adapter = new SubscriptionPlanAdapter(SubscriptionPlanActivity.this, planList ,buttonClickListener);
                        rvSubscriptionPlans.setAdapter(adapter);
                    }
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
        progressSubscriptionPlan.setVisibility(View.VISIBLE);
        StartPaymentReq startPaymentReq =  new StartPaymentReq(idValue);
        Call<StartPaymentResp> call = retrofitAPI.startPayment("Bearer "+ accessToken ,startPaymentReq);
        call.enqueue(new Callback<StartPaymentResp>() {
            @Override
            public void onResponse(Call<StartPaymentResp> call, Response<StartPaymentResp> response) {
                progressSubscriptionPlan.setVisibility(View.GONE);
                if(response!=null) {

                   // Toast.makeText(SubscriptionPlanActivity.this, "" +response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<StartPaymentResp> call, Throwable t) {
                progressSubscriptionPlan.setVisibility(View.GONE);
                Log.i("Error: ", "" + t.getMessage());
            }
        });
    }
}

