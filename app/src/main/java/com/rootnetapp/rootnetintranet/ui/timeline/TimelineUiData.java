package com.rootnetapp.rootnetintranet.ui.timeline;

import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Interaction;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUser;

import java.util.List;

class TimelineUiData {
    private List<TimelineItem> timelineItems;
    private List<User> users;
    private List<WorkflowUser> workflowUsers;
    private List<Interaction> interactionComments;

    protected List<TimelineItem> getTimelineItems() {
        return timelineItems;
    }

    protected void setTimelineItems(
            List<TimelineItem> timelineItems) {
        this.timelineItems = timelineItems;
    }

    protected List<User> getUsers() {
        return users;
    }

    protected void setUsers(List<User> users) {
        this.users = users;
    }

    protected List<WorkflowUser> getWorkflowUsers() {
        return workflowUsers;
    }

    protected void setWorkflowUsers(
            List<WorkflowUser> workflowUsers) {
        this.workflowUsers = workflowUsers;
    }

    protected List<Interaction> getInteractionComments() {
        return interactionComments;
    }

    protected void setInteractionComments(
            List<Interaction> interactionComments) {
        this.interactionComments = interactionComments;
    }
}
