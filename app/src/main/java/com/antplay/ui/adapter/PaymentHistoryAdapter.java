package com.antplay.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antplay.R;
import com.antplay.models.Payment;


import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.MyViewHolder> {

    private Context context;
    List<Payment> paymentHistory_modals;
    private static int currentPosition = 0;

    public PaymentHistoryAdapter(Context context, List<Payment> paymentHistory_modals) {
        this.context = context;
        this.paymentHistory_modals = paymentHistory_modals;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paymenthistory_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Payment modal = paymentHistory_modals.get(position);

        holder.tv_planName.setText(modal.getBillingPlan());
        holder.tv_amountUp.setText(String.valueOf(modal.getBillingPrice()));
        holder.tv_plan.setText(modal.getBillingPlan());
        holder.tv_transactionId.setText(modal.getPaymentId());
        holder.tv_amount.setText(String.valueOf(modal.getBillingPrice()));
        holder.tv_status.setText(String.valueOf(modal.getPaymentStatus()));


        String currentString = modal.getPaymentDate();
        String[] separated = currentString.split("T");
        String s = separated[0];
        holder.tv_trans_date.setText(s);
        holder.linearLayout.setVisibility(View.GONE);
        holder.img_forward.setRotation(0);
        holder.img_forward.setVisibility(View.VISIBLE);

        if (currentPosition == position) {
            //creating an animation
            Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);

            //toggling visibility
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.img_forward.setRotation(90);


            //adding sliding effect
            holder.linearLayout.startAnimation(slideDown);
        }

        holder.linearLayout_upper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getting the position of the item to expand it
                currentPosition = position;

                //reloding the list
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return paymentHistory_modals.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_planName, tv_transactionId, tv_status, tv_amount, tv_plan, tv_amountUp, tv_trans_date;
        LinearLayout linearLayout, linearLayout_upper;
        ImageView  img_forward;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_transactionId = itemView.findViewById(R.id.transactionId);
            tv_amount = itemView.findViewById(R.id.amount);
            tv_status = itemView.findViewById(R.id.status);
            tv_planName = itemView.findViewById(R.id.planName);
            tv_plan = itemView.findViewById(R.id.plan_Name);
            tv_amountUp = itemView.findViewById(R.id.tv_amount_up);
            tv_trans_date = itemView.findViewById(R.id.trans_date);
            linearLayout = itemView.findViewById(R.id.linearLayout_payment_adapter);
            linearLayout_upper = itemView.findViewById(R.id.linearUpper_paymentHistory_adapter);
            img_forward = itemView.findViewById(R.id.img_forward);


        }
    }
}
