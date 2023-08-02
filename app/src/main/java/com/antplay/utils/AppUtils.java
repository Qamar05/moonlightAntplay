package com.antplay.utils;

import static com.antplay.utils.Const.emailPattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.antplay.R;
import com.antplay.ui.activity.EditProfileActivity;
import com.antplay.ui.activity.LoginActivity;
import com.antplay.ui.adapter.StateListAdapter;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
        return (netInfo != null && netInfo.isConnected());
    }
    public  static ArrayList<String> stateList(){
        ArrayList<String> stateList =  new ArrayList<>();
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
        return stateList;
    }
    public static Dialog showInternetDialog(Context mContext){
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.internet_dialog_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button close =(Button) dialog.findViewById(R.id.txtClose);
        close.setOnClickListener(view -> {dialog.dismiss();});

        dialog.show();
    return dialog;
    }

}
