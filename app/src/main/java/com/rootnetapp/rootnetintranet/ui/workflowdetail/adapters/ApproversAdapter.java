package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

public class ApproversAdapter extends RecyclerView.Adapter<ApproversViewholder>{

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

        if (!TextUtils.isEmpty(currentApprover.entityAvatar)) {
            Context context = viewholder.binding.detailApproverAvatar.getContext();
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

        if (currentApprover.isRequire) {
            viewholder.binding.detailApproverRequired.setVisibility(View.VISIBLE);
        } else {
            viewholder.binding.detailApproverRequired.setVisibility(View.GONE);
        }

        viewholder.binding.detailApproverName.setText(currentApprover.entityName);
        viewholder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return currentApprovers.size();
    }


}
