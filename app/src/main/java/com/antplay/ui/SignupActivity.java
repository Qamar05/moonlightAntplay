package com.antplay.ui;

import static com.antplay.utils.Const.emailPattern;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.antplay.R;
import com.antplay.utils.Const;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends Activity {

    EditText edtFirstName, edtLastName, edtPhoneNumber, edtEmail, edtPassword, edtConfirmPassword, edtAge, edtAddress, edtCity, edtPinCode;
    Button btnSignup;
    TextView txtAlreadyRegister, txtUserAgreement;
    CheckBox chkBoxUserAgreement;
    ProgressBar progressBar;
    boolean isUserAgreementChecked = false,isSubscribed = false,isNewUser=true,lastLogin=false;
    Spinner spinnerState;
    String strState, strFirstName, strMiddleName, strLastName, strPhoneNumber, strEmail, strPassword, strAge, strAddress, strPinCode, strCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        edtAge = (EditText) findViewById(R.id.edtAge);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtPinCode = (EditText) findViewById(R.id.edtPinCode);
        spinnerState = (Spinner) findViewById(R.id.spinnerState);
        edtCity = (EditText) findViewById(R.id.edtCity);
        progressBar = (ProgressBar) findViewById(R.id.progressSignUp);

        btnSignup = (Button) findViewById(R.id.btnSignUp);
        txtAlreadyRegister = (TextView) findViewById(R.id.txtAlreadyRegister);
        chkBoxUserAgreement = (CheckBox) findViewById(R.id.chkBoxUserAgreement);
        txtUserAgreement = findViewById(R.id.txtUserAgreement);

        chkBoxUserAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUserAgreementChecked = chkBoxUserAgreement.isChecked();
            }
        });

        txtAlreadyRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        txtUserAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open WebView here
                Intent intent = new Intent(SignupActivity.this, GeneralWebViewActivity.class);
                intent.putExtra(Const.REDIRECT_URL, Const.TERMS_AND_CONDITION_URL);
                startActivity(intent);
            }
        });

        // Spinner Drop down elements
        List<String> stateList = new ArrayList<String>();
        stateList.add("Andaman and Nicobar Islands");
        stateList.add("Andhra Pradesh");
        stateList.add("Arunachal Pradesh");
        stateList.add("Assam");
        stateList.add("Bihar");
        stateList.add("Chandigarh");
        stateList.add("Chhattisgarh");
        stateList.add("Dadra and Nagar Haveli");
        stateList.add("Daman and Diu");
        stateList.add("Delhi");
        stateList.add("Goa");
        stateList.add("Gujarat");
        stateList.add("Haryana");
        stateList.add("Himachal Pradesh");
        stateList.add("Jammu and Kashmir");
        stateList.add("Jharkhand");
        stateList.add("Karnataka");
        stateList.add("Kerala");
        stateList.add("Ladakh");
        stateList.add("Lakshadweep");
        stateList.add("Madhya Pradesh");
        stateList.add("Maharashtra");
        stateList.add("Manipur");
        stateList.add("Meghalaya");
        stateList.add("Mizoram");
        stateList.add("Nagaland");
        stateList.add("Odisha");
        stateList.add("Puducherry");
        stateList.add("Punjab");
        stateList.add("Rajasthan");
        stateList.add("Sikkim");
        stateList.add("Tamil Nadu");
        stateList.add("Telangana");
        stateList.add("Tripura");
        stateList.add("Uttar Pradesh");
        stateList.add("Uttarakhand");
        stateList.add("West Bengal");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerState.setAdapter(dataAdapter);

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strState = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFormField();

                //  Integer intValue = myLong.intValue();
                if (validateFormField()) {
                    // we can call Api here
                    strFirstName = edtFirstName.getText().toString().trim();
                    strLastName = edtLastName.getText().toString().trim();
                    strEmail = edtEmail.getText().toString().trim();
                    strPhoneNumber = edtPhoneNumber.getText().toString().trim();
                    strMiddleName = edtFirstName.getText().toString().trim();

                    strPassword = edtPassword.getText().toString().trim();
                    strPassword = edtAddress.getText().toString();
                    strAge = edtAge.getText().toString().trim();
                    strCity = edtCity.getText().toString().trim();
                    strPinCode = edtPinCode.getText().toString();

                    callRegisterApi(strFirstName, strMiddleName,strLastName, strEmail, strPhoneNumber, lastLogin, isNewUser, isSubscribed, strPassword, strAddress, strAge, strState, strCity, strPinCode);

                }
            }
        });


    }

    private void callRegisterApi(String strFirstName, String strMiddleName, String strLastName, String strEmail, String strPhoneNumber, boolean lastLogin, boolean isNewUser, boolean isSubscribed, String strPassword, String strAddress, String strAge, String strState, String strCity, String strPinCode) {


    }


    private boolean validateFormField() {
        if (edtFirstName.getText().toString().contains(" ")) {
            edtFirstName.setError(getString(R.string.remove_whitespace));
            return false;
        }
        if (edtFirstName.length() == 0) {
            edtFirstName.setError(getString(R.string.error_firstname));
            return false;
        }
        if (edtLastName.getText().toString().contains(" ")) {
            edtLastName.setError(getString(R.string.remove_whitespace));
            return false;
        }

        if (edtLastName.length() == 0) {
            edtLastName.setError(getString(R.string.error_lastname));
            return false;
        }

        if (edtPhoneNumber.getText().toString().contains(" ")) {
            edtPhoneNumber.setError(getString(R.string.remove_whitespace));
            return false;
        }
        if (edtPhoneNumber.length() == 0) {
            edtPhoneNumber.setError(getString(R.string.error_phone));
            return false;
        }
        if (edtEmail.getText().toString().contains(" ")) {
            edtEmail.setError(getString(R.string.remove_whitespace));
            return false;
        } else if (edtEmail.length() == 0) {
            edtEmail.setError(getString(R.string.error_email));
            return false;
        } else if (!edtEmail.getText().toString().matches(emailPattern)) {
            edtEmail.setError(getString(R.string.error_invalidEmail));
            return false;
        }

        if (edtPassword.getText().toString().contains(" ")) {
            edtPassword.setError(getString(R.string.remove_whitespace));
            return false;
        } else if (edtPassword.length() == 0) {
            edtPassword.setError(getString(R.string.error_password));
            return false;
        } else if (edtPassword.length() < 8) {
            edtPassword.setError(getString(R.string.error_pass_minimum));
            return false;
        } else if (!edtPassword.getText().toString().matches(Const.passwordRegex)) {
            edtPassword.setError(getString(R.string.pass_regex));
            return false;
        }

        /*if (edtConfirmPassword.getText().toString().contains(" ")) {
            edtConfirmPassword.setError(getString(R.string.remove_whitespace));
            return false;
        } else if (edtConfirmPassword.length() == 0) {
            edtConfirmPassword.setError(getString(R.string.error_confirmPass));
            return false;
        } else if (edtConfirmPassword.length() < 8) {
            edtConfirmPassword.setError(getString(R.string.error_Confirmpass_minimum));
            return false;
        }*/
        if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            edtConfirmPassword.setError(getString(R.string.error_password_not_match));
            return false;
        }
        if (edtAge.getText().toString().trim().contains(" ")) {
            edtAge.setError(getString(R.string.remove_whitespace));
            return false;
        } else if (edtAge.length() == 0) {
            edtAge.setError(getString(R.string.error_age));
            return false;
        } else if (Integer.parseInt(edtAge.getText().toString().trim()) < 18) {
            edtAge.setError(getString(R.string.error_greater_18));
            return false;
        }
        if (edtAddress.getText().toString().trim().length() == 0) {
            edtAddress.setError(getString(R.string.error_address));
            return false;
        }
        if (edtCity.getText().toString().trim().length() == 0) {
            edtCity.setError(getString(R.string.error_city));
            return false;
        }

        if (edtPinCode.getText().toString().contains(" ")) {
            edtPinCode.setError(getString(R.string.remove_whitespace));
            return false;
        }
        if (edtPinCode.length() == 0) {
            edtPinCode.setError(getString(R.string.error_pinCode));
            return false;
        }

        if (!chkBoxUserAgreement.isChecked()) {
              Toast.makeText(this, getString(R.string.error_checkbox), Toast.LENGTH_SHORT).show();
            //AppUtils.showSnack(getWindow().getDecorView().getRootView(), R.color.black, getString(R.string.error_checkbox), RegisterActivity.this);
            return false;
        }
        return true;
    }
}