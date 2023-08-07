package com.antplay.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antplay.R;

public class MyDialog implements Runnable {
    private final String title;
    private final String message;
    private final Activity activity;
    private final Runnable runOnDismiss;

//    private AlertDialog alert;

    private static final ArrayList<MyDialog> rundownDialogs = new ArrayList<>();

    private MyDialog(Activity activity, String title, String message, Runnable runOnDismiss)
    {
        this.activity = activity;
        this.title = title;
        this.message = message;
        this.runOnDismiss = runOnDismiss;
    }

    public static void closeDialogs()
    {
        synchronized (rundownDialogs) {
            for (MyDialog d : rundownDialogs) {
//                if (d.alert.isShowing()) {
//                    d.alert.dismiss();
//                }
            }

            rundownDialogs.clear();
        }
    }

    public static void displayDialog(final Activity activity, String title, String message, final boolean endAfterDismiss)
    {
        activity.runOnUiThread(new MyDialog(activity, title, message, new Runnable() {
            @Override
            public void run() {
                if (endAfterDismiss) {
                    activity.finish();
                }
            }
        }));
    }

    public static void displayDialog(Activity activity, String title, String message, Runnable runOnDismiss)
    {
        activity.runOnUiThread(new MyDialog(activity, title, message, runOnDismiss));
    }

    @Override
    public void run() {
        // If we're dying, don't bother creating a dialog
        if (activity.isFinishing())
            return;

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleText =  dialog.findViewById(R.id.titleText);
        TextView msgText =  dialog.findViewById(R.id.msgText);
        Button txtNo = dialog.findViewById(R.id.txtNo);
        Button txtYes = dialog.findViewById(R.id.txtYes);
        titleText.setText(title);
        msgText.setText(message);
        txtNo.setText("Help");
        txtYes.setText("Ok");
        txtYes.setOnClickListener(view -> {
            synchronized (rundownDialogs) {
                rundownDialogs.remove(MyDialog.this);
                dialog.dismiss();
            }
            runOnDismiss.run();
        });

        txtNo.setOnClickListener(view -> {
            synchronized (rundownDialogs) {
                rundownDialogs.remove(MyDialog.this);
                dialog.dismiss();
            }
            runOnDismiss.run();
          //  HelpLauncher.launchTroubleshooting(activity);
        });

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.findViewById(R.id.txtYes);
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            button.requestFocus();
        });

        synchronized (rundownDialogs) {
            rundownDialogs.add(this);
            dialog.show();
        }
        dialog.show();




    }

}
