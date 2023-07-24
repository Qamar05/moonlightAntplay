package com.antplay.utils;

import static com.antplay.utils.Const.emailPattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.antplay.R;
import com.antplay.ui.activity.LoginActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class AppUtils {

    public static void showToast(String msg, Context ctx) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public static void showSnack(View view, int color, String message, Context context){
        Snackbar sb = Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_LONG);
        sb.getView().setBackgroundColor(ContextCompat.getColor(context, color));
        sb.show();
    }

    public static void navigateScreen(Activity activity, Class loginActivityClass){
        Intent i = new Intent(activity,loginActivityClass);
        activity.startActivity(i);
        activity.finish();
    }

    public static void navigateScreenWithoutFinish(Activity activity, Class loginActivityClass){
        Intent i = new Intent(activity,loginActivityClass);
        activity.startActivity(i);
    }

    public static void navigateScreenSendValue(Activity activity, Class loginActivityClass ,String key , String value){
        Intent i = new Intent(activity,loginActivityClass);
        i.putExtra(key,value);
        activity.startActivity(i);
    }
    public static boolean validateEmailField(Context mContext,EditText edtEmail) {
        if (edtEmail.getText().toString().contains(" ")) {
            edtEmail.setError(mContext.getString(R.string.remove_whitespace));
            return false;
        } else if (edtEmail.length() == 0) {
            edtEmail.setError(mContext.getString(R.string.error_email));
            return false;
        } else if (!edtEmail.getText().toString().matches(emailPattern)) {
            edtEmail.setError(mContext.getString(R.string.error_invalidEmail));
            return false;
        }

        return true;
    }


    public static boolean isOnline(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
