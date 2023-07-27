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
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antplay.R;
import com.antplay.models.Payment;
import com.skydoves.powermenu.MenuBaseAdapter;

import java.util.List;

public class StateListAdapter extends RecyclerView.Adapter<StateListAdapter.MyViewHolder> {
    private Context mContext;
    List<String> stateList;
    ButtonClickListener buttonClickListener;

    public StateListAdapter(Context mContext, List<String> stateList, ButtonClickListener buttonClickListener) {
        this.mContext = mContext;
        this.stateList = stateList;
        this.buttonClickListener =  buttonClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.state_item_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StateListAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvStateName.setText(stateList.get(position));
        holder.rlState.setOnClickListener(view -> buttonClickListener.onButtonClick(position));
    }

    @Override
    public int getItemCount() {
        return stateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvStateName;
        RelativeLayout rlState;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStateName = itemView.findViewById(R.id.tvStateName);
            rlState = itemView.findViewById(R.id.rlState);


        }
    }
    public interface ButtonClickListener {
        void onButtonClick(int value);
    }
}