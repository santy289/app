package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.databinding.PeopleInvolvedItemBinding;

import java.util.List;

public class PeopleInvolvedAdapter extends RecyclerView.Adapter<PeopleInvolvedViewholder>{
    private List<ProfileInvolved> profiles;

    public PeopleInvolvedAdapter(List<ProfileInvolved> profiles) {
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public PeopleInvolvedViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        PeopleInvolvedItemBinding itemBinding =
                PeopleInvolvedItemBinding.inflate(layoutInflater, viewGroup, false);
        return new PeopleInvolvedViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleInvolvedViewholder viewHolder, int i) {
        if (getItemCount() < 1) {
            return;
        }

        ProfileInvolved profileInvolved = profiles.get(i);

        if (!TextUtils.isEmpty(profileInvolved.picture)) {
            Context context = viewHolder.binding.imgInvolvedAvatar.getContext();
            String path = Utils.imgDomain + profileInvolved.picture;
            GlideUrl url = new GlideUrl(path);
            Glide.with(context)
                    .load(url.toStringUrl())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.default_profile_avatar)
                                    .error(R.drawable.default_profile_avatar)
                    )
                    .into(viewHolder.binding.imgInvolvedAvatar);
        }

        viewHolder.binding.tvInvolvedName.setText(profileInvolved.fullName);
        viewHolder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }
}
