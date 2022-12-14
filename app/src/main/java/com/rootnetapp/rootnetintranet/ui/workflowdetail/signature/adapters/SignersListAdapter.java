package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.SignatureSignerItemBinding;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignerItem;
import java.util.List;

import static com.rootnetapp.rootnetintranet.R.drawable.icon_signature_positive;
import static com.rootnetapp.rootnetintranet.R.drawable.icono_signature;

public class SignersListAdapter extends RecyclerView.Adapter<SignersViewHolder> {

    private List<SignerItem> dataSet;

    public SignersListAdapter(List<SignerItem> dataSet) {
        this.dataSet = dataSet;
    }

    public void updateList(List<SignerItem> items) {
        this.dataSet = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SignersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        SignatureSignerItemBinding itemBinding =
                SignatureSignerItemBinding.inflate(layoutInflater, viewGroup, false);
        return new SignersViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SignersViewHolder viewHolder, int position) {
        if (getItemCount() < 1) {
            return;
        }

        Context context = viewHolder.binding.getRoot().getContext();
        SignerItem item = getItem(position);

        if (!TextUtils.isEmpty(item.getAvatarUrl())) {
            String path = Utils.imgDomain + item.getAvatarUrl();
            GlideUrl url = new GlideUrl(path);
            Glide.with(context)
                    .load(url.toStringUrl())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.default_profile_avatar)
                                    .error(R.drawable.default_profile_avatar)
                    )
                    .into(viewHolder.binding.imgAvatar);
        }

        viewHolder.binding.signerName.setText(item.getName());

        if (TextUtils.isEmpty(item.getRole())) {
            viewHolder.binding.signerRole.setVisibility(View.GONE);
        } else {
            viewHolder.binding.signerRole.setVisibility(View.VISIBLE);
            viewHolder.binding.signerRole.setText(item.getRole());
        }

        if (TextUtils.isEmpty(item.getUserType())) {
            viewHolder.binding.signerUserType.setVisibility(View.GONE);
        } else {
            viewHolder.binding.signerUserType.setVisibility(View.VISIBLE);
            viewHolder.binding.signerUserType.setText(item.getUserType());
        }

        String date = String.format(context.getString(R.string.signature_time_title), item.getDate());
        viewHolder.binding.singerSignatureDate.setText(date);

        if (item.isSigned()) {
            viewHolder.binding.signerStatus.setImageResource(icon_signature_positive);
        } else {
            viewHolder.binding.signerStatus.setImageResource(icono_signature);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private SignerItem getItem(int position) {
        return dataSet.get(position);
    }
}
