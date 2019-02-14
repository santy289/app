package com.rootnetapp.rootnetintranet.ui.timeline.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.databinding.TimelineCommentItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Comment;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimelineCommentAdapter extends RecyclerView.Adapter<TimelineCommentViewholder> {

    private List<Comment> comments;
    private List<User> people;
    private Context context;
    private TimelineViewModel viewModel;
    private String token;
    private Fragment parent;
    private TimelineCommentAdapter adapter = null;

    public TimelineCommentAdapter(List<Comment> comments, List<User> people,
                                  TimelineViewModel viewModel, String token, Fragment parent) {
        this.comments = comments;
        this.people = people;
        this.viewModel = viewModel;
        this.token = token;
        this.parent = parent;
    }

    @Override
    public TimelineCommentViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        this.context = viewGroup.getContext();
        TimelineCommentItemBinding itemBinding =
                TimelineCommentItemBinding.inflate(layoutInflater, viewGroup, false);
        return new TimelineCommentViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(TimelineCommentViewholder holder, int i) {

        User author = null;
        Comment item = null;
        int interactionId = -1, associate = -1;
        if (comments != null) {
            item = comments.get(i);
            for (User user : people) {
                if (user.getUserId() == item.getAuthor()) {
                    author = user;
                    String path = Utils.imgDomain + user.getPicture().trim();
                    Picasso.get().load(path).into(holder.binding.imgPoster);
                    holder.binding.tvName.setText(user.getFullName());
                }
            }
            holder.binding.tvComment.setText(item.getDescription());
            String time = item.getCreatedAt().split("T")[0];
            holder.binding.tvTime.setText(time);
            interactionId = item.getInteractionId();
            associate = item.getId();
        }

        Comment finalItem = item;
        holder.binding.tvReply.setOnClickListener(view -> {
            if (holder.binding.recComments.getVisibility() == View.GONE) {
                if (finalItem != null) {
                    listenToResponse(holder);
                    if (finalItem.getCount() != 0) {
                        Utils.showLoading(context);
                        viewModel.getSubComment(token, finalItem.getId(),
                                (finalItem.getLevel() + 1));
                    }else{
                        holder.binding.recComments.setVisibility(View.VISIBLE);
                        holder.binding.line.setVisibility(View.VISIBLE);
                        holder.binding.lytComments.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                holder.binding.recComments.setVisibility(View.GONE);
                holder.binding.line.setVisibility(View.GONE);
                holder.binding.lytComments.setVisibility(View.GONE);
            }
        });
        int finalInteractionId = interactionId;
        User finalAuthor = author;
        int finalAssociate = associate;
        holder.binding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = holder.binding.inputComment.getText().toString();
                if (!TextUtils.isEmpty(comment)) {
                    if (finalAuthor != null) {
                        final Observer<Comment> postSubCommentsObserver = ((Comment data) -> {
                            Utils.hideLoading();
                            if (null != data) {
                                if (adapter != null) {
                                    adapter.getComments().add(data);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    List<Comment> list = new ArrayList<>();
                                    list.add(data);
                                    adapter = new TimelineCommentAdapter(list, people,
                                            viewModel, token, parent);
                                    holder.binding.recComments.setAdapter(adapter);
                                }
                                holder.binding.inputComment.setText("");
                            }
                            viewModel.getObservablePostSubComments().removeObservers(parent);
                            viewModel.clearPostSubComments();
                        });
                        viewModel.getObservablePostSubComments().observe(parent, postSubCommentsObserver);
                        Utils.showLoading(context);
                        viewModel.postSubComment(token, finalInteractionId, finalAssociate,
                                comment, finalAuthor.getUserId());
                    } else {
                        Toast.makeText(context, "error wth author", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.empty_comment), Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.binding.recComments.setLayoutManager(new LinearLayoutManager(context));

    }

    private void listenToResponse(TimelineCommentViewholder holder) {
        final Observer<List<Comment>> subCommentsObserver = ((List<Comment> data) -> {
            Utils.hideLoading();
            if (null != data) {
                List<Comment> list = new ArrayList<>();
                list.addAll(data);
                viewModel.getObservableSubComments().removeObservers(parent);
                viewModel.clearSubComments();
                adapter = new TimelineCommentAdapter(list, people,
                        viewModel, token, parent);
                holder.binding.recComments.setAdapter(adapter);
                holder.binding.recComments.setVisibility(View.VISIBLE);
                holder.binding.line.setVisibility(View.VISIBLE);
                holder.binding.lytComments.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getObservableSubComments().observe(parent, subCommentsObserver);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public List<Comment> getComments() {
        return comments;
    }
}
