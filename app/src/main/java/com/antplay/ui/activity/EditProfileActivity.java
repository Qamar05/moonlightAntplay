package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.UserDetailsModal;
import com.antplay.models.UserUpdateRequestModal;
import com.antplay.models.UserUpdateResponseModal;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends Activity implements View.OnClickListener {

    private String TAG = "ANT_PLAY";
    LinearLayout linearLayout;
    EditText edTxtName, edTxtUserName, edTxtPhoneNumber, edTxtEmail, edTxtAge, edTxtCity, edTxtAddress, edTxtState, editTextPinCode;
    Button buttonUpdateProfile;
    Spinner spinnerStateList;
    private ProgressBar progressBar;
    List<String> stateList;
    String st_state;
    String access_token , email, phoneNumber;
    RetrofitAPI retrofitAPI;
    Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));
        mContext =  EditProfileActivity.this;
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        access_token = SharedPreferenceUtils.getString(EditProfileActivity.this, Const.ACCESS_TOKEN);
        email = SharedPreferenceUtils.getString(EditProfileActivity.this, Const.EMAIL_ID);
        phoneNumber = SharedPreferenceUtils.getString(EditProfileActivity.this, Const.PHONE_NUMBER);

        progressBar = (ProgressBar) findViewById(R.id.progressBarEditProfile);
        edTxtName = findViewById(R.id.edTxtFullName);
        edTxtUserName = findViewById(R.id.edTxtUserName);
        edTxtPhoneNumber = findViewById(R.id.edTxtPhoneNumber);
        edTxtEmail = findViewById(R.id.edTxtEmail);
        edTxtAge = findViewById(R.id.edTxtAge);
        edTxtCity = findViewById(R.id.edTxtCity);
        edTxtAddress = findViewById(R.id.edTxtAddress);
        editTextPinCode = findViewById(R.id.edTxtPinCode);
        spinnerStateList = findViewById(R.id.spinnerStateList);
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);
        linearLayout = (LinearLayout) findViewById(R.id.back_linear_edit);


        linearLayout.setOnClickListener(this);
        buttonUpdateProfile.setOnClickListener(this);

        setStateListAdapter();
        getUserDetails();

    }



    private void setStateListAdapter() {
        stateList = new ArrayList<String>();
        stateList =  AppUtils.stateList();
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStateList.setAdapter(stateAdapter);
        spinnerStateList.setPrompt("your state here");
        spinnerStateList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st_state = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setData(UserDetailsModal userDetail) {
        if (userDetail.getFirstName()!= null || userDetail.getLastName()!= null)
            edTxtName.setText(userDetail.getFirstName() + " " + userDetail.getLastName());

        if (userDetail.getUsername() != null)
            edTxtUserName.setText(userDetail.getUsername());

        if (userDetail.getPhoneNumber() != null)
            edTxtPhoneNumber.setText(userDetail.getPhoneNumber());

        if (userDetail.getEmail() != null)
            edTxtEmail.setText(userDetail.getEmail());

        if (userDetail.getCity() != null)
            edTxtCity.setText(userDetail.getCity());

        if (userDetail.getAddress() != null)
            edTxtAddress.setText(userDetail.getAddress());

        if (userDetail.getPhoneNumber() != null)
            editTextPinCode.setText(userDetail.getPincode());

        for (int i = 0; i < stateList.size(); i++) {
            if (stateList.get(i).equals(userDetail.getState()))
                spinnerStateList.setSelection(i);
        }
    }


    private void updateUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        UserUpdateRequestModal updateRequestModal = new UserUpdateRequestModal(email,phoneNumber,
                edTxtAddress.getText().toString().trim(),
                st_state,
                edTxtCity.getText().toString().trim(),
                editTextPinCode.getText().toString());
        Call<UserUpdateResponseModal> call = retrofitAPI.userUpdate("Bearer " + access_token, updateRequestModal);
        call.enqueue(new Callback<UserUpdateResponseModal>() {
            @Override
            public void onResponse(Call<UserUpdateResponseModal> call, Response<UserUpdateResponseModal> response) {
                if (response.code() == Const.SUCCESS_CODE_200) {
                    progressBar.setVisibility(View.GONE);
                    getUserDetails();
                    Toast.makeText(EditProfileActivity.this, Const.profile_updated_success, Toast.LENGTH_SHORT).show();
//                    AppUtils.showSnack(getWindow().getDecorView().getRootView(), R.color.black, Const.profile_updated_success, EditProfileActivity.this);

                } else if (response.code() == Const.ERROR_CODE_400 || response.code() == Const.ERROR_CODE_500) {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Else condition");
                    Toast.makeText(EditProfileActivity.this, Const.enter_valid_data, Toast.LENGTH_SHORT).show();
//                    AppUtils.showSnack(getWindow().getDecorView().getRootView(), R.color.black, Const.enter_valid_data, EditProfileActivity.this);
                }  else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditProfileActivity.this, Const.something_went_wrong, Toast.LENGTH_SHORT).show();
//                    AppUtils.showSnsssack(getWindow().getDecorView().getRootView(), R.color.black, Const.something_went_wrong, EditProfileActivity.this);
                }
            }

            @Override
            public void onFailure(Call<UserUpdateResponseModal> call, Throwable t) {
                Log.d("BILLING_PLAN", "Failure");
                progressBar.setVisibility(View.GONE);
                AppUtils.showSnack(getWindow().getDecorView().getRootView(), R.color.black, Const.something_went_wrong, EditProfileActivity.this);

            }
        });
    }

    private void getUserDetails() {
        Call<UserDetailsModal> call = retrofitAPI.getUserDetails("Bearer " + access_token);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserDetailsModal> call, Response<UserDetailsModal> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    SharedPreferenceUtils.saveString(mContext, Const.FIRSTNAME, response.body().getFirstName());
                    SharedPreferenceUtils.saveString(mContext, Const.LASTNAME, response.body().getLastName());
                    SharedPreferenceUtils.saveString(mContext, Const.EMAIL_ID, response.body().getEmail());
                    SharedPreferenceUtils.saveString(mContext, Const.PHONE_NUMBER, response.body().getPhoneNumber());
                    SharedPreferenceUtils.saveString(mContext, Const.ADDRESS, response.body().getAddress());
                    SharedPreferenceUtils.saveString(mContext, Const.STATE, response.body().getState());
                    SharedPreferenceUtils.saveString(mContext, Const.CITY, response.body().getCity());
                    SharedPreferenceUtils.saveString(mContext, Const.USERNAME, response.body().getUsername());
                    SharedPreferenceUtils.saveString(mContext, Const.PINCODE, response.body().getPincode());
                    setData(response.body());

                } else {
                    progressBar.setVisibility(View.GONE);
                    AppUtils.showToast(Const.no_records, EditProfileActivity.this);
                }
            }

            @Override
            public void onFailure(Call<UserDetailsModal> call, Throwable t) {
                Log.e(TAG, "" + t);
                progressBar.setVisibility(View.GONE);
                AppUtils.showToast(Const.something_went_wrong, EditProfileActivity.this);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_linear_edit:
                finish();
                break;
            case R.id.buttonUpdateProfile:
                if (edTxtCity.getText().toString().trim().length() == 0) {
                    AppUtils.showSnack(view, R.color.black, Const.city_should_not_empty, mContext);
                } else if (edTxtAddress.getText().toString().trim().length() == 0) {
                    AppUtils.showSnack(view, R.color.black, Const.address_should_not_empty, mContext);
                } else if (!editTextPinCode.getText().toString().matches(Const.pinCodeRegex)) {
                    AppUtils.showSnack(view, R.color.black, Const.enter_valid_picCode, mContext);
                } else {
                    updateUserProfile();
                }
                    break;
        }

    }
}