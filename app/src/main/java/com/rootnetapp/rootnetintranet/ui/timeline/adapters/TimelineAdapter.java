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
import com.rootnetapp.rootnetintranet.databinding.TimelineItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Comment;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Interaction;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineInterface;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineViewholder> {

    private List<TimelineItem> items;
    private List<User> people;
    private List<Interaction> comments;
    private TimelineViewModel viewModel;
    private Context context;
    private Fragment parent;
    private TimelineInterface anInterface;

    public TimelineAdapter(List<TimelineItem> items, List<User> people, List<Interaction> comments,
                           TimelineViewModel viewModel, Fragment parent,
                           TimelineInterface anInterface) {
        this.items = items;
        this.people = people;
        this.comments = comments;
        this.viewModel = viewModel;
        this.parent = parent;
        this.anInterface = anInterface;
    }

    public void addData(List<TimelineItem> items, List<Interaction> comments){
        int positionStart = this.items.size();

        this.items.addAll(items);
        this.comments.addAll(comments);

        int positionEnd = this.items.size() - 1;

        notifyItemRangeInserted(positionStart, positionEnd);
//        notifyDataSetChanged();
        getItemCount();
    }

    @Override
    public TimelineViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        this.context = viewGroup.getContext();
        TimelineItemBinding itemBinding =
                TimelineItemBinding.inflate(layoutInflater, viewGroup, false);
        return new TimelineViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(TimelineViewholder holder, int i) {

        User author = null;
        if (i < items.size()) {
            TimelineItem item = items.get(i);
            String title = "";
            for (User user : people) {
                if (user.getUserId() == item.getAuthor()) {
                    author = user;
                    if (user.getPicture() != null) {
                        String path = Utils.imgDomain + user.getPicture().trim();
                        Picasso.get().load(path).into(holder.binding.imgPoster);
                    }
                    title = user.getFullName();
                }
            }

            switch (item.getDescription().getText()) {
                case "TIMELINE_TRACKING_CREATED": {
                    title = title + " " + context.getString(R.string.tracking_created);
                    break;
                }
                case "TIMELINE_SPRINT_METADATA_STATUS_CREATED": {
                    title = title + " " + context.getString(R.string.status_created);
                    break;
                }
            }

//            if (item.getDescription().getArguments().getCompanyName() != null) {
//                title = title + " " + item.getDescription().getArguments().getCompanyName();
//            }
            holder.binding.tvTitle.setText(title);
            if (item.getDescription().getArguments().getDescription() != null) {
                holder.binding.tvDescription
                        .setText(context.getString(R.string.txt_description) + " "
                                + item.getDescription().getArguments().getDescription());
            }
            if (item.getDescription().getArguments().getCurrentStatus() != null) {
                holder.binding.tvDescription
                        .setText(context.getString(R.string.txt_statusname) + " "
                                + item.getDescription().getArguments().getCurrentStatus()
                                .getName());
            }
            String date = item.getCreatedAt().split("T")[0];
            holder.binding.tvDate.setText(date);

            if (i == 0) {
                holder.binding.topLine.setVisibility(View.INVISIBLE);
            }
            holder.binding.recComments.setLayoutManager(new LinearLayoutManager(context));
            List<Comment> theComments = new ArrayList<>();
            final int interactionId;
            int x = -1;
            for (Interaction interactionComment : comments) {
                if (interactionComment.getEntity().equals(item.getEntityId())) {
                    x = interactionComment.getId();
                    theComments = interactionComment.getComments();
                }
            }
            interactionId = x;
            holder.binding.recComments.setAdapter(new TimelineCommentAdapter(theComments, people,
                    viewModel, parent));
            holder.binding.tvComments.setOnClickListener(view -> {
                if (holder.binding.recComments.getVisibility() == View.GONE) {
                    holder.binding.recComments.setVisibility(View.VISIBLE);
                    holder.binding.lytComments.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.recComments.setVisibility(View.GONE);
                    holder.binding.lytComments.setVisibility(View.GONE);
                }
            });
            holder.binding.lytThumbsup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            holder.binding.lytThumbsdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            User finalAuthor = author;
            holder.binding.btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String comment = holder.binding.etComment.getText().toString();
                    if (!TextUtils.isEmpty(comment)) {
                        if (finalAuthor != null) {
                            Utils.showLoading(context);
                            viewModel.postComment(interactionId, item.getEntityId(),
                                    item.getEntity(), comment, finalAuthor.getUserId());
                        } else {
                            Toast.makeText(context, "error wth author", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.empty_comment),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            final Observer<Interaction> postCommentObserver = ((Interaction data) -> {
                Utils.hideLoading();
                if (null != data) {
                    viewModel.clearPostComments();
                    anInterface.reload();
                }
            });
            viewModel.getObservablePostComments().observe(parent, postCommentObserver);
        } else {
            holder.binding.lytData.setVisibility(View.INVISIBLE);
            holder.binding.bottomLine.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
