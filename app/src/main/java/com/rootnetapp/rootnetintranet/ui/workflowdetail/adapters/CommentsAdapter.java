package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.CommentsItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsViewholder> {

    public List<Comment> comments;

    private static final String format = "MMM d, y - h:m a";

    public CommentsAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public CommentsViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        CommentsItemBinding itemBinding =
                CommentsItemBinding.inflate(layoutInflater, viewGroup, false);
        return new CommentsViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewholder holder, int i) {
        Comment item = comments.get(i);
        String path = Utils.imgDomain + item.getUserInfo().getPicture();
        Context context = holder.itemView.getContext();
        GlideUrl url = new GlideUrl(path);
        Glide.with(context)
                .load(url.toStringUrl())
                .into(holder.binding.imgUser);
        holder.binding.tvName.setText(item.getUserInfo().getFullName());

        String dateFormatted = Utils.serverFormatToFormat(item.getDate(), format);
        holder.binding.tvDate.setText(dateFormatted);
        holder.binding.tvComment.setText(item.getDescription());
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
