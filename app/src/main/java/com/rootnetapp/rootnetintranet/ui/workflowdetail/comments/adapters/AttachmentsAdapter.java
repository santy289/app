package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.AttachmentItemBinding;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsFragmentInterface;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsViewHolder> {

    private List<CommentFile> commentFiles;
    private CommentsFragmentInterface commentsFragmentInterface;

    public AttachmentsAdapter(CommentsFragmentInterface commentsFragmentInterface,
                              List<CommentFile> commentFiles) {
        this.commentsFragmentInterface = commentsFragmentInterface;
        this.commentFiles = commentFiles;
    }

    public void addItem(CommentFile commentFile) {
        commentFiles.add(commentFile);
        notifyItemInserted(commentFiles.size() - 1);
        getItemCount();
    }

    public void removeItem(CommentFile commentFile) {
        int position = commentFiles.indexOf(commentFile);
        commentFiles.remove(commentFile);
        notifyItemRemoved(position);
        getItemCount();
    }

    @NonNull
    @Override
    public AttachmentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        AttachmentItemBinding itemBinding =
                AttachmentItemBinding.inflate(layoutInflater, viewGroup, false);
        return new AttachmentsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentsViewHolder holder, int i) {
        CommentFile item = getItem(i);

        holder.binding.chip.setText(item.getName());
        holder.binding.chip.setOnCloseIconClickListener(view -> {
            // Handle the click on the close icon.
            commentsFragmentInterface.removeAttachment(item);
        });
    }

    @Override
    public int getItemCount() {
        return commentFiles.size();
    }

    private CommentFile getItem(int position) {
        return commentFiles.get(position);
    }
}
