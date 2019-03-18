package com.rootnetapp.rootnetintranet.ui.timeline.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.databinding.TimelineItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.timeline.Arguments;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Comment;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Interaction;
import com.rootnetapp.rootnetintranet.ui.createworkflow.adapters.OnTouchClickListener;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineAction;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineInterface;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineViewholder> {

    private List<TimelineItem> items;
    private List<User> people;
    private List<Interaction> interactions;
    private TimelineViewModel viewModel;
    private Context context;
    private Fragment parent;
    private TimelineInterface anInterface;

    public TimelineAdapter(List<TimelineItem> items, List<User> people,
                           List<Interaction> interactions,
                           TimelineViewModel viewModel, Fragment parent,
                           TimelineInterface anInterface) {
        this.items = items;
        this.people = people;
        this.interactions = interactions;
        this.viewModel = viewModel;
        this.parent = parent;
        this.anInterface = anInterface;
    }

    public void addData(List<TimelineItem> items, List<Interaction> comments) {
        int positionStart = getItemCount();

        this.items.addAll(items);
        this.interactions.addAll(comments);

        int positionEnd = getItemCount() - 1; //last item

        notifyItemChanged(positionStart - 1); //update previously last item (show bottom line)
        notifyItemRangeInserted(positionStart, positionEnd);
    }

    public void updateInteraction(Interaction interaction) {
        this.interactions.remove(interaction);
        this.interactions.add(interaction);
        notifyDataSetChanged();
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
        TimelineItem item = items.get(i);
        Arguments arguments = item.getDescription().getArguments();

        String title = "";
        if (people != null) {
            for (User user : people) {
                if (user.getId() == item.getAuthor()) {
                    author = user;
                    if (user.getPicture() != null) {
                        String path = Utils.imgDomain + user.getPicture().trim();
                        Picasso.get().load(path).into(holder.binding.imgPoster);
                    }
                    title = user.getFullName();
                }
            }
        }

        Spannable titleSpannable = null;
        String description = null;
        if (author != null) {
            switch (item.getDescription().getText()) {
                case TimelineAction.WORKFLOW_CREATED:
                    titleSpannable = getSpannableTitle(R.string.timeline_action_workflow_created,
                            author.getFullName(), arguments.getName());
                    break;

                case TimelineAction.WORKFLOW_UPDATED:
                    titleSpannable = getSpannableTitle(R.string.timeline_action_workflow_updated,
                            author.getFullName(), arguments.getName());
                    break;

                case TimelineAction.WORKFLOW_STATUS_APPROVED_CREATED:
                case TimelineAction.WORKFLOW_STATUS_APPROVED_UPDATED:
                    titleSpannable = getSpannableTitle(
                            R.string.timeline_action_workflow_status_updated, author.getFullName(),
                            arguments.getName());
                    break;

                case TimelineAction.WORKFLOW_FILE_RECORD_CREATED:
                    titleSpannable = getSpannableTitle(
                            R.string.timeline_action_workflow_file_created, author.getFullName(),
                            arguments.getName());
                    break;

                case TimelineAction.WORKFLOW_COMMENT_CREATED:
                    titleSpannable = getSpannableTitle(
                            R.string.timeline_action_workflow_comment_created, author.getFullName(),
                            arguments.getName());

                    description = item.getDescription().getArguments().getComment();
                    break;
            }
        }

        if (titleSpannable != null) {
            holder.binding.tvTitle.setText(titleSpannable);
        } else {
            holder.binding.tvTitle.setText(title);
        }

        if (description != null) {
            holder.binding.tvDescription.setText(description);
        } else if (item.getDescription().getArguments().getCurrentStatus() != null) {
            holder.binding.tvDescription.setText(context.getString(R.string.timeline_current_status,
                    item.getDescription().getArguments().getCurrentStatus().getName()));
        } else if (item.getDescription().getArguments().getDescription() != null) {
            holder.binding.tvDescription.setText(context.getString(R.string.timeline_description,
                    item.getDescription().getArguments().getDescription()));
        }

        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(Locale.getDefault())
                .build();
        long timeInMillis = Utils
                .getDateInMillisFromString(item.getCreatedAt(), Utils.SERVER_DATE_FORMAT);
        String timeAgo = TimeAgo.using(timeInMillis, messages);
        holder.binding.tvTimeAgo.setText(timeAgo);

        holder.binding.recComments.setLayoutManager(new LinearLayoutManager(context));
        List<Comment> subComments = new ArrayList<>();
        final int interactionId;
        Interaction itemInteraction = null;
        int x = -1;
        if (interactions != null) {
            for (Interaction interaction : interactions) {
                if (interaction.getEntity().equals(item.getEntityId())) {
                    x = interaction.getId();
                    itemInteraction = interaction;
                    subComments = interaction.getComments();
                    break;
                }
            }
        }
        interactionId = x;
        holder.binding.recComments.setAdapter(new TimelineCommentAdapter(subComments, people,
                viewModel, parent));
        holder.binding.tvComments.setOnClickListener(view -> {
            if (holder.binding.recComments.getVisibility() == View.GONE) {
                holder.binding.recComments.setVisibility(View.VISIBLE);
                holder.binding.lytCommentInput.setVisibility(View.VISIBLE);
            } else {
                holder.binding.recComments.setVisibility(View.GONE);
                holder.binding.lytCommentInput.setVisibility(View.GONE);
            }
        });
        holder.binding.tvComments
                .setOnTouchListener(new OnTouchClickListener(holder.binding.tvComments));
        if (subComments != null && !subComments.isEmpty()) {
            holder.binding.tvComments.setText(
                    context.getResources().getQuantityString(
                            R.plurals.timeline_comments,
                            subComments.size(),
                            subComments.size())
            );
        }

        if (itemInteraction == null) {
            holder.binding.lytThumbsUp.setVisibility(View.GONE);
            holder.binding.lytThumbsDown.setVisibility(View.GONE);
        } else {
            holder.binding.lytThumbsUp.setVisibility(View.VISIBLE);
            holder.binding.lytThumbsDown.setVisibility(View.VISIBLE);

            if (itemInteraction.getThumbsUp() != null) {
                holder.binding.tvUpAmount.setText(context.getString(R.string.timeline_thumbs_value,
                        itemInteraction.getThumbsUp()));
            }
            if (itemInteraction.getThumbsDown() != null) {
                holder.binding.tvDownAmmount
                        .setText(context.getString(R.string.timeline_thumbs_value,
                                itemInteraction.getThumbsDown()));
            }
        }

        User finalAuthor = author;
        holder.binding.btnComment.setOnClickListener(view -> {
            String comment = holder.binding.etComment.getText().toString();
            anInterface.addCommentClicked(comment, finalAuthor, item, interactionId);
            holder.binding.etComment.setText("");
        });

        holder.binding.lytThumbsUp.setOnClickListener(view -> {
            anInterface.likeClicked(finalAuthor, item, interactionId);
        });

        holder.binding.lytThumbsDown.setOnClickListener(view -> {
            anInterface.dislikeClicked(finalAuthor, item, interactionId);
        });

        //hide the top line for the first item
        if (i == 0) {
            holder.binding.topLine.setVisibility(View.INVISIBLE);
        } else {
            holder.binding.topLine.setVisibility(View.VISIBLE);
        }

        //hide the bottom line for the last item
        if (i == getItemCount() - 1) {
            holder.binding.bottomLine.setVisibility(View.INVISIBLE);
        } else {
            holder.binding.bottomLine.setVisibility(View.VISIBLE);
        }

        holder.binding.executePendingBindings();
    }

    private Spannable getSpannableTitle(@StringRes int stringRes, String authorName,
                                        String workflowKey) {
        if (authorName == null) return null;
        if (workflowKey == null) workflowKey = "";

        String text = context.getString(stringRes, authorName, workflowKey);

        Spannable spannable = new SpannableString(text);

        int authorStartIndex = text.indexOf(authorName);
        int authorEndIndex = authorStartIndex + authorName.length();
        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                authorStartIndex,
                authorEndIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );

        int workflowStartIndex = text.indexOf(workflowKey);
        int workflowEndIndex = workflowStartIndex + workflowKey.length();
        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                workflowStartIndex,
                workflowEndIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );
        spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent)),
                workflowStartIndex,
                workflowEndIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );

        return spannable;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}
