package com.rootnetapp.rootnetintranet.ui.timeline;

import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;

import androidx.annotation.Nullable;

/**
 * Created by root on 12/04/18.
 */

public interface TimelineInterface {

    void setDate(String start, String end);

    void addCommentClicked(String comment, User author, TimelineItem timelineItem,
                           @Nullable Integer interactionId);

    void likeClicked(User author, TimelineItem timelineItem, @Nullable Integer interactionId);

    void dislikeClicked(User author, TimelineItem timelineItem, @Nullable Integer interactionId);

    void showWorkflowDetails(int workflowId);

    void showToastMessage(int stringRes);
}
