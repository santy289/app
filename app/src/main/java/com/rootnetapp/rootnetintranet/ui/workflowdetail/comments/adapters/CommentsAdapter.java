package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.CommentsItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentFileResponse;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsFragmentInterface;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsViewholder> implements
        CommentsAdapterInterface {

    private static final String format = "MMM d, y - hh:mm a";

    public List<Comment> comments;

    private Context mContext;
    private CommentsFragmentInterface commentsFragmentInterface;
    private int loggedUserId;
    private boolean hideEditOption, hideDeleteOption;

    public CommentsAdapter(CommentsFragmentInterface commentsFragmentInterface, Context context,
                           List<Comment> comments, int loggedUserId) {
        this.commentsFragmentInterface = commentsFragmentInterface;
        this.mContext = context;
        this.comments = comments;
        this.loggedUserId = loggedUserId;
    }

    public void setData(List<Comment> list) {
        comments = list;
        notifyDataSetChanged();
        getItemCount();
    }

    public void addItem(Comment comment) {
        comments.add(0, comment);
        notifyItemInserted(0);
        getItemCount();
    }

    public void setHideEditOption(boolean hide) {
        this.hideEditOption = hide;
        notifyDataSetChanged();
    }

    public void setHideDeleteOption(boolean hide) {
        this.hideDeleteOption = hide;
        notifyDataSetChanged();
    }

    @NonNull
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
                .load(url)
                .into(holder.binding.imgUser);
        holder.binding.tvName.setText(item.getUserInfo().getFullName());

        String dateFormatted = Utils.serverFormatToFormat(item.getDate(), format);
        holder.binding.tvDate.setText(dateFormatted);
        holder.binding.tvComment.setText(item.getDescription());
        holder.binding.executePendingBindings();

        CommentsAttachmentsAdapter adapter = new CommentsAttachmentsAdapter(this, item.getFiles());
        holder.binding.rvAttachments.setLayoutManager(
                new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        holder.binding.rvAttachments.setAdapter(adapter);

        holder.binding.btnOptions.setVisibility(
                item.getUserInfo().getUserId() == loggedUserId ? View.VISIBLE : View.GONE);

        holder.binding.btnOptions.setVisibility(hideEditOption && hideDeleteOption ? View.GONE : View.VISIBLE);

        holder.binding.btnOptions.setOnClickListener(v -> {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(context, holder.binding.btnOptions);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.menu_comment_options, popup.getMenu());

            if (hideEditOption) popup.getMenu().findItem(R.id.action_edit).setVisible(false);
            if (hideDeleteOption) popup.getMenu().findItem(R.id.action_delete).setVisible(false);

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_edit:
                        commentsFragmentInterface.editComment(item);
                        break;
                    case R.id.action_delete:
                        commentsFragmentInterface.deleteComment(item);
                        break;
                }

                return true;
            });

            popup.show();//showing popup menu
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public void downloadAttachment(CommentFileResponse commentFile) {
        commentsFragmentInterface.downloadCommentAttachment(commentFile);
    }
}
