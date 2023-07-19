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

    public SubscriptionPlanAdapter(Context mContext , List<BillingDataList> planList) {
        this.planList = planList;
        this.context = context;
    }

    @Override
    public SubcriptionVieHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        View photoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcription_item_layout, parent, false);
        SubcriptionVieHolder viewHolder = new SubcriptionVieHolder(photoView);
        return viewHolder;
    }


    @Override
    public void
    onBindViewHolder(final SubcriptionVieHolder viewHolder, final int position) {
        int index =viewHolder.getAdapterPosition();
//        viewHolder.title.setText(planList.get(position).name);

    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public static class SubcriptionVieHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public SubcriptionVieHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.title);

        }
    }

}
