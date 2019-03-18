package com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.PeopleInvolvedItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflows.PersonRelated;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PeopleInvolvedAdapter extends RecyclerView.Adapter<PeopleInvolvedViewholder> {

    private Context context;
    private List<PersonRelated> mDataset;

    public PeopleInvolvedAdapter(List<PersonRelated> profiles) {
        this.mDataset = profiles;
    }

    @NonNull
    @Override
    public PeopleInvolvedViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        PeopleInvolvedItemBinding itemBinding =
                PeopleInvolvedItemBinding.inflate(layoutInflater, viewGroup, false);
        context = viewGroup.getContext();
        return new PeopleInvolvedViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleInvolvedViewholder viewHolder, int i) {
        if (getItemCount() < 1) {
            return;
        }

        PersonRelated item = getItem(i);

        if (!TextUtils.isEmpty(item.getPicture())) {
            Context context = viewHolder.binding.imgInvolvedAvatar.getContext();
            String path = Utils.imgDomain + item.getPicture();
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

        viewHolder.binding.tvName.setText(item.getName());

        List<String> relations = new ArrayList<>();
        if (item.isOwner()) {
            relations.add(context
                    .getString(R.string.workflow_detail_people_involved_fragment_owner));
        }
        if (item.isApprover()) {
            relations.add(context
                    .getString(R.string.workflow_detail_people_involved_fragment_approver));
        }
        if (item.isSpecificApprover()) {
            relations.add(context.getString(
                    R.string.workflow_detail_people_involved_fragment_specific_approver));
        }
        if (item.isProfileInvolved()) {
            relations.add(context
                    .getString(R.string.workflow_detail_people_involved_fragment_person_related));
        }

        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = relations.iterator();
        while (iterator.hasNext()) {
            String relation = iterator.next();
            stringBuilder.append(relation);

            if (iterator.hasNext()) {
                stringBuilder.append(" - ");
            }
        }

        viewHolder.binding.tvRelations.setText(stringBuilder.toString());

        viewHolder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private PersonRelated getItem(int position) {
        return mDataset.get(position);
    }
}
