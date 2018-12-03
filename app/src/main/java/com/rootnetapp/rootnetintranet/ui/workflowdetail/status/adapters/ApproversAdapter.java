package com.rootnetapp.rootnetintranet.ui.workflowdetail.status.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ApproversItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Approver;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ApproversAdapter extends RecyclerView.Adapter<ApproversViewholder> {

    private List<Approver> currentApprovers;

    public ApproversAdapter(List<Approver> currentApprovers) {
        this.currentApprovers = currentApprovers;
    }

    @NonNull
    @Override
    public ApproversViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        ApproversItemBinding itemBinding =
                ApproversItemBinding.inflate(layoutInflater, viewGroup, false);
        return new ApproversViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ApproversViewholder viewholder, int i) {
        if (getItemCount() < 1) {
            return;
        }
        Approver currentApprover = currentApprovers.get(i);

        Context context = viewholder.binding.detailApproverAvatar.getContext();

        if (!TextUtils.isEmpty(currentApprover.entityAvatar)) {
            String path = Utils.imgDomain + currentApprover.entityAvatar;
            GlideUrl url = new GlideUrl(path);
            Glide.with(context)
                    .load(url.toStringUrl())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.default_profile_avatar)
                                    .error(R.drawable.default_profile_avatar)
                    )
                    .into(viewholder.binding.detailApproverAvatar);
        }

        //we use INVISIBLE instead of GONE to keep the constraints
        viewholder.binding.detailApproverRequired
                .setVisibility(currentApprover.isRequire ? View.VISIBLE : View.INVISIBLE);

        viewholder.binding.detailApproverName.setText(currentApprover.entityName);

        if (currentApprover.approved) {
            viewholder.binding.detailApproverState.setText(context.getString(R.string.approved));
            viewholder.binding.detailApproverState.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            viewholder.binding.detailApproverState.setText(context.getString(R.string.rejected));
            viewholder.binding.detailApproverState.setTextColor(context.getResources().getColor(R.color.red));
        }

        viewholder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return currentApprovers.size();
    }

}
