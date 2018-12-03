package com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory.adapters;

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
import com.rootnetapp.rootnetintranet.databinding.ApprovalHistoryItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ApproverHistory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ApprovalHistoryAdapter extends RecyclerView.Adapter<ApprovalViewholder>{

    private List<ApproverHistory> approverList;

    private static final String format = "MMM d, y - hh:mm a";

    public ApprovalHistoryAdapter(List<ApproverHistory> approverList) {
        this.approverList = approverList;
    }

    @NonNull
    @Override
    public ApprovalViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        ApprovalHistoryItemBinding itemBinding =
                ApprovalHistoryItemBinding.inflate(layoutInflater, viewGroup, false);
        return new ApprovalViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalViewholder viewholder, int i) {
        if (getItemCount() < 1) {
            return;
        }
        Context context = viewholder.binding.approvalHistoryAvatar.getContext();

        ApproverHistory approverHistory = approverList.get(i);
        if (!TextUtils.isEmpty(approverHistory.avatarPicture)) {
            String path = Utils.imgDomain + approverHistory.avatarPicture;
            GlideUrl url = new GlideUrl(path);
            Glide.with(context)
                    .load(url.toStringUrl())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.default_profile_avatar)
                                    .error(R.drawable.default_profile_avatar)
                    )
                    .into(viewholder.binding.approvalHistoryAvatar);
        }

        viewholder.binding.tvName.setText(approverHistory.approverName);

        if (approverHistory.changedStatus) {
            viewholder.binding.tvApprovalHistoryIsEdited.setVisibility(View.VISIBLE);
        } else {
            viewholder.binding.tvApprovalHistoryIsEdited.setVisibility(View.INVISIBLE);
        }

        if (approverHistory.approved) {
            viewholder.binding.tvIsapproved.setText(context.getString(R.string.approved));
            viewholder.binding.tvIsapproved.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            viewholder.binding.tvIsapproved.setText(context.getString(R.string.rejected));
            viewholder.binding.tvIsapproved.setTextColor(context.getResources().getColor(R.color.red));
        }

        String dateFormatted = Utils.serverFormatToFormat(approverHistory.createdAt, format);
        viewholder.binding.tvApprovalHistoryDate.setText(dateFormatted);


        viewholder.binding.tvStatus.setText(approverHistory.status.getName());

        viewholder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return approverList.size();
    }
}
