package com.antplay.ui.activity;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.MessageResponse;
import com.antplay.models.VMTimerReq;
import com.antplay.ui.intrface.ClearService;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApplication extends Application {

    private boolean isAppInForeground = false;
    RetrofitAPI retrofitAPI;
    String accessToken;


    @Override
    public void onCreate() {
        super.onCreate();
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        accessToken = SharedPreferenceUtils.getString(getApplicationContext(), Const.ACCESS_TOKEN);
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    public boolean isAppInForeground() {
        return isAppInForeground;
    }

    private class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            // Activity created
            Log.i("test_application", "test1");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            // Activity started
            isAppInForeground = true;
            Log.i("test_application", "test2");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            // Activity resumed
            Log.i("test_application", "test23");
        }
        @Override
        public void onActivityPaused(Activity activity) {
            // Activity paused
            Log.i("test_application", "test4");
        }
        @Override
        public void onActivityStopped(Activity activity) {
            // Activity stopped
            isAppInForeground = false;
            Log.i("test_application", "test5");
        }
        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            // Save instance state
            Log.i("test_application", "test6" + outState.toString());
        }
        @Override
        public void onActivityDestroyed(Activity activity) {
            // Activity destroyed
          //      getVMForShutDown();
        }
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            Log.i("test_application", "test8");
            // This callback indicates that the app's UI is no longer visible.
            // This could indicate that the app is being backgrounded or minimized.
            // You can use this as a hint to release resources if necessary.
        }
    }
    private void shutDownVM(String strVMId) {
        VMTimerReq vmTimerReq = new VMTimerReq(strVMId);
        Call<MessageResponse> call = retrofitAPI.shutDownVm("Bearer " + accessToken, vmTimerReq);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

                if (response.code() == 200) {
                    SharedPreferenceUtils.saveBoolean(getApplicationContext(), Const.IS_SHUT_DOWN, true);
                    SharedPreferenceUtils.saveBoolean(getApplicationContext(), Const.IS_VM_DISCONNECTED, false);

                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                AppUtils.showToast(Const.something_went_wrong, getApplicationContext());
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