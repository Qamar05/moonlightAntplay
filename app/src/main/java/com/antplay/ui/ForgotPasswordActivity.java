package com.antplay.ui;

import static com.antplay.utils.Const.emailPattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.models.ResultResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends Activity {
    EditText edtEmail;
    Button btnSignUp;
    String strEmail;
    ProgressBar  progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        edtEmail =  findViewById(R.id.edtEmail);
        btnSignUp =  findViewById(R.id.btnSignUp);
        progressBar =  findViewById(R.id.progressSignUp);

        btnSignUp.setOnClickListener(view -> {
            validateFormField();
            if (validateFormField()) {
                strEmail = edtEmail.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                ResetEmailReq resetEmailReq = new ResetEmailReq(strEmail);
                RetrofitAPI retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
                Call<ResultResponse> call = retrofitAPI.forgotPassword(resetEmailReq);
                call.enqueue(new Callback<ResultResponse>() {
                    @Override
                    public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if(response.body().isStatus()) {
                            Toast.makeText(ForgotPasswordActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }


                    }

                    @Override
                    public void onFailure(Call<ResultResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.i("Error: ", "" + t.getMessage());
                    }
                });
            }
        });


    }

    private boolean validateFormField() {
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

        return true;
    }
}