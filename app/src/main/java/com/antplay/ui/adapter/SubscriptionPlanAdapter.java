package com.antplay.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.antplay.R;
import com.antplay.models.BillingDataList;
import java.util.List;

public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.SubcriptionVieHolder> {

    List<BillingDataList> planList;
    Context context;
    ButtonClickListener buttonClickListener;

    public SubscriptionPlanAdapter(Context mContext , List<BillingDataList> planList ,ButtonClickListener buttonClickListener ) {
        this.planList = planList;
        this.context = mContext;
        this.buttonClickListener = buttonClickListener;
    }

    @Override
    public SubcriptionVieHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        View photoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_billing_plan, parent, false);
        SubcriptionVieHolder viewHolder = new SubcriptionVieHolder(photoView);
        return viewHolder;
    }


    @Override
    public void
    onBindViewHolder(final SubcriptionVieHolder viewHolder, final int position) {
        BillingDataList data =planList.get(position);
        viewHolder.txtPlaneName.setText(data.getPlan_name());
        viewHolder.txtPlaneAmount.setText(data.getPrice() + " Rs.");
        viewHolder.txtValidity.setText("Max days : " + data.getTerm());
        viewHolder.txtDurationInHours.setText(data.getHour_limit() + " Hours");
        viewHolder.tv_gpu.setText(data.getGpu());
        viewHolder.tv_cpu.setText(data.getCpu() + " CPU");
        viewHolder.tv_ram.setText(data.getRam() + " Ram");
        viewHolder.tv_ssd.setText(data.getSsd() + " SSD");

        viewHolder.txtSubscribe.setOnClickListener(view -> buttonClickListener.onButtonClick(data.getId()));

    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public static class SubcriptionVieHolder extends RecyclerView.ViewHolder {
        public TextView txtPlaneName,txtPlaneAmount,txtValidity,txtDurationInHours,tv_gpu,tv_cpu,
        tv_ram,tv_ssd,tv_mbp,txtSubscribe,active_plan;

        public SubcriptionVieHolder(View itemView) {
            super(itemView);
            this.txtPlaneName =  itemView.findViewById(R.id.txtPlaneName);
            this.txtPlaneAmount =  itemView.findViewById(R.id.txtPlaneAmount);
            this.txtValidity =  itemView.findViewById(R.id.txtValidity);
            this.txtDurationInHours =  itemView.findViewById(R.id.txtDurationInHours);
            this.tv_gpu =  itemView.findViewById(R.id.tv_gpu);
            this.tv_cpu =  itemView.findViewById(R.id.tv_cpu);
            this.tv_ram =  itemView.findViewById(R.id.tv_ram);
            this.tv_ssd =  itemView.findViewById(R.id.tv_ssd);
            this.tv_mbp =  itemView.findViewById(R.id.tv_mbp);
            this.txtSubscribe =  itemView.findViewById(R.id.txtSubscribe);
            this.active_plan =  itemView.findViewById(R.id.active_plan);

        }
    }
    public interface ButtonClickListener {
        void onButtonClick(int value);
    }

}
