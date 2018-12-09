package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.CommentsAttachmentItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentFileResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAttachmentsAdapter extends RecyclerView.Adapter<CommentsAttachmentsViewHolder> {

    private List<CommentFileResponse> commentFiles;
    private CommentsAdapterInterface commentsAdapterInterface;

    protected CommentsAttachmentsAdapter(CommentsAdapterInterface commentsAdapterInterface,
                                      List<CommentFileResponse> commentFiles) {
        this.commentsAdapterInterface = commentsAdapterInterface;
        this.commentFiles = commentFiles;
    }

    @NonNull
    @Override
    public CommentsAttachmentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        CommentsAttachmentItemBinding itemBinding =
                CommentsAttachmentItemBinding.inflate(layoutInflater, viewGroup, false);
        return new CommentsAttachmentsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAttachmentsViewHolder holder, int i) {
        CommentFileResponse item = getItem(i);

        holder.binding.chip.setText(item.getName());
        holder.binding.chip.setOnClickListener(view -> {
            // Handle the click on the chip itself.
            commentsAdapterInterface.downloadAttachment(item);
        });
    }

    @Override
    public int getItemCount() {
        return commentFiles.size();
    }

    private CommentFileResponse getItem(int position) {
        return commentFiles.get(position);
    }
}
