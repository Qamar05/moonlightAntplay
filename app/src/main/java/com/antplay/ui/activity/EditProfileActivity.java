package com.antplay.ui.activity;


import static com.antplay.utils.Const.emailPattern;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.UserDetailsModal;
import com.antplay.models.UserUpdateRequestModal;
import com.antplay.models.UserUpdateResponseModal;
import com.antplay.ui.adapter.StateListAdapter;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, StateListAdapter.ButtonClickListener {
    private String TAG = "ANT_PLAY";
    LinearLayout linearLayout;
    EditText edTxtName, edTxtUserName, edTxtPhoneNumber, edTxtEmail, edTxtAge , edTxtCity, edTxtAddress, editTextPinCode;
   TextView edTxtState;
    Button buttonUpdateProfile;

    Spinner spinnerStateList;
    private ProgressBar progressBar;
    List<String> stateList;

    String access_token, email, phoneNumber;
    String city,address,state,pinCode;
    RetrofitAPI retrofitAPI;
    Context mContext;
    Dialog dialog;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));
        mContext = EditProfileActivity.this;

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
        edTxtState = findViewById(R.id.edTxtState);
        editTextPinCode = findViewById(R.id.edTxtPinCode);

        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);
        linearLayout = (LinearLayout) findViewById(R.id.back_linear_edit);


        linearLayout.setOnClickListener(this);
        buttonUpdateProfile.setOnClickListener(this);
        edTxtState.setOnClickListener(this);

        stateList = new ArrayList<>();
        stateList = AppUtils.stateList();
        getUserDetails();

    }


    private void setData(UserDetailsModal userDetail) {
        if (userDetail.getFirstName() != null || userDetail.getLastName() != null)
            edTxtName.setText(userDetail.getFirstName() + " " + userDetail.getLastName());

        if (userDetail.getUsername() != null)
            edTxtUserName.setText(userDetail.getUsername());

        if (userDetail.getPhoneNumber() != null)
            edTxtPhoneNumber.setText(userDetail.getPhoneNumber());

        if (userDetail.getEmail() != null)
            edTxtEmail.setText(userDetail.getEmail());

        if (userDetail.getState() != null)
            edTxtState.setText(userDetail.getState());

        if (userDetail.getCity() != null)
            edTxtCity.setText(userDetail.getCity());

        if (userDetail.getAddress() != null)
            edTxtAddress.setText(userDetail.getAddress());

        if (userDetail.getPhoneNumber() != null)
            editTextPinCode.setText(userDetail.getPincode());

    }


    private void updateUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        UserUpdateRequestModal updateRequestModal = new UserUpdateRequestModal(
                edTxtEmail.getText().toString().trim(),
                edTxtPhoneNumber.getText().toString().trim(),
                edTxtAddress.getText().toString().trim(),
                edTxtState.getText().toString(),
                edTxtCity.getText().toString().trim(),
                editTextPinCode.getText().toString());
        Call<UserUpdateResponseModal> call = retrofitAPI.userUpdate("Bearer " + access_token, updateRequestModal);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserUpdateResponseModal> call, Response<UserUpdateResponseModal> response) {
                if (response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (response.code() == Const.SUCCESS_CODE_200) {
                        progressBar.setVisibility(View.GONE);
                        getUserDetails();
                    }
                }
            }
            @Override
            public void onFailure(Call<UserUpdateResponseModal> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, Const.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserDetails() {
        if(AppUtils.isOnline(mContext)) {
            Call<UserDetailsModal> call = retrofitAPI.getUserDetails("Bearer " + access_token);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<UserDetailsModal> call, Response<UserDetailsModal> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        UserDetailsModal userDetailsModal = response.body();
                        city =  userDetailsModal.getCity();
                        address =  userDetailsModal.getAddress();
                        state  =  userDetailsModal.getState();
                        pinCode  =   userDetailsModal.getPincode();
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
        else
            Toast.makeText(mContext, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_linear_edit:
                finish();
                break;
            case R.id.edTxtState:
                openStateDialog();
                break;
            case R.id.buttonUpdateProfile:
                if(edTxtCity.getText().toString().equalsIgnoreCase(city)
                        && edTxtAddress.getText().toString().equalsIgnoreCase(address)
                        && edTxtState.getText().toString().equalsIgnoreCase(state)
                        && editTextPinCode.getText().toString().equalsIgnoreCase(pinCode)){
                    Toast.makeText(mContext, "There is no change to update", Toast.LENGTH_SHORT).show();
            }
            else if (validateFormField()) {
                if(AppUtils.isOnline(mContext))
                    updateUserProfile();
                else
                    AppUtils.showInternetDialog(mContext);
                }
                break;
        }
    }

    private void openStateDialog() {
        dialog = new Dialog(EditProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.state_dialog_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rvStateList =(RecyclerView) dialog.findViewById(R.id.rvStateList);
        StateListAdapter.ButtonClickListener buttonClickListener = EditProfileActivity.this;
        if(stateList!=null) {
            StateListAdapter adapter = new StateListAdapter(mContext, stateList, buttonClickListener);
            rvStateList.setAdapter(adapter);
        }
        dialog.show();
    }

    @Override
    public void onButtonClick(int position) {
        dialog.dismiss();
        edTxtState.setText(stateList.get(position));
    }

    private boolean validateFormField() {
        if (edTxtPhoneNumber.getText().toString().contains(" ")) {
            edTxtPhoneNumber.setError(getString(R.string.remove_whitespace));
            return false;
        }
        if (edTxtPhoneNumber.length() == 0) {
            edTxtPhoneNumber.setError(getString(R.string.error_phone));
            return false;
        }
        if (edTxtEmail.getText().toString().contains(" ")) {
            edTxtEmail.setError(getString(R.string.remove_whitespace));
            return false;
        }
         if (edTxtEmail.length() == 0) {
            edTxtEmail.setError(getString(R.string.error_email));
            return false;
        }  if (!edTxtEmail.getText().toString().matches(emailPattern)) {
            edTxtEmail.setError(getString(R.string.error_invalidEmail));
            return false;
        }
         if (edTxtCity.length() == 0) {
            edTxtCity.setError(getString(R.string.city_error));
            return false;
        }
        if (edTxtAddress.length() == 0) {
            edTxtAddress.setError(getString(R.string.address_error));
            return false;
        }
        if (edTxtState.length() == 0) {
            edTxtState.setError(getString(R.string.error_state));
            return false;
        }
        if (editTextPinCode.getText().toString().trim().contains(" ")) {
            editTextPinCode.setError(getString(R.string.remove_whitespace));
            return false;
        }
        if (editTextPinCode.getText().toString().trim().length() == 0) {
            editTextPinCode.setError(getString(R.string.error_pinCode));
            return false;
        }

        return true;
    }
}