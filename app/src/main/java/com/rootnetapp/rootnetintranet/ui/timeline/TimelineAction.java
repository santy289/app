package com.rootnetapp.rootnetintranet.ui.timeline;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineAction.WORKFLOW_COMMENT_CREATED;
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineAction.WORKFLOW_CREATED;
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineAction.WORKFLOW_FILE_RECORD_CREATED;
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineAction.WORKFLOW_STATUS_APPROVED_CREATED;
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineAction.WORKFLOW_STATUS_APPROVED_UPDATED;
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineAction.WORKFLOW_UPDATED;

/**
 * Defines the possible timeline actions.
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({
        WORKFLOW_CREATED,
        WORKFLOW_UPDATED,
        WORKFLOW_STATUS_APPROVED_CREATED,
        WORKFLOW_STATUS_APPROVED_UPDATED,
        WORKFLOW_FILE_RECORD_CREATED,
        WORKFLOW_COMMENT_CREATED
})
public @interface TimelineAction {

    String WORKFLOW_CREATED = "TIMELINE_WORKFLOW_CREATED";
    String WORKFLOW_UPDATED = "TIMELINE_WORKFLOW_UPDATED";
    String WORKFLOW_STATUS_APPROVED_CREATED = "TIMELINE_WORKFLOW_STATUS_APPROVE_CREATED";
    String WORKFLOW_STATUS_APPROVED_UPDATED = "TIMELINE_WORKFLOW_STATUS_APPROVE_UPDATED";
    String WORKFLOW_FILE_RECORD_CREATED = "TIMELINE_WORKFLOW_FILE_RECORD_CREATED";
    String WORKFLOW_COMMENT_CREATED = "TIMELINE_WORKFLOW_COMMENT_CREATED";
}
