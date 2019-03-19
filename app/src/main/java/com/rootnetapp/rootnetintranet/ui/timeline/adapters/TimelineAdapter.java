package com.rootnetapp.rootnetintranet.ui.timeline.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
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

import org.threeten.bp.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineViewholder> {

    private List<TimelineItem> items;
    private List<User> people;
    private List<Interaction> interactions;
    private List<Interaction> usedInteractions; //avoid repetition
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

        usedInteractions = new ArrayList<>();
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
        int oldInteractionIndex = this.interactions.indexOf(interaction);
        if (oldInteractionIndex != -1) {
            Interaction oldInteraction = this.interactions.get(oldInteractionIndex);
            //check for null on the new interaction and use the previous interaction values
            if (interaction.getComments() == null) {
                interaction.setComments(oldInteraction.getComments());
            }
            if (interaction.getThumbsUp() == null) {
                interaction.setThumbsUp(oldInteraction.getThumbsUp());
            }
            if (interaction.getThumbsDown() == null) {
                interaction.setThumbsDown(oldInteraction.getThumbsDown());
            }
        }

        this.interactions.remove(interaction);
        this.usedInteractions.remove(interaction);
        this.interactions.add(interaction);

        TimelineItem item = this.items.stream()
                .filter(timelineItem -> timelineItem.getEntityId().equals(interaction.getEntity())
                        && timelineItem.getEntity().equals(interaction.getEntityType()))
                .findAny()
                .orElse(null);

        if (item != null) {
            notifyItemChanged(this.items.indexOf(item));
        } else {
            notifyDataSetChanged();
            getItemCount();
        }
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
        @DrawableRes int drawableResId = 0;
        if (author != null) {
            switch (item.getDescription().getText()) {
                case TimelineAction.WORKFLOW_CREATED:
                    titleSpannable = getSpannableTitle(R.string.timeline_action_workflow_created,
                            author.getFullName(), arguments.getName());
                    drawableResId = R.drawable.ic_code_branch_black;

                    description = getWorkflowDescription(item);
                    break;

                case TimelineAction.WORKFLOW_UPDATED:
                    titleSpannable = getSpannableTitle(R.string.timeline_action_workflow_updated,
                            author.getFullName(), arguments.getName());
                    drawableResId = R.drawable.ic_code_branch_black;

                    description = getWorkflowDescription(item);
                    break;

                case TimelineAction.WORKFLOW_STATUS_APPROVED_CREATED:
                case TimelineAction.WORKFLOW_STATUS_APPROVED_UPDATED:
                    String currentStatus = "";
                    if (arguments.getStatus() != null && arguments.getStatus() instanceof Map) {
                        Map<String, Object> statusMap = (Map<String, Object>) arguments.getStatus();
                        currentStatus = (String) statusMap.get("name");
                    }

                    int titleResId = arguments.getApproved()
                            ? R.string.timeline_action_workflow_status_approved
                            : R.string.timeline_action_workflow_status_rejected;
                    titleSpannable = getSpannableTitle(
                            titleResId,
                            author.getFullName(),
                            currentStatus,
                            arguments.getName()
                    );
                    drawableResId = R.drawable.ic_flag_black_24dp;

                    String nextStatus = null;
                    if (arguments.getNextStatus() != null) {
                        nextStatus = arguments.getNextStatus().getName();
                    }

                    if (arguments.getApproved()) {
                        if (currentStatus != null && nextStatus != null) {
                            description = context.getString(
                                    R.string.timeline_description_workflow_status_approved,
                                    currentStatus,
                                    nextStatus
                            );
                        }
                    } else {
                        if (currentStatus != null) {
                            description = context.getString(
                                    R.string.timeline_description_workflow_status_rejected,
                                    currentStatus
                            );
                        }
                    }
                    break;

                case TimelineAction.WORKFLOW_FILE_RECORD_CREATED:
                    titleSpannable = getSpannableTitle(
                            R.string.timeline_action_workflow_file_created, author.getFullName(),
                            arguments.getName());
                    drawableResId = R.drawable.ic_file_black;

                    description = context.getString(R.string.timeline_description_file,
                            arguments.getFileName());
                    break;

                case TimelineAction.WORKFLOW_COMMENT_CREATED:
                    titleSpannable = getSpannableTitle(
                            R.string.timeline_action_workflow_comment_created, author.getFullName(),
                            arguments.getName());

                    drawableResId = R.drawable.ic_comment_timeline_black_24dp;

                    description = arguments.getComment();
                    break;

                case TimelineAction.WORKFLOW_COMMENT_UPDATED:
                    titleSpannable = getSpannableTitle(
                            R.string.timeline_action_workflow_comment_updated, author.getFullName(),
                            arguments.getName());

                    drawableResId = R.drawable.ic_comment_timeline_black_24dp;

                    description = arguments.getComment();
                    break;
                default:
                    description = context.getString(R.string.timeline_description,
                            arguments.getDescription());
            }
        }

        if (drawableResId != 0) {
            holder.binding.imgItem.setImageResource(drawableResId);
        }

        if (titleSpannable != null) {
            holder.binding.tvTitle.setText(titleSpannable);
        } else {
            holder.binding.tvTitle.setText(title);
        }

        if (description != null) {
            holder.binding.tvDescription
                    .setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
        }

        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(Locale.getDefault())
                .build();
        long timeInMillis = Utils
                .getDateInMillisFromString(item.getCreatedAt(), Utils.SERVER_DATE_FORMAT);
        String timeAgo = TimeAgo.using(timeInMillis, messages);
        holder.binding.tvTimeAgo.setText(timeAgo);

        holder.binding.recComments.setLayoutManager(new LinearLayoutManager(context));

        List<Comment> subComments = new ArrayList<>();
        Integer interactionId = null;
        Interaction itemInteraction = null;
        if (interactions != null) {
            for (Interaction interaction : interactions) {
                if (interaction.getEntity().equals(item.getEntityId())
                        && interaction.getEntityType().equals(item.getEntity())
                        && !usedInteractions.contains(interaction)) {
                    interactionId = interaction.getId();
                    itemInteraction = interaction;
                    subComments = interaction.getComments();

                    usedInteractions.add(interaction);
                    break;
                }
            }
        }

        holder.binding.recComments.setAdapter(new TimelineCommentAdapter(subComments, people,
                viewModel, parent, item.isShowCommentInput()));

        holder.binding.tvComments.setOnClickListener(view -> {
            if (holder.binding.recComments.getVisibility() == View.GONE) {
                holder.binding.recComments.setVisibility(View.VISIBLE);
                if (item.isShowCommentInput()) {
                    holder.binding.lytCommentInput.setVisibility(View.VISIBLE);
                }
            } else {
                holder.binding.recComments.setVisibility(View.GONE);
                holder.binding.lytCommentInput.setVisibility(View.GONE);
            }
        });
        holder.binding.tvComments
                .setOnTouchListener(new OnTouchClickListener(holder.binding.tvComments));
        int commentsAmount = subComments == null ? 0 : subComments.size();
        holder.binding.tvComments.setText(
                context.getResources().getQuantityString(
                        R.plurals.timeline_comments,
                        commentsAmount,
                        commentsAmount)
        );

        holder.binding.lytCommentInput.setVisibility(
                item.isShowCommentInput() && commentsAmount < 10 ? View.VISIBLE : View.GONE);
        holder.binding.recComments.setVisibility(commentsAmount < 10 ? View.VISIBLE : View.GONE);

        int thumbsUp = itemInteraction != null && itemInteraction
                .getThumbsUp() != null ? itemInteraction.getThumbsUp() : 0;
        int thumbsDown = itemInteraction != null && itemInteraction
                .getThumbsDown() != null ? itemInteraction.getThumbsDown() : 0;
        holder.binding.tvUpAmount.setText(context.getString(
                R.string.timeline_thumbs_value,
                thumbsUp));
        holder.binding.tvDownAmmount.setText(context.getString(
                R.string.timeline_thumbs_value,
                thumbsDown));

        User finalAuthor = author;
        Integer finalInteractionId = interactionId;
        holder.binding.btnComment.setOnClickListener(view -> {
            String comment = holder.binding.etComment.getText().toString();
            anInterface.addCommentClicked(comment, finalAuthor, item, finalInteractionId);
            holder.binding.etComment.setText("");
        });

        holder.binding.lytThumbsUp.setOnClickListener(view ->
                anInterface.likeClicked(finalAuthor, item, finalInteractionId));

        holder.binding.lytThumbsDown.setOnClickListener(view ->
                anInterface.dislikeClicked(finalAuthor, item, finalInteractionId));

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
                                        @Nullable String status, String workflowKey) {
        if (authorName == null) return null;
        if (workflowKey == null) workflowKey = "";

        String text;
        if (status == null) {
            text = context.getString(stringRes, authorName, workflowKey);
        } else {
            text = context.getString(stringRes, authorName, status, workflowKey);
        }

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

    private Spannable getSpannableTitle(@StringRes int stringRes, String authorName,
                                        String workflowKey) {
        return getSpannableTitle(stringRes, authorName, null, workflowKey);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    private String getWorkflowDescription(TimelineItem item) {
        String workflowType = "";
        if (item.getDescription().getArguments().getWorkflowType() != null) {
            workflowType = item.getDescription().getArguments().getWorkflowType()
                    .getName();
        }

        int remainingTime = item.getDescription().getArguments().getRemainingTime();
        boolean isOutOfTime = remainingTime < 0;
        remainingTime = Math.abs(remainingTime);

        String remainingTimeString;

        Duration duration = Duration.ofMillis(remainingTime);

        int days = (int) duration.toDays();
        duration = duration.minusDays(days);
        int hours = (int) duration.toHours();

        String daysString = context.getResources().getQuantityString(
                R.plurals.timeline_description_out_of_time_days,
                days,
                days);

        String hoursString = context.getResources().getQuantityString(
                R.plurals.timeline_description_out_of_time_hours,
                hours,
                hours);

        String formattedTime = String.format(Locale.US, "%s, %s", daysString, hoursString);

        if (isOutOfTime) {
            if (days == 0) {
                remainingTimeString = context.getString(
                        R.string.timeline_description_workflow_created_out_of_time,
                        hoursString
                );
            } else {
                remainingTimeString = context.getString(
                        R.string.timeline_description_workflow_created_out_of_time,
                        formattedTime
                );
            }
        } else {
            remainingTimeString = formattedTime;
        }

        String startDate;
        if (item.getDescription().getArguments().getStart() != null) {
            startDate = Utils
                    .getFormattedDate(item.getDescription().getArguments().getStart(),
                            "yyyy-MM-dd hh:mm", Utils.SHORT_DATE_DISPLAY_FORMAT);
        } else {
            startDate = Utils.serverFormatToFormat(item.getCreatedAt(),
                    Utils.SHORT_DATE_DISPLAY_FORMAT);
        }

        String currentStatus = "";
        if (item.getDescription().getArguments().getCurrentStatus() != null) {
            currentStatus = item.getDescription().getArguments().getCurrentStatus()
                    .getName();
        }

        return context.getString(
                R.string.timeline_description_workflow_created,
                workflowType,
                item.getDescription().getArguments().getDescription(),
                startDate,
                remainingTimeString,
                currentStatus
        );
    }
}
