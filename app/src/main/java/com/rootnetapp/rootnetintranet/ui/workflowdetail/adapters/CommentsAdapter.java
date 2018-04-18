package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.CommentsItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by root on 04/04/18.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsViewholder> {

    public List<Comment> comments;
    private Context context;

    public CommentsAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public CommentsViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        context = viewGroup.getContext();
        CommentsItemBinding itemBinding =
                CommentsItemBinding.inflate(layoutInflater, viewGroup, false);
        return new CommentsViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(CommentsViewholder holder, int i) {
        Comment item = comments.get(i);
        Picasso.get().load(Utils.domain + item.getUserInfo().getPicture()).into(holder.binding.imgUser);
        holder.binding.tvName.setText(item.getUserInfo().getFullName());
        String date = item.getDate().split("T")[0];
        String hour = (item.getDate().split("T")[1]).split("-")[0];
        holder.binding.tvDate.setText(date + " - " + hour);
        holder.binding.tvComment.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
