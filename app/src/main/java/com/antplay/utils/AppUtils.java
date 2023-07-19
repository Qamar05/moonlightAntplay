package com.antplay.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

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

}
