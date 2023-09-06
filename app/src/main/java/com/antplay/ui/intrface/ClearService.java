package com.antplay.ui.intrface;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.MessageResponse;
import com.antplay.models.VMTimerReq;
import com.antplay.ui.activity.PcView;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClearService extends Service {
    RetrofitAPI retrofitAPI;
    String accessToken;

    private static final int NOTIFICATION_ID = 123;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialization code for your service
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Your service logic

        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        accessToken = SharedPreferenceUtils.getString(getApplicationContext(), Const.ACCESS_TOKEN);
        // Create a notification for the foreground service

        return START_STICKY;
    }



    @Override
    public void onDestroy() {

        Log.i("testtt_killapp" , "testtt");
        // Cleanup and resource release
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//        getVMForShutDown();


    private void shutDownVM(String strVMId) {
        VMTimerReq vmTimerReq = new VMTimerReq(strVMId);
        Call<MessageResponse> call = retrofitAPI.shutDownVm("Bearer " + accessToken, vmTimerReq);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

                if (response.code() == 200) {
                    SharedPreferenceUtils.saveBoolean(getApplicationContext(), Const.IS_SHUT_DOWN, true);
                    SharedPreferenceUtils.saveBoolean(getApplicationContext(), Const.IS_VM_DISCONNECTED, false);
                    stopSelf();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                AppUtils.showToast(Const.something_went_wrong, getApplication());
            }
        });
    }
    private void stopVM(String strVMId) {
        VMTimerReq vmTimerReq = new VMTimerReq(strVMId);
        Call<MessageResponse> call = retrofitAPI.stopVM("Bearer " + accessToken , vmTimerReq);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.code()==200) {
                    SharedPreferenceUtils.saveBoolean(getApplicationContext(), Const.IS_SHUT_DOWN, true);
                    SharedPreferenceUtils.saveBoolean(getApplicationContext(), Const.IS_VM_DISCONNECTED, false);
                    stopSelf();
                }
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                AppUtils.showToast(Const.something_went_wrong, getApplicationContext());
            }
        });
    }

    public void getVMForShutDown() {
        Call<ResponseBody> call = retrofitAPI.getVMFromServer("Bearer " + accessToken);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try{
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        String strVMId = jsonArray.getJSONObject(0).getString("vmid");
                        String status = jsonArray.getJSONObject(0).getString("status");
                        String vmip = jsonArray.getJSONObject(0).getString("vmip");

                            if (status.equalsIgnoreCase("running")) {
                                if (vmip != null)
                                    shutDownVM(strVMId);
                                else
                                    stopVM(strVMId);
                        }
                    }
                    catch (Exception e){
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                AppUtils.showToast(Const.something_went_wrong, getApplicationContext());
            }
        });
    }
}
