package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.Payment;
import com.antplay.models.PaymentHistory_modal;
import com.antplay.ui.adapter.PaymentHistory_Adapter;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentHistoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Payment> paymentHistory_modals;
    LinearLayout linear_back;
    private ProgressBar loadingPB;
    Context mContext;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        mContext  =  PaymentHistoryActivity.this;
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));

        recyclerView=(RecyclerView) findViewById(R.id.recyclerView_payment);
        linear_back= (LinearLayout) findViewById(R.id.back_linear_payment);
        loadingPB = (ProgressBar) findViewById(R.id.loading_getPaymentHistory);
        linear_back.setOnClickListener(v -> finish());

        paymentHistory_modals = new ArrayList<Payment>();

        callPaymentHistoryAPI();


    }


    private void callPaymentHistoryAPI() {
        if(AppUtils.isOnline(mContext)){
            loadingPB.setVisibility(View.VISIBLE);
            RetrofitAPI retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
            Call<PaymentHistory_modal> call = retrofitAPI.getPaymentHistory("Bearer " + SharedPreferenceUtils.getString(PaymentHistoryActivity.this, Const.ACCESS_TOKEN));
            call.enqueue(new Callback<PaymentHistory_modal>() {
                @Override
                public void onResponse(Call<PaymentHistory_modal> call, Response<PaymentHistory_modal> response) {

                    if (response.isSuccessful()) {
                        loadingPB.setVisibility(View.GONE);
                        paymentHistory_modals = response.body().getData();
                        PaymentHistory_Adapter paymentHistory_adapter = new PaymentHistory_Adapter(PaymentHistoryActivity.this, paymentHistory_modals);
                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(PaymentHistoryActivity.this, 1);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(paymentHistory_adapter);

                    } else {
                        loadingPB.setVisibility(View.GONE);
                        AppUtils.showToast(Const.no_records, PaymentHistoryActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<PaymentHistory_modal> call, Throwable t) {
                    Log.e("Hello Get VM", "Failure");
                    // loadingPB.setVisibility(View.GONE);
                    AppUtils.showToast(Const.something_went_wrong, PaymentHistoryActivity.this);

                }
            });
        }
        else
            Toast.makeText(mContext, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();

    }

}